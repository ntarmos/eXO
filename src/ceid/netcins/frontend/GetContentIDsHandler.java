package ceid.netcins.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import rice.Continuation;
import rice.p2p.commonapi.Id;
import rice.p2p.past.PastException;
import ceid.netcins.CatalogService;
import ceid.netcins.json.Json;

public class GetContentIDsHandler extends CatalogFrontendAbstractHandler {

	public GetContentIDsHandler(CatalogService catalogService,
			Hashtable<String, Object> queue) {
		super(catalogService, queue);
	}

	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		String param = request.getParameter(PostParamTag);
		String UID = null;
		if (param != null) {
			Object jsonParams = Json.parse(param);
			if (jsonParams instanceof Map) {
				@SuppressWarnings("rawtypes")
				Map jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					String reqID = (String)jsonMap.get(ReqIDTag);
					@SuppressWarnings("unchecked")
					Vector<String> res = (Vector<String>)queue.get(reqID);
					if (res == null || res.get(0).equals(RequestStatusProcessingTag)) {
						Map<String, String> ret = new HashMap<String, String>();
						ret.put(RequestStatusTag, RequestStatusProcessingTag);
						response.getWriter().write(Json.toString(ret));
						response.flushBuffer();
						return;
					}
					response.getWriter().write(Json.toString(res.toArray()));
					response.flushBuffer();
					queue.remove(reqID);
					return;
				}
				if (jsonMap.containsKey(UIDTag))
					UID = (String)jsonMap.get(UIDTag);
			}
		}

		// If local request, return immediately
		if (UID == null) {
			Set<Id> contentIDs = catalogService.getUser().getSharedContentProfile().keySet();
			response.getWriter().write(Json.toString(contentIDs.toArray()));
			return;
		}

		// Search for it in the network
		final String reqID = Integer.toString(CatalogFrontend.nextReqID());
		Vector<String> na = new Vector<String>();
		na.add(RequestStatusProcessingTag);
		queue.put(reqID, na);
		Map<String, String> ret = new HashMap<String, String>();
		ret.put(ReqIDTag, reqID);
		response.getWriter().write(Json.toString(ret));
		try {
			catalogService.retrieveContentIDs(rice.pastry.Id.build(UID), 
					new Continuation<Object, Exception>() {
				@SuppressWarnings("unchecked")
				@Override
				public void receiveResult(Object result) {
					if (result == null || !(result instanceof Vector))
						receiveException(new PastException("Result was null or of wrong type"));

					Vector<Object> res = new Vector<Object>();
					res.add(RequestStatusSuccessTag);
					res.add(((Vector<Id>)result).toArray());
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
	}
}
