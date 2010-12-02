package ceid.netcins;

import rice.p2p.commonapi.Id;

/**
 * A request by the tager to the tagee's node for tagging some content object
 * 
 * @author Andreas Loupasakis
 */
public class TagContentRequest extends Request {

	// The string of tags
	private String tags;

	// The number of shared content to be tagged (as given by the stats on
	// node's shared content list). This should be replaced with Id in a
	// xml based testing. (Simulator only)
	// Only for dirty debugging.
	@Deprecated
	private String cid;

	// The node of content owner (Simulator only)
	private int uid;

	// Real checksum or synonym of the content object.
	private Id contentId;

	// Real uid of user.
	private Id userId;

	// The source node number (Simulator only)
	private int source;
	public static final int RANDOMSOURCE = -1;

	/**
	 * Constructor For debugging only!
	 * 
	 * @param tags
	 * @param cid
	 * @param uid
	 * @deprecated
	 */
	@Deprecated
	public TagContentRequest(String tags, String cid, int uid) {
		this(tags, cid, uid, RANDOMSOURCE);
	}

	/**
	 * Constructor For debugging only!
	 * 
	 * @param tags
	 * @param cid
	 * @param uid
	 * @param source
	 * @deprecated
	 */
	@Deprecated
	public TagContentRequest(String tags, String cid, int uid, int source) {
		super();
		this.tags = tags;
		this.source = source;
		this.uid = uid;
		this.cid = cid;
		this.contentId = null;
		this.userId = null;
	}

	/**
	 * Constructor (Simulator only)
	 * 
	 * @param tags
	 * @param contentId
	 * @param uid
	 * @param source
	 */
	public TagContentRequest(String tags, Id contentId, int uid, int source) {
		super();
		this.tags = tags;
		this.source = source;
		this.contentId = contentId;
		this.uid = uid;
		this.userId = null;
	}

	/**
	 * Constructor for real deployment
	 * 
	 * @param tags
	 * @param contentId
	 * @param userId
	 * @param source
	 */
	public TagContentRequest(String tags, Id contentId, Id userId, int source) {
		super();
		this.tags = tags;
		this.source = source;
		this.contentId = contentId;
		this.userId = userId;
	}

	/**
	 * Getter for the tags.
	 * 
	 * @return
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Getter for the source.
	 * 
	 * @return
	 */
	public int getSource() {
		return source;
	}

	/**
	 * Getter for the cid.
	 * 
	 * @return
	 */
	public String getCID() {
		return cid;
	}

	/**
	 * Getter for the uid.
	 * 
	 * @return
	 */
	public int getUID() {
		return uid;
	}

	/**
	 * Getter for the contentId.
	 * 
	 * @return
	 */
	public Id getContentId() {
		return contentId;
	}

	/**
	 * Getter for the userId.
	 * 
	 * @return
	 */
	public Id getUserId() {
		return userId;
	}
}
