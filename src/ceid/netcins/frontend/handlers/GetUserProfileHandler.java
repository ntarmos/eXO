package ceid.netcins.frontend.handlers;

import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentProfile;

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
public class GetUserProfileHandler extends AbstractHandler {

	private static final long serialVersionUID = 2401227782075291999L;

	public GetUserProfileHandler(CatalogService catalogService,
			Hashtable<String, Hashtable<String, Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;

		if (uid == null) { // Local operation. Return immediately.
			ContentProfile userProfile = catalogService.getUserProfile();
			if (userProfile != null)
				sendStatus(response, RequestStatus.SUCCESS, userProfile);
			else
				sendStatus(response, RequestStatus.FAILURE, null);
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
					queueStatus(reqID, RequestStatus.SUCCESS, resMap.get("data"));
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
