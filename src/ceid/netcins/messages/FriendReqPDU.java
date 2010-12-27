package ceid.netcins.messages;

import java.io.Serializable;

/**
 * This class contains the message for friendship request.
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public class FriendReqPDU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8560411161085968272L;

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
