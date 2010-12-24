package ceid.netcins.frontend;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ceid.netcins.CatalogService;
import ceid.netcins.json.ContentProfileJSONConvertor;

public abstract class CatalogFrontendAbstractHandler extends HttpServlet {
	private static final long serialVersionUID = -5657532444852074783L;
	public static final String ReqIDTag = "eXO::reqID";
	public static final String UIDTag = "eXO::UID";
	public static final String CIDTag = "eXO::CID";
	public static final String ProfileTag = ContentProfileJSONConvertor.ProfileTag;
	public static final String PostParamTag = "eXO_data";
	public static final String RequestStatusTag = "eXO::Status";
	public static final String RequestStatusSuccessTag = "eXO::Success";
	public static final String RequestStatusFailureTag = "eXO::Failure";
	public static final String RequestStatusProcessingTag = "eXO::Processing";
	public static final String RequestStatusUnknownTag = "eXO::Unknown";

	protected CatalogService catalogService = null;
	protected Hashtable<String, Object> queue = null;

	public CatalogFrontendAbstractHandler(CatalogService catalogService, Hashtable<String, Object> queue) {
		this.catalogService = catalogService;
		this.queue = queue;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}
}
