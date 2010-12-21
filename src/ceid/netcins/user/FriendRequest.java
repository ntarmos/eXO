package ceid.netcins.user;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import ceid.netcins.messages.FriendReqPDU;

/**
 * 
 * @author Andreas Loupasakis
 */
public class FriendRequest {

	// The pdu from the friend request message
	private FriendReqPDU freqpdu;

	// The friend's uid
	private Id uid;
	
	// The source node handle
	private NodeHandle sourceHandle;

	public FriendRequest(FriendReqPDU freqpdu, Id uid, NodeHandle source) {
		this.freqpdu = freqpdu;
		this.uid = uid;
		this.sourceHandle = source;
	}

	public FriendReqPDU getFriendReqPDU() {
		return freqpdu;
	}

	public Id getUID() {
		return uid;
	}
	
	public NodeHandle getSourceHandle(){
		return this.sourceHandle;
	}

	/**
	 * Used to compare two request. Two friend reqs are the same if: they have
	 * the same UID
	 * 
	 * @param o
	 *            The comparing friend request
	 * @return True if are the same.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FriendRequest)) {
			return false;
		}
		FriendRequest fr = (FriendRequest) o;

		return fr.getUID().equals(uid);
	}
	
	@Override
	public int hashCode() {
		return uid.hashCode();
	}
}
