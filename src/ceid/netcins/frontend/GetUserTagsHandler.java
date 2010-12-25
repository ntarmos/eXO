package ceid.netcins.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.p2p.commonapi.Id;
import ceid.netcins.CatalogService;
import ceid.netcins.social.TagCloud;

public class GetUserTagsHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = 7508245962714729908L;

	public GetUserTagsHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (prepare(request, response) == JobStatus.FINISHED)
			return;
		Map<Id, TagCloud> userTags = catalogService.getUser().getUserTagClouds();
		sendStatus(response, RequestSuccess, (userTags != null) ? userTags :new HashMap<String, String>());
	}
}
