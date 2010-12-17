package ceid.netcins.frontend;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.ajax.JSON;

import ceid.netcins.CatalogService;
import ceid.netcins.content.ContentField;
import ceid.netcins.content.ContentProfile;

public class GetUserProfileHandler extends CatalogFrontendAbstractHandler {
	public GetUserProfileHandler(CatalogService catalogService, Hashtable<String, Vector<String>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		ContentProfile userProfile = catalogService.getUserProfile();
		if (userProfile != null) {
			List<ContentField> cflist = userProfile.getAllFields();
			JSON json = new JSON();
			StringBuffer sb = new StringBuffer();
			json.appendArray(sb, cflist.toArray());
			response.getWriter().write(sb.toString());
		} else {
			response.getWriter().println("{}");
		}
		response.flushBuffer();
	}
}
