package ceid.netcins.exo.messages;

/**
 * @author <a href="mailto:loupasak@ceid.upatras.gr">A. Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public final class MessageType {
    public static final short Cache = 1;
    public static final short FetchHandle = 2;
    public static final short Fetch = 3;
    public static final short Insert = 4;
    public static final short LookupHandles = 5;
    public static final short Lookup = 6;
    public static final short Catalog = 7;
    public static final short Query = 8;
    public static final short SocialQuery = 9;
    public static final short FriendQuery = 10;
    public static final short FriendRequest = 11;
    public static final short FriendAccept = 12;
    public static final short FriendReject = 13;
    public static final short GetUserProfile = 14;
    public static final short RetrieveContent = 15;
    public static final short RetrieveContentIDs = 16;
    public static final short RetrieveContentTags = 17;
    public static final short TagContent = 18;
    public static final short TagUser = 19;
}
