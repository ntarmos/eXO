package ceid.netcins.frontend;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import rice.p2p.commonapi.Id;

import ceid.netcins.CatalogService;
import ceid.netcins.json.JSON;
import ceid.netcins.user.Friend;

public class GetFriendUIDsHandler extends CatalogFrontendAbstractHandler {

	public GetFriendUIDsHandler(CatalogService catalogService,
			Hashtable<String, Vector<String>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		List<Friend> friends = catalogService.getUser().getFriends();
		HashSet<Id> friendIDs = new HashSet<Id>();
		if (friends != null)
			for (Friend fr : friends)
				friendIDs.add(fr.getUID());

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().write(JSON.toString(friendIDs));
	}
}
