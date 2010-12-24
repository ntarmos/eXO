package ceid.netcins.frontend;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.pastry.Id;
import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.json.Json;

public class GetUserProfileHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = 2401227782075291999L;

	public GetUserProfileHandler(CatalogService catalogService, Hashtable<String, Object> queue) {
		super(catalogService, queue);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		String param = request.getParameter(PostParamTag);
		if (param != null) {
			param = URLDecoder.decode(param, DefaultEncoding);
			Object jsonParams = null;
			try {
				jsonParams = Json.parse(param);
			} catch (IllegalStateException e) {
				Vector<Object> res = new Vector<Object>();
				res.add(RequestFailure);
				response.getWriter().write(Json.toString(res.toArray()));
				System.err.println("Error parsing JSON request");
				return;
			}
			if (jsonParams instanceof Map) {
				Map jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					String reqID = (String)jsonMap.get(ReqIDTag);
					@SuppressWarnings("unchecked")
					Vector<Object> res = (Vector<Object>) queue.get(reqID);
					if (res == null) {
						response.getWriter().write(Json.toString(new Map[] { RequestUnknown }));
						return;
					} else if (res.get(0).equals(RequestProcessing)) {
						response.getWriter().write(Json.toString(new Map[] { RequestProcessing }));
						return;
					}
					response.getWriter().write(Json.toString(res.toArray()));
					response.flushBuffer();
					queue.remove(reqID);
					return;
				} else if (jsonMap.containsKey(UIDTag)) {
					final String reqID = Integer.toString(CatalogFrontend.nextReqID());
					Vector<Object> na = new Vector<Object>();
					na.add(RequestProcessing);
					queue.put(reqID, na);
					Map<String, String> ret = new HashMap<String, String>();
					ret.put(ReqIDTag, reqID);
					response.getWriter().write(Json.toString(ret));
					String UID = (String)jsonMap.get(UIDTag);
					try {
						catalogService.getUserProfile(Id.build(UID), new Continuation<Object, Exception>() {

							@SuppressWarnings("unchecked")
							@Override
							public void receiveResult(Object result) {
								HashMap<String, Object> resMap = (HashMap<String, Object>)result;
								if (!((Integer)resMap.get("status")).equals(CatalogService.SUCCESS))
									receiveException(new RuntimeException());

								Vector<Object> res = new Vector<Object>();
								res.add(RequestSuccess);
								ContentProfile cp = (ContentProfile)resMap.get("data");
								res.add(cp);
								queue.put(reqID, res);
							}

							@Override
							public void receiveException(Exception exception) {
								Vector<Object> res = new Vector<Object>();
								res.add(RequestFailure);
								queue.put(reqID, res);
							}
						});
					} catch (Exception e) {
						Vector<Object> res = new Vector<Object>();
						res.add(RequestFailure);
						queue.put(reqID, res);
					}
					return;
				}
			}
		}
		ContentProfile userProfile = catalogService.getUserProfile();
		System.err.println("Returning profile for user: " + catalogService.getUser().getUID().toStringFull());
		Vector<Object> res = new Vector<Object>();
		if (userProfile != null) {
			res.add(RequestSuccess);
			res.add(userProfile);
		} else {
			res.add(RequestFailure);
		}
		response.getWriter().write(Json.toString(res.toArray()));
		response.flushBuffer();
	}
}
