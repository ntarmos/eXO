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
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class TagUserMessage extends ContinuationMessage {

    private static final long serialVersionUID = -4033244706260792985L;

    public static final short TYPE = MessageType.TagUser;

    // the id of the tagged user
    private Id taggedId;

    // whether or not this message has been cached
    private boolean cached = false;

    // the list of nodes where this message has been
    private NodeHandle handle;

    // TagContentPDU holds the tag message
    private TagPDU tuPDU;

    /**
     * Constructor
     *
     * @param uid    The unique id
     * @param id     The location to be stored
     * @param source The source address
     * @param dest   The destination address
     */
    public TagUserMessage(int uid, Id taggedId, NodeHandle source, Id dest,
                          TagPDU tagUserPDU) {
        super(uid, source, dest);

        this.taggedId = taggedId;
        this.tuPDU = tagUserPDU;
    }

    /**
     * Method which returns the taggee's id
     *
     * @return The contained id
     */
    public Id getTaggedId() {
        return taggedId;
    }

    /**
     * Getter for PDU
     *
     * @return
     */
    public TagPDU getTagUserPDU() {
        return tuPDU;
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
        return "[TagContentMessage for " + id + " data " + response + "]";
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

        buf.writeShort(taggedId.getType());
        taggedId.serialize(buf);
        buf.writeBoolean(cached);

        // Java serialization is used for the serialization of the TagContentPDU
        JavaSerializer.serialize(buf, tuPDU);
    }

    public static TagUserMessage build(InputBuffer buf, Endpoint endpoint,
                                       PastContentDeserializer pcd) throws IOException {
        byte version = buf.readByte();
        switch (version) {
            case 0:
                return new TagUserMessage(buf, endpoint, pcd);
            default:
                throw new IOException("Unknown Version: " + version);
        }
    }

    private TagUserMessage(InputBuffer buf, Endpoint endpoint,
                           PastContentDeserializer pcd) throws IOException {
        super(buf, endpoint);
        if (serType == S_SUB) {
            short contentType = buf.readShort();
            response = pcd.deserializePastContent(buf, endpoint, contentType);
        }
        if (buf.readBoolean())
            handle = endpoint.readNodeHandle(buf);
        try {
            taggedId = endpoint.readId(buf, buf.readShort());
        } catch (IllegalArgumentException iae) {
            System.out.println(iae + " " + this + " serType:" + serType
                    + " UID:" + getUID() + " d:" + dest + " s:" + source);
            throw iae;
        }
        cached = buf.readBoolean();

        // Java deserialization
        tuPDU = (TagPDU) JavaSerializer.deserialize(buf, endpoint);
    }
}
