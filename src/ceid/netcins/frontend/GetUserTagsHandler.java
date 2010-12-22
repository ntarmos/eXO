package ceid.netcins.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import rice.p2p.commonapi.Id;
import ceid.netcins.CatalogService;
import ceid.netcins.json.Json;
import ceid.netcins.social.TagCloud;

public class GetUserTagsHandler extends CatalogFrontendAbstractHandler {
	public GetUserTagsHandler(CatalogService catalogService, Hashtable<String, Object> queue) {
		super(catalogService, queue);
	}

	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		Map<Id, TagCloud> userTags = catalogService.getUser().getUserTagClouds();
		if (userTags == null) {
			response.getWriter().write(Json.toString(new HashMap<String, String>()));
			return;
		}

		response.getWriter().write(Json.toString(userTags));
	}
}
