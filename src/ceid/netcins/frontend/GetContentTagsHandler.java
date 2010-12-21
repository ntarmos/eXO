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

import rice.Continuation;
import rice.pastry.Id;
import ceid.netcins.CatalogService;
import ceid.netcins.catalog.ScoreBoard;
import ceid.netcins.content.ContentField;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.json.Json;
import ceid.netcins.messages.ResponsePDU;

public class GetContentTagsHandler extends CatalogFrontendAbstractHandler {
	public GetContentTagsHandler(CatalogService catalogService, Hashtable<String, Object> queue) {
		super(catalogService, queue);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		String CID = null, UID = null;
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		String param = request.getParameter(PostParamTag);
		if (param != null) {
			Object jsonParams = Json.parse(param);
			if (jsonParams instanceof Map) {
				Map jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					String reqID = (String)jsonMap.get(ReqIDTag);
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
					baseRequest.setHandled(true);
					return;
				} else {
					if (jsonMap.containsKey(CIDTag))
						CID = (String)jsonMap.get(CIDTag);
					if (jsonMap.containsKey(UIDTag))
						UID = (String)jsonMap.get(UIDTag);
				}
			}
		}

		if (CID == null) {
			response.getWriter().write(Json.toString(new HashMap<String, String>()));
			return;
		}

		ContentProfile cp = catalogService.getUser().getSharedContentProfile().get(Id.build(CID));
		if (UID == null) { // Local resource. Return immediately.
			if (cp != null) {
				List<ContentField> cflist = cp.getAllFields();
				Json json = Json.getInstance();
				StringBuffer sb = new StringBuffer();
				json.appendArray(sb, cflist.toArray());
				response.getWriter().write(sb.toString());
				return;
			}
			response.getWriter().write(Json.toString(new HashMap<String, String>()));
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
			catalogService.searchQuery(CatalogService.CONTENT, CID, 1,
					new Continuation<Object, Exception>() {
				@Override
				public void receiveResult(Object result) {
					ScoreBoard sb = null;
					if (result instanceof ResponsePDU) {
						ResponsePDU pdu = (ResponsePDU)result;
						sb = pdu.getScoreBoard();
					}

					/*
					HashMap<String, Object> resMap = (HashMap<String, Object>)result;
					if (!((Integer)resMap.get("status")).equals(CatalogService.SUCCESS))
						receiveException(new RuntimeException());
					 */

					Vector<String> res = new Vector<String>();
					res.add(RequestStatusSuccessTag);
					if (sb != null)
						res.add(Json.toString(sb));
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
