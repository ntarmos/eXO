package ceid.netcins.frontend.handlers;

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

/**
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 * 
 */
public class GetContentIDsHandler extends AbstractHandler {

	private static final long serialVersionUID = 2066271262351320193L;

	public GetContentIDsHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;

		// If local request, return immediately
		if (uid == null) {
			Set<Id> contentIDs = catalogService.getUser().getSharedContentIDs();
			sendStatus(response, RequestStatus.SUCCESS, contentIDs.toArray());
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
					queueStatus(reqID, RequestStatus.SUCCESS, ((Vector<Id>)result).toArray());
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
