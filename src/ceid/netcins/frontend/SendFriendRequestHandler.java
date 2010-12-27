package ceid.netcins.frontend;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import ceid.netcins.CatalogService;

public class SendFriendRequestHandler extends AbstractHandler {
	private static final long serialVersionUID = -5535744211758924495L;
	public static final String FriendMessageTag = "eXO::FriendMessage";

	public SendFriendRequestHandler(CatalogService catalogService,
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
		if (msg == null)
			msg = "";
		final String reqID = getNewReqID(response);
		try {
			catalogService.friendRequest(uid, msg, 
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
