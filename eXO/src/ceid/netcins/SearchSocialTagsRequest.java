/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins;

/**
 * A request for social query.
 * 
 * @author andy
 */
public class SearchSocialTagsRequest extends Request {

	// The string of query terms
	private String qterms;

	// This array contains the number of each user we want to visit
	// (Simulator only)
	private int[] nodeNumbers;

	// An array which contains the real uids of users we want to visit.
	private String[] userIds;

	// The source node number (Simulator only)
	private int source;
	public static final int RANDOMSOURCE = -1;

	/**
	 * Constructor (Simulator only)
	 * 
	 * @param qterms
	 * @param nodeNumbers
	 * @deprecated
	 */
	@Deprecated
	public SearchSocialTagsRequest(String qterms, int[] nodeNumbers) {
		this(qterms, nodeNumbers, RANDOMSOURCE);
	}

	/**
	 * Constructor (Simulator only)
	 * 
	 * @param qterms
	 * @param nodeNumbers
	 * @param source
	 * @deprecated
	 */
	@Deprecated
	public SearchSocialTagsRequest(String qterms, int[] nodeNumbers, int source) {
		super();
		this.qterms = qterms;
		this.source = source;
		this.nodeNumbers = nodeNumbers;
		this.userIds = null;
	}

	/**
	 * Constructor
	 * 
	 * @param qterms
	 * @param uids
	 */
	public SearchSocialTagsRequest(String qterms, String[] uids) {
		this(qterms, uids, RANDOMSOURCE);
	}

	/**
	 * Constructor
	 * 
	 * @param qterms
	 * @param uids
	 * @param source
	 */
	public SearchSocialTagsRequest(String qterms, String[] uids, int source) {
		super();
		this.qterms = qterms;
		this.source = source;
		this.userIds = uids;
	}

	/**
	 * Getter for the query terms.
	 * 
	 * @return
	 */
	public String getQuery() {
		return qterms;
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
	 * Getter for the uids.
	 * 
	 * @return
	 */
	public String[] getUIDs() {
		return userIds;
	}

	/**
	 * Getter for the node numbers.
	 * 
	 * @return
	 */
	public int[] getNodeNumbers() {
		return nodeNumbers;
	}
}
