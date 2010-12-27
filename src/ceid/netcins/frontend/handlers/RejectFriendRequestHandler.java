package ceid.netcins.frontend.handlers;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.p2p.commonapi.Id;
import ceid.netcins.CatalogService;
import ceid.netcins.user.FriendRequest;

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
public class RejectFriendRequestHandler extends AbstractHandler {
	private static final long serialVersionUID = -3228871623527689289L;
	public static final String FriendMessageTag = "eXO::FriendMessage";

	public RejectFriendRequestHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;
		if (uid == null)
			sendStatus(response, RequestStatus.FAILURE, null);
		String msg = (String)jsonMap.get(FriendMessageTag);
		final String reqID = getNewReqID(response);
		final Hashtable<Id, FriendRequest> fr = catalogService.getUser().getPendingIncomingFReq();
		if (!fr.containsKey(uid)) {
			sendStatus(response, RequestStatus.FAILURE, null);
		}
		try {
			catalogService.rejectFriend(fr.get(uid), msg, 
					new Continuation<Object, Exception>() {
				@Override
				public void receiveResult(Object result) {
					boolean didit = (result instanceof Boolean && (Boolean)result == true);
					queueStatus(reqID, didit ? RequestStatus.SUCCESS : RequestStatus.FAILURE, null);
				}

				@Override
				public void receiveException(Exception exception) {
					queueStatus(reqID, RequestStatus.FAILURE, null);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			sendStatus(response, RequestStatus.FAILURE, null);
		}
	}
}
