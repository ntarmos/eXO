package ceid.netcins.frontend.handlers;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import ceid.netcins.CatalogService;

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
public class ShareFileHandler extends AbstractHandler {

	private static final long serialVersionUID = 6460386943881811107L;
	public static final String FilenameTag = "eXO::Filename";

	public ShareFileHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
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
						queueStatus(reqID, didit ? RequestStatus.SUCCESS : RequestStatus.FAILURE, null);
					}

					@Override
					public void receiveException(Exception exception) {
						queueStatus(reqID, RequestStatus.FAILURE, null);
					}
				});
				return;
			}
		}
		sendStatus(response, RequestStatus.FAILURE, null);
	}
}
