package ceid.netcins.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentProfile;

public class GetUserProfileHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = 2401227782075291999L;

	public GetUserProfileHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (prepare(request, response) == JobStatus.FINISHED)
			return;

		if (uid == null) { // Local operation. Return immediately.
			ContentProfile userProfile = catalogService.getUserProfile();
			if (userProfile != null)
				sendStatus(response, RequestSuccess, userProfile);
			else
				sendStatus(response, RequestFailure);
			return;
		}

		final String reqID = getNewReqID(response);
		try {
			catalogService.getUserProfile(uid, new Continuation<Object, Exception>() {

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
	}
}
