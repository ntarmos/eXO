package ceid.netcins.messages;

import java.io.IOException;

import ceid.netcins.utils.JavaSerializer;

import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.OutputBuffer;
import rice.p2p.past.messaging.ContinuationMessage;
import rice.p2p.past.rawserialization.PastContentDeserializer;
import rice.p2p.past.rawserialization.RawPastContent;

/**
 * This message type is sent to indicate that a friend request has been rejected
 * by the user.
 * 
 * @author Andreas Loupasakis
 */
public class FriendRejectMessage extends ContinuationMessage {

	private static final long serialVersionUID = -3806559028918837945L;

	public static final short TYPE = MessageType.FriendReject;

	// whether or not this message has been cached
	private boolean cached = false;

	// the list of nodes where this message has been
	private NodeHandle handle;

	// FriendReqPDU holds the message and the username
	private FriendReqPDU frPDU;
	
	/**
	 * Constructor
	 * 
	 * @param uid The unique id
	 * @param id The dest UID (duplication of dest)
	 * @param source The source address
	 * @param dest The destination address UID
	 */
	public FriendRejectMessage(int uid, Id id, NodeHandle source, Id dest,
			FriendReqPDU frPDU) {
		super(uid, source, dest);

		this.frPDU = frPDU;
	}

	/**
	 * Getter for PDU
	 * 
	 * @return The FriendReqPDU object
	 */
	public FriendReqPDU getFriendReqPDU() {
		return frPDU;
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
	 * @param handle
	 *            The current local handle
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
		return "[FriendRejectMessage for " + id + " data " + response + "]";
	}

	/***************** Raw Serialization ***************************************/
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

		buf.writeBoolean(cached);

		// Java serialization is used for the serialization of the FriendReqPDU
		JavaSerializer.serialize(buf, frPDU);
	}

	public static FriendRejectMessage build(InputBuffer buf, Endpoint endpoint,
			PastContentDeserializer pcd) throws IOException {
		byte version = buf.readByte();
		switch (version) {
		case 0:
			return new FriendRejectMessage(buf, endpoint, pcd);
		default:
			throw new IOException("Unknown Version: " + version);
		}
	}

	private FriendRejectMessage(InputBuffer buf, Endpoint endpoint,
			PastContentDeserializer pcd) throws IOException {
		super(buf, endpoint);
		if (serType == S_SUB) {
			short contentType = buf.readShort();
			response = pcd.deserializePastContent(buf, endpoint, contentType);
		}
		if (buf.readBoolean())
			handle = endpoint.readNodeHandle(buf);
		cached = buf.readBoolean();

		// Java deserialization
		frPDU = (FriendReqPDU) JavaSerializer.deserialize(buf, endpoint);
	}
}