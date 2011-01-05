package ceid.netcins.exo.frontend.handlers;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rice.Continuation;
import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.catalog.ScoreBoard;
import ceid.netcins.exo.messages.ResponsePDU;

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
abstract public class SearchRequestBaseHandler extends AbstractHandler {
	private static final long serialVersionUID = 8956831399923066438L;

	protected class SearchResultContinuation implements Continuation<Object, Exception> {
		private String reqID;

		public SearchResultContinuation(String reqID, AbstractHandler handler) {
			this.reqID = reqID;
		}

		@Override
		public void receiveResult(Object arg0) {
			ScoreBoard sb = null;
			if (arg0 == null || !(arg0 instanceof ResponsePDU)) {
				queueStatus(reqID, RequestStatus.FAILURE, null);
				return;
			}
			if ((sb = ((ResponsePDU)arg0).getScoreBoard()) == null)
				sb = new ScoreBoard(null, null);
			queueStatus(reqID, RequestStatus.SUCCESS, sb);
		}

		@Override
		public void receiveException(Exception arg0) {
			queueStatus(reqID, RequestStatus.FAILURE, null);
		}
	}

	public SearchRequestBaseHandler(CatalogService catalogService,
			Hashtable<String, Hashtable<String, Object>> queue) {
		super(catalogService, queue);
	}

	@Override
	protected RequestState prepare(HttpServletRequest request,
			HttpServletResponse response) {
		if (super.prepare(request, response) == RequestState.FINISHED)
			return RequestState.FINISHED;

		if (rawQuery == null || queryTopK == null) {
			sendStatus(response, RequestStatus.FAILURE, null);
			return RequestState.FINISHED;
		}
		return RequestState.REMOTE;
	}
}
