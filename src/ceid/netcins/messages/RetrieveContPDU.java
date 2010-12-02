

package ceid.netcins.messages;

import java.io.Serializable;

import rice.p2p.commonapi.Id;

/**
 * Holds the File Checksum (the File identifier).
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
	private Id checksum;

	// Flag for Tagclouds
	// TODO : Implement it in the requests
	private boolean cloudflag;

	public RetrieveContPDU(Id checksum) {
		// this.uid = uid;
		this.checksum = checksum;
		this.cloudflag = true;
	}

	// public Id getUID(){
	// return uid;
	// }

	public Id getCheckSum() {
		return checksum;
	}

	public boolean getCloudFlag() {
		return cloudflag;
	}
}
