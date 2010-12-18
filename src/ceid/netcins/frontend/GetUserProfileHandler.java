package ceid.netcins.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.ajax.JSON;

import rice.Continuation;
import rice.pastry.Id;

import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentField;
import ceid.netcins.content.ContentProfile;

public class GetUserProfileHandler extends CatalogFrontendAbstractHandler {
	public GetUserProfileHandler(CatalogService catalogService, Hashtable<String, Vector<String>> queue) {
		super(catalogService, queue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		String param = request.getParameter(PostParamTag);
		if (param != null) {
			Object jsonParams = JSON.parse(param);
			if (jsonParams instanceof Map) {
				Map jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					String reqID = (String)jsonMap.get(ReqIDTag);
					Vector<String> res = queue.get(reqID);
					if (res == null || res.get(0).equals(RequestStatusProcessingTag)) {
						Map<String, String> ret = new HashMap<String, String>();
						ret.put(RequestStatusTag, RequestStatusProcessingTag);
						response.getWriter().write(JSON.toString(ret));
						response.flushBuffer();
						return;
					}
					response.getWriter().write(JSON.toString(res.toArray()));
					response.flushBuffer();
					queue.remove(reqID);
					baseRequest.setHandled(true);
					return;
				} else if (jsonMap.containsKey(UIDTag)) {
					final String reqID = Integer.toString(CatalogFrontend.nextReqID());
					Vector<String> na = new Vector<String>();
					na.add(RequestStatusProcessingTag);
					queue.put(reqID, na);
					Map<String, String> ret = new HashMap<String, String>();
					ret.put(ReqIDTag, reqID);
					response.getWriter().write(JSON.toString(ret));
					String UID = (String)jsonMap.get(UIDTag);
					try {
						catalogService.getUserProfile(Id.build(UID), new Continuation<Object, Exception>() {

							@Override
							public void receiveResult(Object result) {
								HashMap<String, Object> resMap = (HashMap<String, Object>)result;
								if (!((Integer)resMap.get("status")).equals(CatalogService.SUCCESS))
									receiveException(new RuntimeException());

								Vector<String> res = new Vector<String>();
								res.add(RequestStatusSuccessTag);
								ContentProfile cp = (ContentProfile)resMap.get("data");
								res.add(JSON.toString(cp));
								queue.put(reqID, res);
							}

							@Override
							public void receiveException(Exception exception) {
								Vector<String> res = new Vector<String>();
								res.add(RequestStatusFailureTag);
								queue.put(reqID, res);
							}
						});
					} catch (Exception e) {
						Vector<String> res = new Vector<String>();
						res.add(RequestStatusFailureTag);
						queue.put(reqID, res);
					}
					return;
				}
			}
		}
		ContentProfile userProfile = catalogService.getUserProfile();
		System.err.println("Returning profile for user: " + catalogService.getUser().getUID().toStringFull());
		if (userProfile != null) {
			List<ContentField> cflist = userProfile.getAllFields();
			JSON json = new JSON();
			StringBuffer sb = new StringBuffer();
			json.appendArray(sb, cflist.toArray());
			response.getWriter().write(sb.toString());
		} else {
			response.getWriter().write(JSON.toString(new HashMap<String, String>()));
		}
		response.flushBuffer();
	}
}