

package ceid.netcins.messages;

import java.io.Serializable;

import rice.p2p.commonapi.Id;

/**
 * This class contains the data (tags,contentId,UserId) for the social tagging
 * 
 * @author Andreas Loupasakis
 */
public class TagPDU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2339647551267755148L;

	// The Id of the tagged entity
	private Id taggedId;

	private String[] tags;

	public TagPDU(Id taggedId, String[] tags) {

		this.taggedId = taggedId;
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
	public Id getTaggedId() {
		return taggedId;
	}
}
