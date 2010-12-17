package ceid.netcins.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.ajax.JSON;

import rice.Continuation;
import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.content.TermField;
import ceid.netcins.user.User;

public class SetUserProfileHandler extends CatalogFrontendAbstractHandler {

	public SetUserProfileHandler(CatalogService catalogService, Hashtable<String, Vector<String>> queue) {
		super(catalogService, queue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
 		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		ContentProfile profile = new ContentProfile();
		Map<String, String[]> reqParams = request.getParameterMap();
		if (reqParams != null && reqParams.containsKey("eXO_data")) {
			Object jsonParams = JSON.parse(reqParams.get("eXO_data")[0]);
			if (jsonParams instanceof Map && ((Map)jsonParams).containsKey("reqID")) {
				String reqID = (String)((Map)jsonParams).get("reqID");
				Vector<String> res = queue.get(reqID);
				if (res == null || res.get(0).equals(PROCESSING)) {
					response.getWriter().println("{ \"status\": \"" + PROCESSING + "\" }");
					response.flushBuffer();
					return;
				}
				response.getWriter().write(JSON.toString(res.toArray()));
				response.flushBuffer();
				queue.remove(reqID);
				baseRequest.setHandled(true);
				return;
			}
			Object[] data = (Object[])jsonParams;
			if (data != null)
				for (Object cf : data) {
					TermField tf = new TermField(null, null);
					tf.fromJSON((Map)cf);
					profile.add(tf);
				}
			final User user = catalogService.getUser();
			ContentProfile oldProfile = new ContentProfile(user.getCompleteUserProfile());
			user.setUserProfile(profile);
			if (!oldProfile.equalsPublic(profile)) {
				final String reqID = Integer.toString(CatalogFrontend.nextReqID());
				Vector<String> na = new Vector<String>();
				na.add(PROCESSING);
				queue.put(reqID, na);
				Map<String, String> ret = new HashMap<String, String>();
				ret.put(CatalogFrontend.ReqIDTag, reqID);
				response.getWriter().write(JSON.toString(ret));
				// The public part has changed. We should reindex the user profile in the network
				catalogService.indexUser(new Continuation<Object, Exception>() {
					public void receiveResult(Object result) {
						System.out.println("SUPH: User : " + user.getUID()
								+ ", indexed successfully");
						// TODO : Check the replicas if are updated correctly!
						// run replica maintenance
						// runReplicaMaintence();
						int indexedNum = 0;
						Vector<String> res = new Vector();
						res.add(SUCCESS);
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
						Vector<String> res = new Vector();
						res.add(FAILURE);
						queue.put(reqID, res);
					}
				});
			}
		}
		response.flushBuffer();
	}
}