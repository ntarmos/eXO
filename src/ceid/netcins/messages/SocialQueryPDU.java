

package ceid.netcins.messages;

import java.io.Serializable;

import ceid.netcins.content.ContentProfile;

/**
 * Holds the tags (query terms) to search in each social node
 * 
 * @author Andreas Loupasakis
 */
public class SocialQueryPDU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5093183142130751857L;
	// All the ENHANCED QUERIES use the similarity of source user's profile
	private int type = CONTENTQUERY; // default
	public static final int CONTENTQUERY = 0;
	public static final int CONTENT_ENHANCEDQUERY = 1;
	public static final int USERQUERY = 2;
	public static final int USER_ENHANCEDQUERY = 3;
	// HYBRID uses the raw terms for content and user similarity
	public static final int HYBRIDQUERY = 4;
	public static final int HYBRID_ENHANCEDQUERY = 5;
	public static final int URLQUERY = 6;

	// The packet data (query terms)
	private String[] data;

	// Source user's profile.
	private ContentProfile userProfile;

	/**
	 * Constructor
	 * 
	 * @param data
	 */
	public SocialQueryPDU(String[] data) {
		this.data = data;
		this.userProfile = null;
	}

	/**
	 * Constructor
	 * 
	 * @param data
	 * @param type
	 */
	public SocialQueryPDU(String[] data, int type) {
		this.data = data;
		this.type = type;
		this.userProfile = null;
	}

	/**
	 * Constructor
	 * 
	 * @param data
	 * @param type
	 * @param userProfile
	 */
	public SocialQueryPDU(String[] data, int type, ContentProfile userProfile) {
		this.data = data;
		this.type = type;
		this.userProfile = userProfile;
	}

	/**
	 * Getter for query terms
	 * 
	 * @return
	 */
	public String[] getData() {
		return data;
	}

	/**
	 * Getter for type
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Getter for source user profile
	 * 
	 * @return
	 */
	public ContentProfile getSourceUserProfile() {
		return userProfile;
	}
}
