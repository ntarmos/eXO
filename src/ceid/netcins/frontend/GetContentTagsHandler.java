package ceid.netcins.frontend;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.p2p.past.PastException;
import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentProfile;

public class GetContentTagsHandler extends AbstractHandler {

	private static final long serialVersionUID = -358145592191291166L;

	public GetContentTagsHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;

		if (cid == null) {
			sendStatus(response, RequestStatus.FAILURE, null);
			return;
		}

		if (uid == null) { // Local resource. Return immediately.
			ContentProfile cp = catalogService.getUser().getSharedContentProfile(cid);
			if (cp != null) {
				sendStatus(response, RequestStatus.SUCCESS, cp);
				return;
			}
			sendStatus(response, RequestStatus.FAILURE, null);
			return;
		}

		// Search for it in the network
		final String reqID = getNewReqID(response);
		try {
			catalogService.retrieveContentTags(uid, cid,
					new Continuation<Object, Exception>() {
				@Override
				public void receiveResult(Object result) {
					if (result == null || !(result instanceof ContentProfile))
						receiveException(new PastException("Result was null or of wrong type"));
					queueStatus(reqID, RequestStatus.SUCCESS, result);
				}

				@Override
				public void receiveException(Exception exception) {
					queueStatus(reqID, RequestStatus.FAILURE, null);
				}
			});
		} catch (Exception e) {
			queueStatus(reqID, RequestStatus.FAILURE, null);
		}
	}
}
