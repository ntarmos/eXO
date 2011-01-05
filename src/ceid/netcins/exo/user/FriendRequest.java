package ceid.netcins.exo.user;

import java.io.Serializable;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import ceid.netcins.exo.messages.FriendReqPDU;

/**
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public class FriendRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6311000103670100898L;

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
