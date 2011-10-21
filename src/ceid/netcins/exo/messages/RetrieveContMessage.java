package ceid.netcins.exo.messages;

import ceid.netcins.exo.utils.JavaSerializer;
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
 * This class represents a message which is sent to the source of a shared
 * object to begin downloading of the content.
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class RetrieveContMessage extends ContinuationMessage {

    /**
     *
     */
    private static final long serialVersionUID = 2929433230379969486L;

    public static final short TYPE = MessageType.RetrieveContent;

    // the id to fetch
    private Id id;

    // whether or not this message has been cached
    private boolean cached = false;

    // the list of nodes where this message has been
    private NodeHandle handle;

    // RetrieveContPDU holds the query terms
    private RetrieveContPDU rcpdu;

    /**
     * Constructor
     *
     * @param uid    The unique id
     * @param id     The location to be routed
     * @param source The source address
     * @param dest   The destination address
     */
    public RetrieveContMessage(int uid, Id id, NodeHandle source, Id dest,
                               RetrieveContPDU rcpdu) {
        super(uid, source, dest);

        this.id = id;
        this.rcpdu = rcpdu;
    }

    /**
     * Method which returns the id
     *
     * @return The contained id
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
     * Getter for PDU
     *
     * @return
     */
    public RetrieveContPDU getRetrieveContPDU() {
        return rcpdu;
    }

    /**
     * Returns a string representation of this message
     *
     * @return A string representing this message
     */
    @Override
    public String toString() {
        return "[RetrieveContMessage for " + id + " data " + response + "]";
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

        // Java serialization is used for the serialization of the QueryPDU
        JavaSerializer.serialize(buf, rcpdu);
    }

    public static RetrieveContMessage build(InputBuffer buf, Endpoint endpoint,
                                            PastContentDeserializer pcd) throws IOException {
        byte version = buf.readByte();
        switch (version) {
            case 0:
                return new RetrieveContMessage(buf, endpoint, pcd);
            default:
                throw new IOException("Unknown Version: " + version);
        }
    }

    private RetrieveContMessage(InputBuffer buf, Endpoint endpoint,
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

        // Java deserialization
        rcpdu = (RetrieveContPDU) JavaSerializer.deserialize(buf, endpoint);
    }

}
