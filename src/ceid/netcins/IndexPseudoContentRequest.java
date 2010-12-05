

package ceid.netcins;

import java.util.HashMap;
import java.util.Map;

import ceid.netcins.content.ContentProfileFactory;

/**
 * Used for simulation purposes on large datasets
 * 
 * @author Andreas Loupasakis
 */
public class IndexPseudoContentRequest extends Request {

	// The field terms pair (Terms are all in one String per pair)
	// It includes identifier that is mapped to checksum through SHA-1
	private Map<String, String> contentProfile;

	// The unique string which describes the resource (just a term after all)
	private String identifier;

	// The delimiter which separates the terms
	private String delimiter;

	// The source node number (Simulator only)
	private int source;
	public static final int RANDOMSOURCE = -1;

	// Only for SAX!
	public IndexPseudoContentRequest() {
		this.source = RANDOMSOURCE;
		this.delimiter = ContentProfileFactory.DEFAULT_DELIMITER;
		this.contentProfile = new HashMap<String, String>();
	}

	public void addToContentProfile(String fieldname, String keywords) {
		this.contentProfile.put(fieldname, keywords);
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public IndexPseudoContentRequest(String identifier,
			Map<String, String> cprofile) {
		this(identifier, cprofile, RANDOMSOURCE);
	}

	public IndexPseudoContentRequest(String identifier,
			Map<String, String> cprofile, int source) {
		this(identifier, cprofile, source,
				ContentProfileFactory.DEFAULT_DELIMITER);
	}

	public IndexPseudoContentRequest(String identifier,
			Map<String, String> cprofile, int source, String delimiter) {
		super();
		this.identifier = identifier;
		this.contentProfile = cprofile;
		this.source = source;
		this.delimiter = delimiter;
	}

	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Getter for the Map contentProfile
	 * 
	 * @return
	 */
	public Map<String, String> getContentProfileMap() {
		return this.contentProfile;
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
