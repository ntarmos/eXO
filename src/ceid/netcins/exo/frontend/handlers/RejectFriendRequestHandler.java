package ceid.netcins.exo.frontend.handlers;

import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.p2p.commonapi.Id;
import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.user.FriendRequest;

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
public class RejectFriendRequestHandler extends FriendRequestBaseHandler {
	private static final long serialVersionUID = -3228871623527689289L;

	public RejectFriendRequestHandler(CatalogService catalogService,
			Hashtable<String, Hashtable<String, Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;
		final Hashtable<Id, FriendRequest> fr = catalogService.getUser().getPendingIncomingFReq();
		if (!fr.containsKey(uid)) {
			sendStatus(response, RequestStatus.FAILURE, null);
		}
		catalogService.rejectFriend(fr.get(uid), frMsg, command);
	}
}
