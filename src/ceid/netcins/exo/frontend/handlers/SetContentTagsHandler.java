package ceid.netcins.exo.frontend.handlers;

import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.content.ContentProfile;
import ceid.netcins.exo.content.StoredField;
import ceid.netcins.exo.content.TermField;
import ceid.netcins.exo.frontend.json.ContentProfileJSONConvertor;
import rice.Continuation;

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
public class SetContentTagsHandler extends AbstractHandler {

    private static final long serialVersionUID = -565717952819033549L;

    public SetContentTagsHandler(CatalogService catalogService,
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

        if (cid == null || profile == null) {
            sendStatus(response, RequestStatus.FAILURE, null);
            return;
        }

        final String reqID = getNewReqID(response);

        if (profile.getField("SHA-1", TermField.class) == null && profile.getField("SHA-1", StoredField.class) == null)
            profile.add(new StoredField("SHA-1", cid.toStringFull()));
        TermField identifier = (TermField) profile.getField("Filename", TermField.class);

        if (uid == null) { // Local resource.
            // New tag-less content item
            ContentProfile additions = null, deletions = null;
            ContentProfile oldProfile = catalogService.getUser().getSharedContentProfile(cid);
            if (oldProfile != null) {
                additions = profile.minus(oldProfile);
                deletions = oldProfile.minus(profile);
            } else {
                additions = profile;
            }
            doIndexPseudoContent((identifier != null) ? identifier.getFieldData() : null, additions, deletions, reqID);
            return;
        }

        // Search for it in the network
        doTagContent(profile, reqID);
    }

    private void doIndexPseudoContent(final String identifier, final ContentProfile additions, final ContentProfile deletions, final String reqID) {
        catalogService.indexPseudoContent(cid,
                identifier, additions, deletions,
                new Continuation<Object, Exception>() {
                    @Override
                    public void receiveResult(Object result) {
                        if (!(result instanceof Boolean[])) {
                            queueStatus(reqID, RequestStatus.FAILURE, null);
                            return;
                        }
                        Boolean[] resBool = (Boolean[]) result;
                        boolean didit = false;
                        for (int i = 0; i < resBool.length && !didit; i++)
                            didit = resBool[i];
                        queueStatus(reqID, didit ? RequestStatus.SUCCESS : RequestStatus.FAILURE, null);
                    }

                    @Override
                    public void receiveException(Exception exception) {
                        System.err.println("Received exception while trying to index content. Retrying...");
                        queueStatus(reqID, RequestStatus.FAILURE, null);
                    }
                });
    }

    private void doTagContent(final ContentProfile profile, final String reqID) {
        catalogService.tagContent(uid, cid, profile,
                new Continuation<Object, Exception>() {
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
                        System.err.println("Received exception while trying to tag content. Retrying...");
                        doTagContent(profile, reqID);
                    }
                });
    }
}
