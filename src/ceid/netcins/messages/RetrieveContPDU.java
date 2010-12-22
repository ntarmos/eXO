

package ceid.netcins.messages;

import java.io.Serializable;

import rice.p2p.commonapi.Id;

/**
 * Holds the Content Identifier.
 * 
 * @author Andreas Loupasakis
 */
public class RetrieveContPDU implements Serializable {

	// The user Id
	// private Id uid;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3861889468881922351L;

	// The checksum
	private Id contentId;

	// Flag for Tagclouds
	// TODO : Implement it in the requests
	private boolean cloudflag;

	public RetrieveContPDU(Id contentId) {
		// this.uid = uid;
		this.contentId = contentId;
		this.cloudflag = true;
	}

	// public Id getUID(){
	// return uid;
	// }

	public Id getContentId() {
		return contentId;
	}

	public boolean getCloudFlag() {
		return cloudflag;
	}
}
