package ceid.netcins.user;

import java.net.InetSocketAddress;

import rice.p2p.commonapi.Id;

/**
 * This class contains the necessary data of an entry in the friends list.
 * Important parts of such an entry are the UID, the socket address (IP, port)
 * and the screen name of the user's friend. IP, port may be null indicating 
 * that we are in simulation mode.
 * 
 * @author Andreas Loupasakis
 */
public class Friend {

	// User unique identifier created by SHA-1 hash function
	private Id uid;

	// IP address of the friend
	private InetSocketAddress sockAddr = null;

	// The screen name of the friend.
	private String screenName = "Anonymous";

	public Friend(Id uid, String screenName) {
		this(uid, screenName, null);
	}

	public Friend(Id uid, String screenName, InetSocketAddress sockAddr) {
		this.uid = uid;
		this.sockAddr = sockAddr;
		this.screenName = screenName;
	}

	public void setName(String name) {
		screenName = name;
	}

	public void setIp(InetSocketAddress ip) {
		this.sockAddr = sockAddr;
	}

	public Id getUID() {
		return this.uid;
	}

	public String getName() {
		return this.screenName;
	}

	public InetSocketAddress getSockAddr() {
		return this.sockAddr;
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
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n - Friend UID : " + this.uid);
		if (this.screenName != null)
			buffer.append("\n - Friend's Screen Name : " + this.screenName);
		else
			buffer.append("\n - Friend's Screen Name : n/a");
		if (this.sockAddr != null)
			buffer.append("\n - Friend's IP Address : " + this.sockAddr);
		else
			buffer.append("\n - Friend's IP Address : n/a\n");

		return buffer.toString();
	}
}
