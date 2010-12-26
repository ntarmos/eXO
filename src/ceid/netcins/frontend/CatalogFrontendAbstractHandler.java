package ceid.netcins.frontend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.p2p.commonapi.Id;
import ceid.netcins.CatalogService;
import ceid.netcins.frontend.json.ContentProfileJSONConvertor;
import ceid.netcins.frontend.json.Json;

public abstract class CatalogFrontendAbstractHandler extends HttpServlet {
	private static final long serialVersionUID = -5657532444852074783L;

	protected static final long DefaultSleepTime = 3000;
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

	protected static enum JobStatus { LOCAL, REMOTE, FINISHED };

	protected CatalogService catalogService = null;
	protected Hashtable<String, Vector<Object>> queue = null;
	protected long sleepTime;

	@SuppressWarnings("rawtypes")
	protected Map jsonMap = null;
	protected Id uid = null;
	protected Id cid = null;

	public CatalogFrontendAbstractHandler(CatalogService catalogService, Hashtable<String, Vector<Object>> queue, long sleepTime) {
		this.catalogService = catalogService;
		this.queue = queue;
		this.sleepTime = sleepTime;
		RequestSuccess.put(RequestStatusTag, RequestStatusSuccessTag);
		RequestFailure.put(RequestStatusTag, RequestStatusFailureTag);
		RequestProcessing.put(RequestStatusTag, RequestStatusProcessingTag);
		RequestUnknown.put(RequestStatusTag, RequestStatusUnknownTag);
	}

	public CatalogFrontendAbstractHandler(CatalogService catalogService, Hashtable<String, Vector<Object>> queue) {
		this(catalogService, queue, DefaultSleepTime);
	}

	// TODO: We only want POST access; remove this method when RnD is over.
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}

	protected void sendStatus(HttpServletResponse response, Map<String, String> status, Object data, String msg) {
		if (msg != null)
			System.err.println(msg);
		Vector<Object> ret = new Vector<Object>();
		if (status != null)
			ret.add(status);
		else
			System.err.println("Bogus response format: No status supplied!");
		if (data != null)
			ret.add(data);
		try {
			response.getWriter().write(Json.toString(ret.toArray()));
		} catch (IOException e) {
			System.err.println("Error sending response to client");
			e.printStackTrace();
		}
	}

	protected void sendStatus(HttpServletResponse response, Map<String, String> status, Object data) {
		sendStatus(response, status, data, null);
	}

	protected void sendStatus(HttpServletResponse response, Map<String, String> status) {
		sendStatus(response, status, null, null);
	}

	protected String getNewReqID(HttpServletResponse response) {
		String reqID = Integer.toString(CatalogFrontend.nextReqID());
		Vector<Object> na = new Vector<Object>();
		na.add(RequestProcessing);
		queue.put(reqID, na);
		Map<String, String> ret = new HashMap<String, String>();
		ret.put(ReqIDTag, reqID);
		try {
			response.getWriter().write(Json.toString(ret));
		} catch (IOException e) {
			System.err.println("Error sending response to client");
			e.printStackTrace();
		}
		return reqID;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected JobStatus prepare(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("utf-8");

		String param = request.getParameter(PostParamTag);
		if (param != null) {
			try {
				param = URLDecoder.decode(param, DefaultEncoding);
			} catch (UnsupportedEncodingException e1) {
				sendStatus(response, RequestFailure, "The thing that shouldn't be: " + e1.getMessage());
				return JobStatus.FINISHED;
			}
			Object jsonParams = null;
			try {
				jsonParams = Json.parse(param);
			} catch (IllegalStateException e) {
				sendStatus(response, RequestFailure, "Error parsing JSON request: " + e.getMessage());
				return JobStatus.FINISHED;
			}
			if (jsonParams instanceof Map) {
				this.jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					String reqID = (String)jsonMap.get(ReqIDTag);
					Vector<Object> res = (Vector<Object>)queue.get(reqID);
					if (res == null) {
						sendStatus(response, RequestUnknown);
						return JobStatus.FINISHED;
					} else if (res.get(0).equals(RequestProcessing)) {
						// Sleep as in long polling
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							System.err.println("Sleep interrupted. Ignoring...");
						}
						sendStatus(response, RequestProcessing);
						return JobStatus.FINISHED;
					}
					sendStatus(response, (Map<String, String>)res.get(0), res.size() > 1 ? res.get(1) : null);
					queue.remove(reqID);
					return JobStatus.FINISHED;
				}
				if (jsonMap.containsKey(UIDTag))
					uid = rice.pastry.Id.build((String)jsonMap.get(UIDTag));
				if (jsonMap.containsKey(CIDTag))
					cid = rice.pastry.Id.build((String)jsonMap.get(UIDTag));
				return JobStatus.REMOTE;
			}
			sendStatus(response, RequestFailure, "Error in JSON request");
			return JobStatus.FINISHED;
		}
		return JobStatus.LOCAL;
	}

	protected long getSleepTime() {
		return sleepTime;
	}

	protected void setSleepTime(long sleepTime) {
		if (sleepTime >= 0)
			this.sleepTime = sleepTime;
	}
}
