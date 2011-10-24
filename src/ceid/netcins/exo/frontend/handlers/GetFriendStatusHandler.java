package ceid.netcins.exo.frontend.handlers;

import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.content.ContentField;
import ceid.netcins.exo.content.ContentProfile;
import ceid.netcins.exo.content.Status;
import ceid.netcins.exo.frontend.json.Json;
import ceid.netcins.exo.frontend.json.StatusJSONConvertor;
import rice.Continuation;
import rice.p2p.commonapi.Id;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

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



        /*if (uid == null) { // Local operation. Return immediately.
            ContentProfile userProfile = catalogService.getUserProfile();
            if (userProfile != null)

            else
                sendStatus(response, RequestStatus.FAILURE, null);
            return;
        }*/
        System.out.println("GetFriendStatusHandler");
        //final String reqID = getNewReqID(response);
        for (final Id id : catalogService.getUser().getFriends().keySet()) {
            try {
                catalogService.getUserProfile(id, catalogService.getUser().getFriends().get(id).getNodeHandle(), new Continuation<Object, Exception>() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void receiveResult(Object result) {
                        final HashMap<String, Object> resMap = (HashMap<String, Object>) result;
                        if (!((Integer) resMap.get("status")).equals(CatalogService.SUCCESS))
                            receiveException(new RuntimeException());
                        System.out.println("iterator");
                        System.out.println("------------------------------");
                        ContentProfile contentProfile = new ContentProfile((ContentProfile) resMap.get("data"));

                        for (ContentField contentField : contentProfile.getAllFields()) {
                            if (contentField.getFieldName().equals(StatusJSONConvertor.StatusTag)) {
                                System.out.println(((Status) contentField).toString());
                            }
                        }


                        System.out.println("------------------------------");

                        sendStatus(response, RequestStatus.SUCCESS, contentProfile);
                        //queueStatus(reqID, RequestStatus.SUCCESS, resMap.get("data"));
                    }

                    @Override
                    public void receiveException(Exception exception) {
                        exception.printStackTrace();
                        //queueStatus(reqID, RequestStatus.FAILURE, null);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                //queueStatus(reqID, RequestStatus.FAILURE, null);
            }
        }


    }
}
