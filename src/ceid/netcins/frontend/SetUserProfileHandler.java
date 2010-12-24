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
import ceid.netcins.json.ContentProfileJSONConvertor;
import ceid.netcins.json.Json;
import ceid.netcins.user.User;

public class SetUserProfileHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = 5124127253356875812L;

	public SetUserProfileHandler(CatalogService catalogService, Hashtable<String, Object> queue) {
		super(catalogService, queue);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
				if (((Map)jsonParams).containsKey(ReqIDTag)) {
					String reqID = (String)((Map)jsonParams).get(ReqIDTag);
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
				} else if (jsonMap.containsKey(UIDTag)) {
					final String reqID = Integer.toString(CatalogFrontend.nextReqID());
					Vector<Object> na = new Vector<Object>();
					na.add(RequestProcessing);
					queue.put(reqID, na);
					Map<String, String> ret = new HashMap<String, String>();
					ret.put(ReqIDTag, reqID);
					response.getWriter().write(Json.toString(ret));
					String UID = (String)jsonMap.get(UIDTag);
					ContentProfileJSONConvertor cpj = new ContentProfileJSONConvertor();
					ContentProfile profile = (ContentProfile)cpj.fromJSON(jsonMap);
					try {
						catalogService.tagUser(Id.build(UID), profile, null, new Continuation<Object, Exception>() {

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
						e.printStackTrace();
						res.add(RequestFailure);
						queue.put(reqID, res);
					}
					return;
				}

				ContentProfileJSONConvertor cc = new ContentProfileJSONConvertor();
				ContentProfile profile = (ContentProfile)cc.fromJSON(jsonMap);
				final User user = catalogService.getUser();
				ContentProfile oldProfile = new ContentProfile(user.getCompleteUserProfile());
				user.setUserProfile(profile);
				if (!oldProfile.equalsPublic(profile)) {
					final String reqID = Integer.toString(CatalogFrontend.nextReqID());
					Vector<Object> na = new Vector<Object>();
					na.add(RequestProcessing);
					queue.put(reqID, na);
					Map<String, String> ret = new HashMap<String, String>();
					ret.put(ReqIDTag, reqID);
					response.getWriter().write(Json.toString(ret));
					// The public part has changed. We should reindex the user profile in the network
					catalogService.indexUser(new Continuation<Object, Exception>() {
						public void receiveResult(Object result) {
							System.out.println("SUPH: User : " + user.getUID()
									+ ", indexed successfully");
							// TODO : Check the replicas if are updated correctly!
							// run replica maintenance
							// runReplicaMaintence();
							int indexedNum = 0;
							Vector<Object> res = new Vector<Object>();
							res.add(RequestSuccess);
							if (result instanceof Boolean[]) {
								Boolean[] results = (Boolean[]) result;
								if (results != null)
									for (Boolean isIndexedTerm : results) {
										if (isIndexedTerm)
											indexedNum++;
										res.add(Boolean.toString(isIndexedTerm));
									}
								System.out.println("Total " + indexedNum
										+ " terms indexed out of " + results.length
										+ "!");
							}
							queue.put(reqID, res);
						}

						public void receiveException(Exception result) {
							System.out.println("User : " + user.getUID()
									+ ", indexed with errors : "
									+ result.getMessage());
							Vector<Object> res = new Vector<Object>();
							res.add(RequestFailure);
							queue.put(reqID, res);
						}
					});
				} else {
					Vector<Object> res = new Vector<Object>();
					res.add(RequestSuccess);
					response.getWriter().write(Json.toString(res));
				}
				return;
			}
		}
		Vector<Object> res = new Vector<Object>();
		res.add(RequestFailure);
		response.getWriter().write(Json.toString(res));
	}
}
