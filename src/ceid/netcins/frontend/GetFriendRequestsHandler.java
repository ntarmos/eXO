package ceid.netcins.frontend;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.p2p.commonapi.Id;
import ceid.netcins.CatalogService;

public class GetFriendRequestsHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = -7350932922284839640L;

	public GetFriendRequestsHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (prepare(request, response) == JobStatus.FINISHED)
			return;
		Set<Id> friendReqIDs = catalogService.getUser().getPendingIncomingFReq().keySet();
		sendStatus(response, RequestSuccess, friendReqIDs.toArray());
	}
}
