package ceid.netcins.messages;

/**
 *
 * @author Andreas Loupasakis
 */
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.OutputBuffer;
import rice.p2p.past.messaging.ContinuationMessage;
import rice.p2p.past.rawserialization.PastContentDeserializer;
import rice.p2p.past.rawserialization.RawPastContent;

public class CatalogMessage extends ContinuationMessage {

	private static final long serialVersionUID = -465459501485739375L;

	public static final short TYPE = MessageType.Catalog;

	// the id of the FileID
	private Id id;

	// the data (NODELIST that should be checked across the path)
	/**
	 * DESCRIBE THE FIELD
	 */
	protected SortedSet<Id> nodeList;
	// if we want a synchronized version we must switch to hashtable
	// protected HashMap<Id,Profile> nodeList;

	// whether or not this message has been cached
	// private boolean cached = false;

	// the last node where this message has been !!! FOR NOW THIS IS BYPASSED IN
	// RAWSERIALIZATION!!!
	private NodeHandle handle;

	// the list of nodes where this message has been !!! FOR NOW THIS IS
	// BYPASSED IN RAWSERIALIZATION!!!
	public List<NodeHandle> nodeHops;

	// The hops of CEIDMsg
	public transient int ceidMsgHops = 0;

	// indicates if we are in continuous multiple retrieve mode
	public boolean continuous = false;

	// counts the physical copies we have met so far!
	public int continuous_count = 0;

	// max number of copies to retrieve in multiple retrieve mode
	public int max_copies = 0;

	// multicontinuation for the continuous retrieves
	public transient MultiContinuation multi;

	/**
	 * Constructor
	 * 
	 * @param uid
	 *            The unique id
	 * @param nodeList
	 *            the list of NodeIds which have the file
	 * @param source
	 *            The source address
	 * @param dest
	 *            The destination address
	 */
	public CatalogMessage(int uid, Id id, SortedSet<Id> nodeList,
			NodeHandle source, Id dest) {
		this(uid, id, nodeList, source, dest, false, 0, null);
	}

	/**
	 * Constructor
	 * 
	 * @param uid
	 *            The unique id
	 * @param nodeList
	 *            the list of NodeIds which have the file
	 * @param source
	 *            The source address
	 * @param dest
	 *            The destination address
	 * @param continuous
	 *            The mode we use to retrieve physical copies
	 * @oaram max_copies Indicates the max number of physical copies we want to
	 *        be retrieved
	 */
	public CatalogMessage(int uid, Id id, SortedSet<Id> nodeList,
			NodeHandle source, Id dest, boolean continuous, int max_copies,
			MultiContinuation multi) {
		super(uid, source, dest);
		this.id = id;
		this.nodeList = nodeList;
		this.nodeHops = new LinkedList<NodeHandle>();
		this.continuous = continuous;
		this.max_copies = max_copies;
		this.multi = multi;
	}

	/**
	 * Method which returns the nodeList
	 * 
	 * @return The contained nodeList
	 */
	public SortedSet<Id> getNodeList() {
		return nodeList;
	}

	/**
	 * Method which returns the id of the filename
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
	// public boolean isCached() {
	// return cached;
	// }

	/**
	 * Method which returns the previous hop (where the message was just at)
	 * 
	 * @return The previous hop
	 */
	public NodeHandle getPreviousNodeHandle() {
		return handle;
	}

	/**
	 * Sets this message as having been cached.
	 */
	// public void setCached() {
	// cached = true;
	// }

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
		this.nodeHops.add(handle);
	}

	/**
	 * Returns a string representation of this message
	 * 
	 * @return A string representing this message
	 */
	@Override
	public String toString() {
		return "[CatalogMessage with nodelist: " + nodeList + " Data "
				+ response + "]";
	}

	/***************** Raw Serialization ***************************************/

	@Override
	public void serialize(OutputBuffer buf) throws IOException {
		buf.writeByte((byte) 0); // version
		if (response != null && response instanceof RawPastContent) {
			super.serialize(buf, false);
			RawPastContent rpc = (RawPastContent) response;
			buf.writeShort(rpc.getType());
			rpc.serialize(buf);
		} else {
			super.serialize(buf, true); // HERE WE GET ALWAYS (The above is for
										// future work)
		}

		// buf.writeBoolean(handle != null);
		// if (handle != null) handle.serialize(buf);

		buf.writeShort(id.getType()); // TID serialization
		id.serialize(buf);

		// NodeList serialization
		buf.writeInt(nodeList.size()); // The number of nodeIDs
		for (Id it : nodeList) {
			buf.writeShort(it.getType()); // NodeID serialization
			it.serialize(buf);
		}
		buf.writeBoolean(continuous); // continuous or not serialization
	}

	public short getType() {
		return TYPE;
	}

	public static CatalogMessage build(InputBuffer buf, Endpoint endpoint,
			PastContentDeserializer pcd) throws IOException {
		byte version = buf.readByte();
		switch (version) {
		case 0:
			return new CatalogMessage(buf, endpoint, pcd);
		default:
			throw new IOException("Unknown Version: " + version);
		}
	}

	private CatalogMessage(InputBuffer buf, Endpoint endpoint,
			PastContentDeserializer pcd) throws IOException {
		super(buf, endpoint);
		if (serType == S_SUB) {
			short contentType = buf.readShort();
			response = pcd.deserializePastContent(buf, endpoint, contentType);
		}
		// if (buf.readBoolean())handle = endpoint.readNodeHandle(buf);
		try {
			id = endpoint.readId(buf, buf.readShort());
			nodeList = Collections.synchronizedSortedSet(new TreeSet<Id>());
			int length = buf.readInt();
			for (int i = 0; i < length; i++)
				nodeList.add(endpoint.readId(buf, buf.readShort()));
			continuous = buf.readBoolean();
		} catch (IllegalArgumentException iae) {
			System.out.println(iae + " " + this + " serType:" + serType
					+ " UID:" + getUID() + " d:" + dest + " s:" + source);
			throw iae;
		}
	}
}