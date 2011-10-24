package ceid.netcins.exo.frontend.handlers;

import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.content.ContentField;
import ceid.netcins.exo.content.ContentProfile;
import ceid.netcins.exo.content.Status;
import ceid.netcins.exo.content.TermField;
import ceid.netcins.exo.user.Friend;
import rice.Continuation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/24/11
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetFriendStatusHandler extends AbstractHandler {

    private static final long serialVersionUID = 7508245962714729908L;

    public GetFriendStatusHandler(CatalogService catalogService,
                                  Hashtable<String, Hashtable<String, Object>> queue) {
        super(catalogService, queue);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       final HttpServletResponse response) throws ServletException {
        if (prepare(request, response) == RequestState.FINISHED)
            return;


        final String reqID = getNewReqID(response);

        final List<ContentProfile> contentProfiles = new ArrayList<ContentProfile>();
        for (final Friend friend : catalogService.getUser().getFriends().values()) {
            try {
                catalogService.getUserProfile(friend.getUID(), friend.getNodeHandle(), new Continuation<Object, Exception>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void receiveResult(Object result) {
                        final HashMap<String, Object> resMap = (HashMap<String, Object>) result;
                        if (!((Integer) resMap.get("status")).equals(CatalogService.SUCCESS))
                            receiveException(new RuntimeException());

                        final ContentProfile thisContentProfile = new ContentProfile((ContentProfile) resMap.get("data"));
                        final ContentProfile thisStatusProfile = new ContentProfile();

                        for (final ContentField contentField : thisContentProfile.getAllFields()) {
                            if (contentField instanceof Status) {
                                thisStatusProfile.add(contentField);
                            } else if (contentField.getFieldName().equals("Username")) {
                                thisStatusProfile.add(contentField);
                            }
                        }
                        thisContentProfile.add(new TermField("eXO:UID", friend.getUID().toStringFull()));

                        contentProfiles.add(thisStatusProfile);
                    }

                    @Override
                    public void receiveException(Exception exception) {
                        exception.printStackTrace();
                        queueStatus(reqID, RequestStatus.FAILURE, null);
                    }
                });
            } catch (final Exception e) {
                queueStatus(reqID, RequestStatus.FAILURE, null);
            }
        }

        queueStatus(reqID, RequestStatus.SUCCESS, contentProfiles);
    }
}
