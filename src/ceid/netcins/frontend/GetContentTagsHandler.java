package ceid.netcins.frontend;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.p2p.past.PastException;
import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentProfile;

public class GetContentTagsHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = -358145592191291166L;

	public GetContentTagsHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (prepare(request, response) == JobStatus.FINISHED)
			return;

		if (cid == null) {
			sendStatus(response, RequestFailure);
			return;
		}

		if (uid == null) { // Local resource. Return immediately.
			ContentProfile cp = catalogService.getUser().getSharedContentProfile(cid);
			if (cp != null) {
				sendStatus(response, RequestSuccess, cp);
				return;
			}
			sendStatus(response, RequestFailure);
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

					Vector<Object> res = new Vector<Object>();
					res.add(RequestSuccess);
					res.add(((ContentProfile)result));
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
