package ceid.netcins.frontend;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.p2p.commonapi.Id;
import rice.p2p.past.PastException;
import ceid.netcins.CatalogService;

public class GetContentIDsHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = 2066271262351320193L;

	public GetContentIDsHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (prepare(request, response) == JobStatus.FINISHED)
			return;

		// If local request, return immediately
		if (uid == null) {
			Set<Id> contentIDs = catalogService.getUser().getSharedContentIDs();
			sendStatus(response, RequestSuccess, contentIDs.toArray());
			return;
		}

		// Search for it in the network
		final String reqID = getNewReqID(response);
		try {
			catalogService.retrieveContentIDs(uid,
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
