package ceid.netcins.frontend;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ceid.netcins.CatalogService;

public class GetContentHandler extends AbstractHandler {

	private static final long serialVersionUID = -2901313244513782698L;

	public GetContentHandler(CatalogService catalogService,
			Hashtable<String, Vector<Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		if (prepare(request, response) == RequestState.FINISHED)
			return;
		sendStatus(response, RequestStatus.SUCCESS, catalogService.getUser().getSharedContentProfiles());
	}
}
