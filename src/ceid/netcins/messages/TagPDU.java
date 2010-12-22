

package ceid.netcins.messages;

import java.io.Serializable;
import java.util.Arrays;

import rice.p2p.commonapi.Id;
import ceid.netcins.content.ContentField;
import ceid.netcins.content.ContentProfile;

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

	private ContentProfile tags;

	public TagPDU(Id taggedId, ContentProfile tags) {
		this.taggedId = taggedId;
		this.tags = tags;
	}

	public TagPDU(Id taggedId, ContentField[] tags) {

		this.taggedId = taggedId;
		this.tags = new ContentProfile(Arrays.asList(tags));
	}

	/**
	 * Getter for tags
	 * 
	 * @return
	 */
	public ContentProfile getTags() {
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
