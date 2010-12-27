package ceid.netcins.frontend.handlers;

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
import ceid.netcins.frontend.CatalogFrontend;
import ceid.netcins.frontend.json.Json;

/**
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 * 
 */
public abstract class AbstractHandler extends HttpServlet {
	private static final long serialVersionUID = -5657532444852074783L;

	private static final long DefaultSleepTime = 3000;
	private static final String DefaultEncoding = "utf-8";
	private static final String PostParamTag = "eXO_data";

	private static final String UIDTag = "eXO::UID";
	private static final String CIDTag = "eXO::CID";
	private static final String ReqIDTag = "eXO::reqID";

	protected static enum RequestState {
		LOCAL,
		REMOTE,
		FINISHED
	}

	protected static enum RequestStatus {
		SUCCESS("eXO::Success"),
		FAILURE("eXO::Failure"),
		PROCESSING("eXO::Processing"),
		UNKNOWN("eXO::Unknown");

		private String tag;
		public static final String RequestStatusTag = "eXO::Status";

		private RequestStatus(String tag) {
			this.tag = tag;
		}

		public String toString() {
			return tag;
		}
	}

	private long sleepTime;
	private Hashtable<String, Vector<Object>> queue = null;
	private static final Map<RequestStatus, Map<String, String>> JobStatusMaps = new HashMap<RequestStatus, Map<String, String>>(); 

	protected CatalogService catalogService = null;

	@SuppressWarnings("rawtypes")
	protected Map jsonMap = null;
	protected Id uid = null;
	protected Id cid = null;

	public AbstractHandler(CatalogService catalogService, Hashtable<String, Vector<Object>> queue, long sleepTime) {
		this.catalogService = catalogService;
		this.queue = queue;
		this.sleepTime = sleepTime;
		makeStatusMaps();
	}

	public AbstractHandler(CatalogService catalogService, Hashtable<String, Vector<Object>> queue) {
		this(catalogService, queue, DefaultSleepTime);
	}

	// TODO: We only want POST access; remove this method when RnD is over.
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}


	@Override
	public abstract void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException;

	private void makeStatusMaps() {
		for (RequestStatus v : RequestStatus.values()) {
			HashMap<String, String> ret = new HashMap<String, String>();
			ret.put(RequestStatus.RequestStatusTag, v.toString());
			JobStatusMaps.put(v, ret);
		}
	}

	private Map<String, String> getStatusMap(RequestStatus status) {
		return JobStatusMaps.get(status);
	}

	private void sendStatus(HttpServletResponse response, RequestStatus status, Object data, String msg) {
		if (msg != null)
			System.err.println(msg);
		Vector<Object> ret = makeSendResult(status, data);
		try {
			response.getWriter().write(Json.toString(ret.toArray()));
		} catch (IOException e) {
			System.err.println("Error sending response to client");
			e.printStackTrace();
		}
	}

	protected void sendStatus(HttpServletResponse response, RequestStatus status, Object data) {
		sendStatus(response, status, data, null);
	}

	protected String getNewReqID(HttpServletResponse response) {
		String reqID = Integer.toString(CatalogFrontend.nextReqID());
		queueStatus(reqID, RequestStatus.PROCESSING, null);
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

	@SuppressWarnings("rawtypes")
	protected RequestState prepare(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("utf-8");

		String param = request.getParameter(PostParamTag);
		if (param != null) {
			try {
				param = URLDecoder.decode(param, DefaultEncoding);
			} catch (UnsupportedEncodingException e1) {
				sendStatus(response, RequestStatus.FAILURE, null, "The thing that shouldn't be: " + e1.getMessage());
				return RequestState.FINISHED;
			}
			Object jsonParams = null;
			try {
				jsonParams = Json.parse(param);
			} catch (IllegalStateException e) {
				sendStatus(response, RequestStatus.FAILURE, null, "Error parsing JSON request: " + e.getMessage());
				return RequestState.FINISHED;
			}
			if (jsonParams instanceof Map) {
				this.jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					RequestStatus curStatus = null;
					String reqID = (String)jsonMap.get(ReqIDTag);
					Vector<Object> res = (Vector<Object>)queue.get(reqID);
					if (res == null) {
						sendStatus(response, RequestStatus.UNKNOWN, null, null);
						return RequestState.FINISHED;
					} else if ((curStatus = ((RequestStatus)res.get(0))).equals(RequestStatus.PROCESSING)) {
						// Sleep as in long polling
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							System.err.println("Sleep interrupted. Ignoring...");
						}
						sendStatus(response, RequestStatus.PROCESSING, null, null);
						return RequestState.FINISHED;
					}
					sendStatus(response, curStatus, res.size() > 1 ? res.get(1) : null, null);
					queue.remove(reqID);
					return RequestState.FINISHED;
				}
				if (jsonMap.containsKey(UIDTag))
					uid = rice.pastry.Id.build((String)jsonMap.get(UIDTag));
				if (jsonMap.containsKey(CIDTag))
					cid = rice.pastry.Id.build((String)jsonMap.get(CIDTag));
				return RequestState.REMOTE;
			}
			sendStatus(response, RequestStatus.FAILURE, null, "Error in JSON request");
			return RequestState.FINISHED;
		}
		return RequestState.LOCAL;
	}

	protected long getSleepTime() {
		return sleepTime;
	}

	protected void setSleepTime(long sleepTime) {
		if (sleepTime >= 0)
			this.sleepTime = sleepTime;
	}

	protected void queueStatus(String reqID, RequestStatus status, Object data) {
		queue.put(reqID, makeQueueResult(status, data));
	}

	private Vector<Object> makeQueueResult(RequestStatus status, Object data) {
		Vector<Object> res = new Vector<Object>();
		if (status != null)
			res.add(status);
		else
			System.err.println("Bogus response format: No status supplied!");
		if (data != null)
			res.add(data);
		return res;
	}

	private Vector<Object> makeSendResult(RequestStatus status, Object data) {
		Vector<Object> res = new Vector<Object>();
		if (status != null)
			res.add(getStatusMap(status));
		else
			System.err.println("Bogus response format: No status supplied!");
		if (data != null)
			res.add(data);
		return res;
	}
}
