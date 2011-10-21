package ceid.netcins.exo.frontend.handlers;

import ceid.netcins.exo.CatalogService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Hashtable;

/**
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class SearchContentDHTHandler extends SearchRequestBaseHandler {
    private static final long serialVersionUID = 825367464625718048L;

    public SearchContentDHTHandler(CatalogService catalogService,
                                   Hashtable<String, Hashtable<String, Object>> queue) {
        super(catalogService, queue);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException {
        if (prepare(request, response) == RequestState.FINISHED)
            return;

        final String reqID = getNewReqID(response);
        catalogService.searchContent(rawQuery, queryTopK,
                new SearchResultContinuation(reqID, this));
    }
}
