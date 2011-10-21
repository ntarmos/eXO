package ceid.netcins.exo.frontend.handlers;

import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.content.ContentProfile;
import ceid.netcins.exo.frontend.json.ContentProfileJSONConvertor;
import rice.Continuation;
import rice.environment.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
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
public class SetUserProfileHandler extends AbstractHandler {

    private static final long serialVersionUID = 5124127253356875812L;

    public SetUserProfileHandler(CatalogService catalogService,
                                 Hashtable<String, Hashtable<String, Object>> queue) {
        super(catalogService, queue);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException {
        if (prepare(request, response) == RequestState.FINISHED)
            return;

        ContentProfileJSONConvertor cpj = new ContentProfileJSONConvertor();
        ContentProfile profile = (ContentProfile) cpj.fromJSON(jsonMap);

        if (uid == null) { // Local operation
            ContentProfile oldProfile = catalogService.getUser().getPublicUserProfile();
            if (!oldProfile.equalsPublic(profile)) {
                final String reqID = getNewReqID(response);
                // The public part has changed. We should reindex the user profile in the network
                ContentProfile deletions = oldProfile.minus(profile);
                doSetUserProfile(profile, deletions, reqID);
            } else {
                catalogService.getUser().setUserProfile(profile);
                sendStatus(response, RequestStatus.SUCCESS, null);
            }
            return;
        }

        // Search for the user in the network
        final String reqID = getNewReqID(response);
        doTagUser(profile, reqID);
    }

    private void doSetUserProfile(final ContentProfile additions, final ContentProfile deletions, final String reqID) {
        catalogService.indexUser(additions, deletions, new Continuation<Object, Exception>() {
            public void receiveResult(Object result) {
                // TODO : Check the replicas if are updated correctly!
                // run replica maintenance
                // runReplicaMaintence();
                int indexedNum = 0;
                Boolean[] results = null;
                if (!(result instanceof Boolean[])) {
                    queueStatus(reqID, RequestStatus.FAILURE, null);
                    return;
                }
                results = (Boolean[]) result;
                if (results != null)
                    for (Boolean isIndexedTerm : results) {
                        if (isIndexedTerm)
                            indexedNum++;
                    }
                Logger logger = catalogService.getEnvironment().getLogManager().getLogger(this.getClass(), null);
                if (logger.level <= Logger.INFO)
                    logger.log("Total " + indexedNum + " terms indexed out of " + results.length);
                if (indexedNum < results.length)
                    receiveException(new Exception());
                queueStatus(reqID, RequestStatus.SUCCESS, results);
            }

            public void receiveException(Exception result) {
                System.err.println("Received exception while trying to index user. Retrying...");
                doSetUserProfile(additions, deletions, reqID);
            }
        });
    }

    private void doTagUser(final ContentProfile profile, final String reqID) {
        catalogService.tagUser(uid, profile, null, new Continuation<Object, Exception>() {

            @Override
            public void receiveResult(Object result) {
                if (!(result instanceof HashMap)) {
                    queueStatus(reqID, RequestStatus.FAILURE, null);
                    return;
                }
                @SuppressWarnings("unchecked")
                HashMap<String, Object> resMap = (HashMap<String, Object>) result;
                if (!((Integer) resMap.get("status")).equals(CatalogService.SUCCESS))
                    receiveException(new RuntimeException());
                queueStatus(reqID, RequestStatus.SUCCESS, resMap.get("data"));
            }

            @Override
            public void receiveException(Exception exception) {
                System.err.println("Received exception while trying to tag user. Retrying...");
                doTagUser(profile, reqID);
            }
        });
    }
}
