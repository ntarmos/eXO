package ceid.netcins.exo.frontend.handlers;

import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.p2p.past.PastException;
import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.content.ContentProfile;

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
public class GetContentTagsHandler extends AbstractHandler {

	private static final long serialVersionUID = -358145592191291166L;

	public GetContentTagsHandler(CatalogService catalogService,
			Hashtable<String, Hashtable<String, Object>> queue) {
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
