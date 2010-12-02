package ceid.netcins;

import java.util.HashMap;
import java.util.Map;

import ceid.netcins.content.ContentProfileFactory;

/**
 * It is used to issue a request for user profile indexing. The profile contains
 * separate fields which must be provided as a map with the corresponding
 * keywords or phrases.
 * 
 * TODO : Include extra details such as user credentials, addresses etc.
 * 
 * @author Andreas Loupasakis
 */
public class IndexUserRequest extends Request {

	// The field terms pair (Terms are all in one String per pair)
	private Map<String, String> userProfile;

	// The delimiter which separates the terms
	private String delimiter;

	// The source node number (Simulator only)
	private int source;
	public static final int RANDOMSOURCE = -1;

	// Only for SAX!!!!!!!
	public IndexUserRequest() {
		this.source = RANDOMSOURCE;
		this.delimiter = ContentProfileFactory.DEFAULT_DELIMITER;
		this.userProfile = new HashMap<String, String>();
	}

	public void addToUserProfile(String fieldname, String keywords) {
		this.userProfile.put(fieldname, keywords);
	}

	public void setSource(int source) {
		this.source = source;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public IndexUserRequest(Map<String, String> uprofile) {
		this(uprofile, RANDOMSOURCE);
	}

	public IndexUserRequest(Map<String, String> userProfile, int source) {
		this(userProfile, source, ContentProfileFactory.DEFAULT_DELIMITER);
	}

	public IndexUserRequest(Map<String, String> userProfile, int source,
			String delimiter) {
		super();
		this.userProfile = userProfile;
		this.source = source;
		this.delimiter = delimiter;
	}

	/**
	 * Getter for the Map userProfile
	 * 
	 * @return
	 */
	public Map<String, String> getUserProfileMap() {
		return this.userProfile;
	}

	/**
	 * Getter for source number
	 * 
	 * @return
	 */
	public int getSource() {
		return this.source;
	}

	/**
	 * Getter for index delimiter
	 * 
	 * @return
	 */
	public String getDelimiter() {
		return this.delimiter;
	}

}
