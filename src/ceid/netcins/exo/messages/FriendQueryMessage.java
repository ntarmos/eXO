package ceid.netcins.exo.messages;

import java.io.IOException;

import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.OutputBuffer;
import rice.p2p.past.messaging.ContinuationMessage;
import rice.p2p.past.rawserialization.PastContentDeserializer;
import rice.p2p.past.rawserialization.RawPastContent;
import ceid.netcins.exo.utils.JavaSerializer;

/**
 * This class holds the message send to the Unstructured Friends Network
 * to request for searching data in a Friend's node.
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 * @version 1.0
 */
public class FriendQueryMessage extends ContinuationMessage {

	private static final long serialVersionUID = -3425651882785233815L;

	public static final short TYPE = MessageType.FriendQuery;

	// whether or not this message has been cached
	private boolean cached = false;

	// the list of nodes where this message has been
	private NodeHandle handle;

	// QueryPDU holds the query terms
	private QueryPDU queryPDU;

	// Count of overlay hops or messages between two overlay nodes
	private int hops;

	/**
	 * Constructor
	 * 
	 * @param uid
	 *            The unique id
	 * @param source
	 *            The source address
	 * @param dest
	 *            The destination address
	 */
	public FriendQueryMessage(int uid, NodeHandle source, Id dest,
			QueryPDU qpdu) {
		super(uid, source, dest);

		this.queryPDU = qpdu;
		this.hops = 0;
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
	 * Getter for PDU
	 * 
	 * @return
	 */
	public QueryPDU getQueryPDU() {
		return queryPDU;
	}

	/**
	 * Getter for hops
	 * 
	 * @return
	 */
	public int getHops() {
		return hops;
	}

	/**
	 * Add one more hop to the counter
	 */
	public void addHop() {
		hops++;
	}

	/**
	 * Returns a string representation of this message
	 * 
	 * @return A string representing this message
	 */
	@Override
	public String toString() {
		return "[FriendQueryMessage for " + id + " data " + response + "]";
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

		// Hop count serialization
		buf.writeInt(hops);

		// Java serialization is used for the serialization of the QueryPDU
		JavaSerializer.serialize(buf, queryPDU);
	}

	public static FriendQueryMessage build(InputBuffer buf, Endpoint endpoint,
			PastContentDeserializer pcd) throws IOException {
		byte version = buf.readByte();
		switch (version) {
		case 0:
			return new FriendQueryMessage(buf, endpoint, pcd);
		default:
			throw new IOException("Unknown Version: " + version);
		}
	}

	private FriendQueryMessage(InputBuffer buf, Endpoint endpoint,
			PastContentDeserializer pcd) throws IOException {
		super(buf, endpoint);
		if (serType == S_SUB) {
			short contentType = buf.readShort();
			response = pcd.deserializePastContent(buf, endpoint, contentType);
		}
		if (buf.readBoolean())
			handle = endpoint.readNodeHandle(buf);
		cached = buf.readBoolean();

		// Hop count deserialization
		hops = buf.readInt();

		// Java deserialization
		queryPDU = (QueryPDU) JavaSerializer.deserialize(buf, endpoint);
	}

}
