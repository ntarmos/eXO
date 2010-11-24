/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.messages;

/**
 * This class contains the message for friendship request.
 * 
 * @author andy
 */
public class FriendReqPDU {

	// A message written from the request source user
	private String message;

	// Source user's name.
	private String screenname;

	public FriendReqPDU(String data, String screenname) {
		this.message = data;
		this.screenname = screenname;
	}

	public String getMessage() {
		return message;
	}

	public String getScreenName() {
		return screenname;
	}
}
