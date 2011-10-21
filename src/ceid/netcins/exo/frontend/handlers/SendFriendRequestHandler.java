package ceid.netcins.exo.frontend.handlers;

import ceid.netcins.exo.CatalogService;
import ceid.netcins.exo.user.Friend;
import ceid.netcins.exo.user.FriendRequest;
import rice.p2p.commonapi.Id;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Hashtable;
import java.util.Set;

/**
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class SendFriendRequestHandler extends FriendRequestBaseHandler {
    private static final long serialVersionUID = -5535744211758924495L;

    public SendFriendRequestHandler(CatalogService catalogService,
                                    Hashtable<String, Hashtable<String, Object>> queue) {
        super(catalogService, queue);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException {
        if (prepare(request, response) == RequestState.FINISHED)
            return;

        synchronized (catalogService) {
            Hashtable<Id, Friend> friends = catalogService.getUser().getFriends();
            Set<Id> freqsOut = catalogService.getUser().getPendingOutgoingFReq();
            Hashtable<Id, FriendRequest> freqsIn = catalogService.getUser().getPendingIncomingFReq();

            if (friends.containsKey(uid) || freqsOut.contains(uid)) {
                queueStatus(reqID, RequestStatus.SUCCESS, null);
                return;
            }
            if (freqsIn.containsKey(uid)) {
                catalogService.acceptFriend(freqsIn.get(uid), command);
                return;
            }
            catalogService.friendRequest(uid, frMsg, command);
        }
    }
}
