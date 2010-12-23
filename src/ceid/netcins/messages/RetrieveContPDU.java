

package ceid.netcins.messages;

import java.io.Serializable;

import rice.p2p.commonapi.Id;

/**
 * Holds the Content Identifier.
 * 
 * @author Andreas Loupasakis
 */
public class RetrieveContPDU implements Serializable {

	private static final long serialVersionUID = -3861889468881922351L;

	// The content ID
	private Id contentId;

	// Flag for Tagclouds
	private boolean cloudflag;

	public RetrieveContPDU(Id contentId) {
		this.contentId = contentId;
		this.cloudflag = true;
	}

	public Id getContentId() {
		return contentId;
	}

	public boolean getCloudFlag() {
		return cloudflag;
	}
}
