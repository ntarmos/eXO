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
import ceid.netcins.content.StoredField;
import ceid.netcins.content.TermField;
import ceid.netcins.frontend.json.ContentProfileJSONConvertor;
import ceid.netcins.frontend.json.Json;

public class SetContentTagsHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = -565717952819033549L;

	public SetContentTagsHandler(CatalogService catalogService, Hashtable<String, Object> queue) {
		super(catalogService, queue);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String CID = null, UID = null;
		Map jsonMap = null;
		ContentProfile profile = null;
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		String param = URLDecoder.decode(request.getParameter(PostParamTag), "UTF-8");
		if (param != null) {
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
				jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					String reqID = (String)jsonMap.get(ReqIDTag);
					Vector<Object> res = (Vector<Object>)queue.get(reqID);
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
				} else {
					if (jsonMap.containsKey(CIDTag))
						CID = (String)jsonMap.get(CIDTag);
					if (jsonMap.containsKey(UIDTag))
						UID = (String)jsonMap.get(UIDTag);
					ContentProfileJSONConvertor cpj = new ContentProfileJSONConvertor();
					profile = (ContentProfile)cpj.fromJSON(jsonMap);					
				}
			} else {
				Vector<Object> res = new Vector<Object>();
				res.add(RequestFailure);
				response.getWriter().write(Json.toString(res.toArray()));
				return;
			}
		} else {
			Vector<Object> res = new Vector<Object>();
			res.add(RequestFailure);
			response.getWriter().write(Json.toString(res.toArray()));
			return;			
		}

		if (CID == null || profile == null) {
			Vector<Object> res = new Vector<Object>();
			res.add(RequestFailure);
			response.getWriter().write(Json.toString(res.toArray()));
			return;
		}

		final String reqID = Integer.toString(CatalogFrontend.nextReqID());
		Vector<Object> na = new Vector<Object>();
		na.add(RequestProcessing);
		queue.put(reqID, na);
		Map<String, String> ret = new HashMap<String, String>();
		ret.put(ReqIDTag, reqID);
		response.getWriter().write(Json.toString(ret));
		profile.add(new StoredField("SHA-1", CID));
		profile.add(new TermField("Identifier", CID));

		if (UID == null) { // Local resource.
			// New tag-less content item
			catalogService.indexPseudoContent(profile, new Continuation<Object, Exception>() {
				@Override
				public void receiveResult(Object result) {
					if (!(result instanceof Boolean[]))
						receiveException(null);
					Boolean[] resBool = (Boolean[])result;
					boolean didit = false;
					for (int i = 0; i < resBool.length && !didit; i++)
						didit = resBool[i];

					Vector<Object> res = new Vector<Object>();
					if (didit)
						res.add(RequestSuccess);
					else
						res.add(RequestFailure);
					queue.put(reqID, res);
				}

				@Override
				public void receiveException(Exception exception) {
					Vector<Object> res = new Vector<Object>();
					res.add(RequestFailure);
					queue.put(reqID, res);
				}
			});
			response.flushBuffer();
			return;
		}

		// Search for it in the network
		try {
			catalogService.tagContent(Id.build(UID), Id.build(CID), profile, 
					new Continuation<Object, Exception>() {
				@Override
				public void receiveResult(Object result) {
					HashMap<String, Object> resMap = (HashMap<String, Object>)result;
					if (!((Integer)resMap.get("status")).equals(CatalogService.SUCCESS))
						receiveException(new RuntimeException());

					Vector<Object> res = new Vector<Object>();
					res.add(RequestSuccess);
					res.add(resMap.get("data"));
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
