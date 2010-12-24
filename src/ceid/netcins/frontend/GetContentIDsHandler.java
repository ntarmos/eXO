package ceid.netcins.frontend;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.p2p.commonapi.Id;
import rice.p2p.past.PastException;
import ceid.netcins.CatalogService;
import ceid.netcins.json.Json;

public class GetContentIDsHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = 2066271262351320193L;

	public GetContentIDsHandler(CatalogService catalogService,
			Hashtable<String, Object> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		String param = request.getParameter(PostParamTag);
		String UID = null;
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
				@SuppressWarnings("rawtypes")
				Map jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					String reqID = (String)jsonMap.get(ReqIDTag);
					@SuppressWarnings("unchecked")
					Vector<String> res = (Vector<String>)queue.get(reqID);
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
				}
				if (jsonMap.containsKey(UIDTag))
					UID = (String)jsonMap.get(UIDTag);
			}
		}

		// If local request, return immediately
		if (UID == null) {
			Vector<Object> na = new Vector<Object>();
			na.add(RequestSuccess);
			Set<Id> contentIDs = catalogService.getUser().getSharedContentIDs();
			na.add(contentIDs.toArray());
			response.getWriter().write(Json.toString(na.toArray()));
			return;
		}

		// Search for it in the network
		final String reqID = Integer.toString(CatalogFrontend.nextReqID());
		Vector<Object> na = new Vector<Object>();
		na.add(RequestProcessing);
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
					res.add(RequestSuccess);
					res.add(((Vector<Id>)result).toArray());
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
	}
}
