package ceid.netcins.exo.messages;

import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.OutputBuffer;
import rice.p2p.past.messaging.ContinuationMessage;
import rice.p2p.past.rawserialization.PastContentDeserializer;
import rice.p2p.past.rawserialization.RawPastContent;

import java.io.IOException;

/**
 * This class will carry the friend request message. Specifically, this class
 * will not have to carry any extra data than LookupMessage.
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class GetUserProfileMessage extends ContinuationMessage {

    private static final long serialVersionUID = -7491091045725823901L;

    public static final short TYPE = MessageType.GetUserProfile;

    // the destination id (DUPE of dest)
    private Id id;

    // whether or not this message has been cached
    private boolean cached = false;

    // the list of nodes where this message has been
    private NodeHandle handle;

    /**
     * Constructor
     *
     * @param uid    The unique id
     * @param id     The location to be stored
     * @param source The source address
     * @param dest   The destination address
     */
    public GetUserProfileMessage(int uid, Id id, NodeHandle source, Id dest) {
        super(uid, source, dest);
        this.id = id;
    }

    /**
     * Getter for the destination id
     *
     * @return The destination id
     */
    public Id getId() {
        return id;
    }

    /**
     * Returns whether or not this message has been cached
     *
     * @return Whether or not this message has been cached
     */
    public boolean isCached() {
        return cached;
    }

    /**
     * Sets this message as having been cached.
     */
    public void setCached() {
        cached = true;
    }

    /**
     * Method which is designed to be overridden by subclasses if they need to
     * keep track of where they've been.
     *
     * @param handle The current local handle
     */
    @Override
    public void addHop(NodeHandle handle) {
        this.handle = handle;
    }

    /**
     * Method which returns the previous hop (where the message was just at)
     *
     * @return The previous hop
     */
    public NodeHandle getPreviousNodeHandle() {
        return handle;
    }

    /**
     * Returns a string representation of this message
     *
     * @return A string representing this message
     */
    @Override
    public String toString() {
        return "[GetUserProfileMessage for " + id + " data " + response + "]";
    }

    /**
     * ************** Raw Serialization **************************************
     */
    public short getType() {
        return TYPE;
    }

    @Override
    public void serialize(OutputBuffer buf) throws IOException {
        buf.writeByte((byte) 0); // version
        if (response != null && response instanceof RawPastContent) {
            super.serialize(buf, false);
            RawPastContent rpc = (RawPastContent) response;
            buf.writeShort(rpc.getType());
            rpc.serialize(buf);
        } else {
            super.serialize(buf, true);
        }

        buf.writeBoolean(handle != null);
        if (handle != null)
            handle.serialize(buf);

        buf.writeShort(id.getType());
        id.serialize(buf);
        buf.writeBoolean(cached);
    }

    public static GetUserProfileMessage build(InputBuffer buf, Endpoint endpoint,
                                              PastContentDeserializer pcd) throws IOException {
        byte version = buf.readByte();
        switch (version) {
            case 0:
                return new GetUserProfileMessage(buf, endpoint, pcd);
            default:
                throw new IOException("Unknown Version: " + version);
        }
    }

    private GetUserProfileMessage(InputBuffer buf, Endpoint endpoint,
                                  PastContentDeserializer pcd) throws IOException {
        super(buf, endpoint);
        if (serType == S_SUB) {
            short contentType = buf.readShort();
            response = pcd.deserializePastContent(buf, endpoint, contentType);
        }
        if (buf.readBoolean())
            handle = endpoint.readNodeHandle(buf);
        try {
            id = endpoint.readId(buf, buf.readShort());
        } catch (IllegalArgumentException iae) {
            System.out.println(iae + " " + this + " serType:" + serType
                    + " UID:" + getUID() + " d:" + dest + " s:" + source);
            throw iae;
        }
        cached = buf.readBoolean();
    }
}
