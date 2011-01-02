package ceid.netcins.exo.frontend.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import rice.Continuation;
import rice.environment.params.Parameters;
import rice.persistence.PersistentStorage;
import ceid.netcins.exo.CatalogService;

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
	private static final String FileHeaderTag = "X-File-Name";
	private static final String UploadRepository = "shared";

	private String uploadRepository = null;

	public ShareFileHandler(CatalogService catalogService,
			Hashtable<String, Hashtable<String, Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;

		String filename = request.getHeader(FileHeaderTag);
		if (filename == null || filename.equals("")) {
			sendStatus(response, RequestStatus.FAILURE, null);
			return;
		}

		PersistentStorage ps = (PersistentStorage)catalogService.getStorageManager().getStorage();
		Parameters params = catalogService.getEnvironment().getParameters();
		// XXX : In a real-world implementation, this should be either sanitized or decoupled from user-supplied data
		uploadRepository =
			ps.getRoot() + File.separator +
			ps.getName() + File.separator +
			(params.contains("exo_uploads_repository") ?
					params.getString("exo_uploads_repository") :
					UploadRepository
			);

		File uploadDir = new File(uploadRepository);
		if (!uploadDir.mkdirs() && !(uploadDir.exists() && uploadDir.isDirectory())) {
			sendStatus(response, RequestStatus.FAILURE, null);
			return;
		}

		try {
			InputStream is = request.getInputStream();

			File upload = new File(uploadRepository + File.separator + filename);
			if (upload == null ||
					(upload.exists() && !upload.delete() && !upload.createNewFile()) ||
					(!upload.exists() && !upload.createNewFile()))
				throw new Exception();

			FileOutputStream fos = new FileOutputStream(upload);
			IOUtils.copy(is, fos);
			doIndexContent(upload, getNewReqID(response));
			return;
		} catch (IOException e) {
			// Fall through
		} catch (Exception e) {
			// Fall through
		}
		sendStatus(response, RequestStatus.FAILURE, null);
	}

	private void doIndexContent(final File upload, final String reqID) {
		catalogService.indexContent(upload, new Continuation<Object, Exception>() {
			@Override
			public void receiveResult(Object result) {
				if (!(result instanceof Boolean[])) {
					queueStatus(reqID, RequestStatus.FAILURE, null);
					return;
				}
				Boolean[] resBool = (Boolean[])result;
				boolean didit = false;
				for (int i = 0; i < resBool.length && !didit; i++)
					didit = resBool[i];
				queueStatus(reqID, didit ? RequestStatus.SUCCESS : RequestStatus.FAILURE, null);
			}

			@Override
			public void receiveException(Exception exception) {
				System.err.println("Received exception while trying to index file. Retrying...");
				doIndexContent(upload, reqID);
			}
		});
	}
}
