package ceid.netcins.frontend;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import ceid.netcins.CatalogService;

public class ShareFileHandler extends CatalogFrontendAbstractHandler {

	private static final long serialVersionUID = 6460386943881811107L;
	public static final String FilenameTag = "eXO::Filename";

	public ShareFileHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (prepare(request, response) == JobStatus.FINISHED)
			return;
		if (jsonMap.containsKey(FilenameTag)) {
			String filename = (String)jsonMap.get(FilenameTag);
			File f = new File(filename);
			if (f.canRead()) {
				final String reqID = getNewReqID(response);
				catalogService.indexContent(f, new Continuation<Object, Exception>() {
					@Override
					public void receiveResult(Object result) {
						if (!(result instanceof Boolean[]))
							receiveException(null);
						Boolean[] resBool = (Boolean[])result;
						boolean didit = false;
						for (int i = 0; i < resBool.length && !didit; i++)
							didit = resBool[i];

						Vector<Object> res = new Vector<Object>();
						res.add(didit ? RequestSuccess : RequestFailure);
						queue.put(reqID, res);
					}

					@Override
					public void receiveException(Exception exception) {
						Vector<Object> res = new Vector<Object>();
						res.add(RequestFailure);
						queue.put(reqID, res);
					}
				});
				return;
			}
		}
		sendStatus(response, RequestFailure);
	}
}
