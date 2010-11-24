/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins;

import ceid.netcins.content.ContentProfileFactory;
import ceid.netcins.simulator.SimMain;

/**
 * This is a search request
 * 
 * @author andy
 */
public class SearchUserRequest extends Request {

	// The string of terms
	private String query;

	// The delimiter which separates the terms
	private String delimiter;

	// The k of top K (number of results to return)
	private int k;
	public final static int RETURN_ALL = -1;

	// Enhanced or Simple query
	public final static int SIMPLE = 0;
	public final static int ENHANCED = 1;
	private int queryType;

	// The source node number (Simulator only)
	private int source;
	public static final int RANDOMSOURCE = -1;

	public SearchUserRequest(String query) {
		this(query, SIMPLE, RANDOMSOURCE);
	}

	public SearchUserRequest(String query, int type) {
		this(query, type, RANDOMSOURCE);
	}

	public SearchUserRequest(String query, int type, int source) {
		this(query, type, source, RETURN_ALL);
	}

	public SearchUserRequest(String query, int type, int source, int k) {
		this(query, type, source, k, ContentProfileFactory.DEFAULT_DELIMITER);
	}

	public SearchUserRequest(String query, int type, int source, int k,
			String delimiter) {
		super();
		this.query = query;
		this.queryType = type;
		this.source = source;
		this.k = k;
		this.delimiter = SimMain.DELIMITER;
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
	 * Getter for the queryType
	 * 
	 * @return
	 */
	public int getQueryType() {
		return queryType;
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

	/**
	 * Getter for query delimiter
	 * 
	 * @return
	 */
	public String getDelimiter() {
		return this.delimiter;
	}
}
