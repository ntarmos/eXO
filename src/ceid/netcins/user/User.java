package ceid.netcins.user;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import rice.p2p.commonapi.Id;
import ceid.netcins.catalog.SocialCatalog;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.social.SocialBookMark;
import ceid.netcins.social.SocialLink;
import ceid.netcins.social.TagCloud;

/**
 * This class represents a User entity. User includes all the necessary
 * information about a user.
 * 
 * @author Andreas Loupasakis
 */
public class User {

	// User unique identifier created by SHA-1 hash function
	private Id uid;

	// User nick(screen) name in the network. This name will be indexed as a
	// field in the userProfile!
	private String username;

	// This is the set of "terms" that describe the user.
	private ContentProfile userProfile;

	// The list of friends' UIDs
	private List<Friend> friends;

	// Friendship requests pending to be confirmed by user
	private Vector<FriendRequest> pendingIncomingFReq;

	// Friendship requests that user waits to be approved by his candidate friends
	private Vector<Id> pendingOutgoingFReq;

	// Map of shared files with their corresponding SHA-1 checksums.
	// TIP : SHA-1 checksum is returned by "libextractor", so we need to use
	// buildId(String) to obtain the Id instance.
	private Map<Id, File> sharedContent;

	// Checksum or synonym Id with the the corresponding content profile
	// TODO : This should be later be merged with the sharedContent!
	private Map<Id, ContentProfile> sharedContentProfile;

	// The content Ids (checksums) mapped with their corresponding TagCloud.
	// TAGEE's PERSPECTIVE (owner of content)
	private Map<Id, TagCloud> contentTagClouds;

	// The tag Ids mapped with their corresponding Content Profiles of contents,
	// which have been tagged by this user.
	// TAGER's PERSPECTIVE (non-owner of content)
	// alternatively Map<Id,SocialCatalog>
	private Map<String, SocialCatalog> invertedTagContentList;

	// Bookmarks : URLBookMarks etc.
	// Maps Bookmark Id (bid) to the set of tags, name, description etc.
	private Map<Id, SocialBookMark> bookMarks;

	// TODO: Implement this, Describe design etc.
	@SuppressWarnings("unused")
	private Map<Id, SocialLink> sociallinks;

	/**
	 * Constructor of user entity
	 * 
	 * @param uid
	 */
	public User(Id uid) {
		this(uid, null, null);
	}

	/**
	 * Constructor of user entity
	 * 
	 * @param uid
	 * @param username
	 */
	public User(Id uid, String username) {
		this(uid, username, null);
	}

	/**
	 * Constructor of user entity
	 * 
	 * @param username
	 * @param userProfile
	 */
	public User(Id uid, String username, ContentProfile userProfile) {
		this(uid, username, userProfile, null);
	}

	/**
	 * Constructor of user entity
	 * 
	 * @param username
	 * @param userProfile
	 * @param friends
	 */
	public User(Id uid, String username, ContentProfile userProfile,
			List<Friend> friends) {
		this.uid = uid;
		this.username = username;
		this.userProfile = userProfile;
		if (friends == null)
			this.friends = new Vector<Friend>();
		else
			this.friends = friends;
		this.pendingIncomingFReq = new Vector<FriendRequest>();
		this.pendingOutgoingFReq = new Vector<Id>();
		this.sharedContent = new HashMap<Id, File>();
		this.sharedContentProfile = new HashMap<Id, ContentProfile>();
		this.bookMarks = new HashMap<Id, SocialBookMark>();
		this.contentTagClouds = new HashMap<Id, TagCloud>();
		this.invertedTagContentList = new HashMap<String, SocialCatalog>();
	}

	public void setUserProfile(ContentProfile userProfile) {
		this.userProfile = userProfile;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}

	public ContentProfile getCompleteUserProfile() {
		return userProfile;
	}

	public ContentProfile getPublicUserProfile() {
		return userProfile.getPublicPart();
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public Id getUID() {
		return uid;
	}

	public String getUsername() {
		return username;
	}

	public Vector<FriendRequest> getPendingIncomingFReq() {
		return pendingIncomingFReq;
	}

	public Vector<Id> getPendingOutgoingFReq() {
		return pendingOutgoingFReq;
	}

	public Map<Id, File> getSharedContent() {
		return sharedContent;
	}

	public Map<Id, ContentProfile> getSharedContentProfile() {
		return sharedContentProfile;
	}

	public Map<Id, SocialBookMark> getBookMarks() {
		return bookMarks;
	}

	public Map<Id, TagCloud> getContentTagClouds() {
		return contentTagClouds;
	}

	public Map<String, SocialCatalog> getTagContentList() {
		return invertedTagContentList;
	}

	/**
	 * Add a friend's uid in the friend list
	 * 
	 * @param friend
	 */
	public void addFriend(Friend friend) {
		friends.add(friend);
	}

	public void addPendingIncomingFReq(FriendRequest freq) {
		pendingIncomingFReq.add(freq);
	}

	public void addPendingOutgoingFReq(Id uid) {
		pendingOutgoingFReq.add(uid);
	}

	public void addSharedContent(Id checksum, File file) {
		sharedContent.put(checksum, file);
	}

	public void addSharedContentProfile(Id checksum, ContentProfile cp) {
		sharedContentProfile.put(checksum, cp);
	}

	public void addBookMark(Id bid, SocialBookMark sbm) {
		bookMarks.put(bid, sbm);
	}

	public void addContentTagCloud(Id cid, TagCloud tc) {
		contentTagClouds.put(cid, tc);
	}

	public void addTagContentList(String tag, SocialCatalog cat) {
		invertedTagContentList.put(tag, cat);
	}

	public void removeFriend(Friend friend) {
		friends.remove(friend);
	}

	public void removePendingIncomingFReq(FriendRequest freq) {
		pendingIncomingFReq.remove(freq);
	}

	public void removePendingOutgoingFReq(Id uid) {
		pendingOutgoingFReq.remove(uid);
	}

	public void removeSharedContent(Id checksum) {
		sharedContent.remove(checksum);
	}

	public void removeSharedContentProfile(Id checksum) {
		sharedContentProfile.remove(checksum);
	}

	public void removeBookMark(Id bid) {
		bookMarks.remove(bid);
	}

	public void removeContentTagCloud(Id cid) {
		contentTagClouds.remove(cid);
	}

	public void removeTagContentList(String tag) {
		invertedTagContentList.remove(tag);
	}
}
