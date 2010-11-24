/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins;

/**
 * Request for url catalog searching Each term should be a URL
 * 
 * @author andy
 */
public class SearchURLRequest extends Request {

	// The string of terms
	private String query;

	// The k of top K (number of results to return)
	private int k;
	public final static int RETURN_ALL = -1;

	// The source node number (Simulator only)
	private int source;
	public static final int RANDOMSOURCE = -1;

	public SearchURLRequest(String query) {
		this(query, RANDOMSOURCE);
	}

	public SearchURLRequest(String query, int source) {
		this(query, source, RETURN_ALL);
	}

	public SearchURLRequest(String query, int source, int k) {
		super();
		this.query = query;
		this.source = source;
		this.k = k;
	}

	/**
	 * Getter for the query.
	 * 
	 * @return
	 */
	public String getQuery() {
		return query;
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
	 * Getter for the number of results, k variable.
	 * 
	 * @return
	 */
	public int getK() {
		return k;
	}

	/**
	 * Sets the query String.
	 * 
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}
}
