package ceid.netcins.frontend;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import ceid.netcins.CatalogService;
import ceid.netcins.json.Json;

public class ShareFileHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = 6460386943881811107L;
	public static final String FilenameTag = "eXO::Filename";

	public ShareFileHandler(CatalogService catalogService,
			Hashtable<String, Object> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		final Vector<String> failRet = new Vector<String>();
		failRet.add(RequestStatusFailureTag);

		String param = request.getParameter(PostParamTag);
		if (param != null) {
			param = URLDecoder.decode(param, DefaultEncoding);
			Object jsonParams = Json.parse(param);
			if (jsonParams instanceof Map) {
				@SuppressWarnings("rawtypes")
				Map jsonMap = (Map)jsonParams;
				if (jsonMap.containsKey(ReqIDTag)) {
					String reqID = (String)jsonMap.get(ReqIDTag);
					@SuppressWarnings("unchecked")
					Vector<Object> res = (Vector<Object>) queue.get(reqID);
					if (res == null || res.get(0).equals(RequestStatusProcessingTag)) {
						Map<String, String> ret = new HashMap<String, String>();
						ret.put(RequestStatusTag, RequestStatusProcessingTag);
						response.getWriter().write(Json.toString(ret));
						response.flushBuffer();
						return;
					}
					response.getWriter().write(Json.toString(res.toArray()));
					response.flushBuffer();
					queue.remove(reqID);
					return;
				} else if (jsonMap.containsKey(FilenameTag)) {
					@SuppressWarnings("rawtypes")
					String filename = (String)((Map)jsonParams).get(FilenameTag);
					File f = new File(filename);
					if (f.canRead()) {
						final String reqID = Integer.toString(CatalogFrontend.nextReqID());
						Vector<String> na = new Vector<String>();
						na.add(RequestStatusProcessingTag);
						queue.put(reqID, na);
						Map<String, String> ret = new HashMap<String, String>();
						ret.put(ReqIDTag, reqID);
						response.getWriter().write(Json.toString(ret));

						catalogService.indexContent(f, new Continuation<Object, Exception>() {
							@Override
							public void receiveResult(Object result) {
								if (!(result instanceof Boolean[]))
									receiveException(null);
								Boolean[] resBool = (Boolean[])result;
								boolean didit = false;
								for (int i = 0; i < resBool.length && !didit; i++)
									didit = resBool[i];

								Vector<String> res = new Vector<String>();
								if (didit)
									res.add(RequestStatusSuccessTag);
								else
									res.add(RequestStatusFailureTag);
								queue.put(reqID, res);
							}

							@Override
							public void receiveException(Exception exception) {
								queue.put(reqID, failRet);
							}
						});
						return;
					}
				}
			}
		}
		response.getWriter().write(Json.toString(failRet));
	}
}
