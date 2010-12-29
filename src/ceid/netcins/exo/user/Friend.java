package ceid.netcins.exo.user;


import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;

/**
 * This class contains the necessary data of an entry in the friends list.
 * Important parts of such an entry are the UID, the NodeHandle 
 * (which may contain the socket address - IP, port), and the screen name 
 * of the user's friend. IP, port may be null indicating 
 * that we are in simulation mode.
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public class Friend {

	// User unique identifier created by SHA-1 hash function
	private Id uid;

	// The node handle of the friend
	private NodeHandle nodeHandle = null;

	// The screen name of the friend.
	private String screenName = "Anonymous";

	public Friend(Id uid, String screenName) {
		this(uid, screenName, null);
	}

	public Friend(Id uid, String screenName, NodeHandle nhandle) {
		this.uid = uid;
		this.nodeHandle = nhandle;
		this.screenName = screenName;
	}

	public void setName(String name) {
		screenName = name;
	}

	public void setNodeHandle(NodeHandle nodeHandle) {
		this.nodeHandle = nodeHandle;
	}

	public Id getUID() {
		return this.uid;
	}

	public String getName() {
		return this.screenName;
	}

	public NodeHandle getNodeHandle() {
		return this.nodeHandle;
	}

	/**
	 * Used to compare two friends. Two friends are the same if: they have the
	 * same UID.
	 * 
	 * @param o The other friend to compare
	 * @return True if are the same friend.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Friend)) {
			return false;
		}
		Friend f = (Friend) o;

		return f.getUID().equals(uid);
	}

	@Override
	public int hashCode() {
		return uid.hashCode();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n - Friend UID : " + this.uid);
		if (this.screenName != null)
			buffer.append("\n - Friend's Screen Name : " + this.screenName);
		else
			buffer.append("\n - Friend's Screen Name : n/a");
		if (this.nodeHandle != null)
			buffer.append("\n - Friend's NodeHandle : " + this.nodeHandle);
		else
			buffer.append("\n - Friend's NodeHandle : n/a\n");

		return buffer.toString();
	}
}
