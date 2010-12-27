package ceid.netcins.frontend;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import rice.p2p.commonapi.Id;
import ceid.netcins.CatalogService;
import ceid.netcins.user.FriendRequest;

public class AcceptFriendRequestHandler extends AbstractHandler {
	private static final long serialVersionUID = -5852818920517847654L;
	public static final String FriendMessageTag = "eXO::FriendMessage";

	public AcceptFriendRequestHandler(CatalogService catalogService,
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
			catalogService.acceptFriend(fr.get(uid), msg, 
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
