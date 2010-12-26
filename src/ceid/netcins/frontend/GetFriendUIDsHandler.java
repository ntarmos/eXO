package ceid.netcins.frontend;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.p2p.commonapi.Id;
import ceid.netcins.CatalogService;
import ceid.netcins.user.Friend;

public class GetFriendUIDsHandler extends AbstractHandler {

	private static final long serialVersionUID = 5244547683415352126L;

	public GetFriendUIDsHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;
		Hashtable<Id, Friend> friends = catalogService.getUser().getFriends();
		sendStatus(response, RequestStatus.SUCCESS, friends.keySet().toArray());
	}
}
