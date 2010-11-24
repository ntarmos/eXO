/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.user;

import rice.p2p.commonapi.Id;
import ceid.netcins.messages.FriendReqPDU;

/**
 * 
 * @author andy
 */
public class FriendRequest {

	// The pdu from the friend request message
	private FriendReqPDU freqpdu;

	// The friend's uid
	private Id uid;

	public FriendRequest(FriendReqPDU freqpdu, Id uid) {
		this.freqpdu = freqpdu;
		this.uid = uid;
	}

	public FriendReqPDU getFriendReqPDU() {
		return freqpdu;
	}

	public Id getUID() {
		return uid;
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
}
