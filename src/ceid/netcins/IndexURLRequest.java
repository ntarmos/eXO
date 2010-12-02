

package ceid.netcins;

import java.net.URL;
import java.util.Map;

/**
 * This is the request from ui to dispatcher thread for URL indexing to the
 * corresponding.
 * 
 * @author Andreas Loupasakis
 */
public class IndexURLRequest extends Request {

	// The field terms pair (Terms are all in one String per pair)
	private Map<String, String> urlProfile;

	// The Url address
	private URL url;

	// The source node number (Simulator only)
	private int source;
	public static final int RANDOMSOURCE = -1;

	public IndexURLRequest(URL url, Map<String, String> uprofile) {
		this(url, uprofile, RANDOMSOURCE);
	}

	public IndexURLRequest(URL url, Map<String, String> urlProfile, int source) {
		super();
		this.url = url;
		this.urlProfile = urlProfile;
		this.source = source;
	}

	/**
	 * Getter for the Map urlProfile
	 * 
	 * @return
	 */
	public Map<String, String> getURLProfileMap() {
		return this.urlProfile;
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
	 * Gettern for the url
	 * 
	 * @return
	 */
	public URL getURL() {
		return url;
	}
}
