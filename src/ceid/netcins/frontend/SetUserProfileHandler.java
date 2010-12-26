package ceid.netcins.frontend;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.frontend.json.ContentProfileJSONConvertor;
import ceid.netcins.user.User;

public class SetUserProfileHandler extends AbstractHandler {

	private static final long serialVersionUID = 5124127253356875812L;

	public SetUserProfileHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;

		ContentProfileJSONConvertor cpj = new ContentProfileJSONConvertor();
		ContentProfile profile = (ContentProfile)cpj.fromJSON(jsonMap);

		if (uid == null) { // Local operation
			final User user = catalogService.getUser();
			ContentProfile oldProfile = new ContentProfile(user.getCompleteUserProfile());
			user.setUserProfile(profile);
			if (!oldProfile.equalsPublic(profile)) {
				final String reqID = getNewReqID(response);
				// The public part has changed. We should reindex the user profile in the network
				catalogService.indexUser(new Continuation<Object, Exception>() {
					public void receiveResult(Object result) {
						System.out.println("SUPH: User : " + user.getUID()
								+ ", indexed successfully");
						// TODO : Check the replicas if are updated correctly!
						// run replica maintenance
						// runReplicaMaintence();
						int indexedNum = 0;
						Boolean[] results = null;
						if (result instanceof Boolean[]) {
							results = (Boolean[]) result;
							if (results != null)
								for (Boolean isIndexedTerm : results) {
									if (isIndexedTerm)
										indexedNum++;
								}
							System.out.println("Total " + indexedNum
									+ " terms indexed out of " + results.length
									+ "!");
						}
						queueStatus(reqID, RequestStatus.SUCCESS, results);
					}

					public void receiveException(Exception result) {
						System.out.println("User : " + user.getUID()
								+ ", indexed with errors : "
								+ result.getMessage());
						queueStatus(reqID, RequestStatus.FAILURE, null);
					}
				});
			} else {
				sendStatus(response, RequestStatus.SUCCESS, null);
			}
			return;
		}

		// Search for the user in the network
		final String reqID = getNewReqID(response);
		try {
			catalogService.tagUser(uid, profile, null, new Continuation<Object, Exception>() {

				@Override
				public void receiveResult(Object result) {
					HashMap<String, Object> resMap = (HashMap<String, Object>)result;
					if (!((Integer)resMap.get("status")).equals(CatalogService.SUCCESS))
						receiveException(new RuntimeException());
					queueStatus(reqID, RequestStatus.SUCCESS, resMap.get("data"));
				}

				@Override
				public void receiveException(Exception exception) {
					queueStatus(reqID, RequestStatus.FAILURE, null);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			queueStatus(reqID, RequestStatus.FAILURE, null);
		}
	}
}
