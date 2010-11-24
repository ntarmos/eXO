/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.messages;

import java.io.Serializable;

import rice.p2p.commonapi.Id;

/**
 * This class contains the data (tags,contentId,UserId) for the social tagging
 * 
 * @author andy
 */
public class TagContentPDU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2339647551267755148L;

	// The checksum of the taging object
	private Id contentId;

	private String[] tags;

	public TagContentPDU(Id contentId, String[] tags) {

		this.contentId = contentId;
		this.tags = tags;
	}

	/**
	 * Getter for tags
	 * 
	 * @return
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * Getter for the contentId
	 * 
	 * @return
	 */
	public Id getContentId() {
		return contentId;
	}
}
