package ceid.netcins.exo.frontend.handlers;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.p2p.commonapi.Id;
import rice.p2p.past.PastException;
import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.user.SharedContentInfo;

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
			Hashtable<String, Hashtable<String, Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;

		// If local request, return immediately
		if (uid == null) {
			Map<Id, String> ret = new HashMap<Id, String>();
			Map<Id, SharedContentInfo> map = catalogService.getUser().getSharedContent();
			Iterator<Id> itid = map.keySet().iterator();
			Iterator<SharedContentInfo> itsci = map.values().iterator();
			while (itid.hasNext()) {
				ret.put(itid.next(), itsci.next().getFilename());
			}
			sendStatus(response, RequestStatus.SUCCESS, ret);
			return;
		}

		// Search for it in the network
		final String reqID = getNewReqID(response);
		try {
			catalogService.retrieveContentIDs(uid,
					new Continuation<Object, Exception>() {
				@Override
				public void receiveResult(Object result) {
					if (result == null || !(result instanceof Map)) {
						receiveException(new PastException("Result was null or of wrong type"));
						return;
					}
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
