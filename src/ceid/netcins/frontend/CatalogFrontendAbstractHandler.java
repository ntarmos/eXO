package ceid.netcins.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ceid.netcins.CatalogService;
import ceid.netcins.frontend.json.ContentProfileJSONConvertor;

public abstract class CatalogFrontendAbstractHandler extends HttpServlet {
	private static final long serialVersionUID = -5657532444852074783L;
	protected static final String ReqIDTag = "eXO::reqID";
	protected static final String UIDTag = "eXO::UID";
	protected static final String CIDTag = "eXO::CID";
	protected static final String ProfileTag = ContentProfileJSONConvertor.ProfileTag;
	protected static final String PostParamTag = "eXO_data";
	private static final String RequestStatusTag = "eXO::Status";
	private static final String RequestStatusSuccessTag = "eXO::Success";
	protected static final HashMap<String, String> RequestSuccess = new HashMap<String, String>();
	private static final String RequestStatusFailureTag = "eXO::Failure";
	protected static final HashMap<String, String> RequestFailure = new HashMap<String, String>();
	private static final String RequestStatusProcessingTag = "eXO::Processing";
	protected static final HashMap<String, String> RequestProcessing = new HashMap<String, String>();
	private static final String RequestStatusUnknownTag = "eXO::Unknown";
	protected static final HashMap<String, String> RequestUnknown = new HashMap<String, String>();
	protected static final String DefaultEncoding = "UTF-8";

	protected CatalogService catalogService = null;
	protected Hashtable<String, Object> queue = null;

	public CatalogFrontendAbstractHandler(CatalogService catalogService, Hashtable<String, Object> queue) {
		RequestSuccess.put(RequestStatusTag, RequestStatusSuccessTag);
		RequestFailure.put(RequestStatusTag, RequestStatusFailureTag);
		RequestProcessing.put(RequestStatusTag, RequestStatusProcessingTag);
		RequestUnknown.put(RequestStatusTag, RequestStatusUnknownTag);
		this.catalogService = catalogService;
		this.queue = queue;
	}

	// TODO: We only want POST access; remove this method when RnD is over.
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}
}
