

package ceid.netcins.request;

import ceid.netcins.content.ContentProfileFactory;

import com.sun.net.httpserver.HttpExchange;

/**
 * Represents a request for searching specific content through a number of
 * content terms
 * 
 * @author Andreas Loupasakis
 */
public class SearchContentRequest extends Request {

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

	public SearchContentRequest(String query) {
		this(query, SIMPLE, RANDOMSOURCE);
	}

	public SearchContentRequest(String query, int type) {
		this(query, type, RANDOMSOURCE);
	}

	public SearchContentRequest(String query, int type, int source) {
		this(query, type, source, RETURN_ALL);
	}

	public SearchContentRequest(String query, int type, int source, int k) {
		this(query, type, source, k, ContentProfileFactory.DEFAULT_DELIMITER);
	}

	public SearchContentRequest(String query, int type, int source, int k,
			String delimiter) {
		super();
		this.query = query;
		this.queryType = type;
		this.source = source;
		this.k = k;
		this.delimiter = DELIMITER;
	}

	public SearchContentRequest(String query, int source, HttpExchange t) {
		super();
		this.query = query;
		this.source = source;
		this.t = t;
	}

	public HttpExchange t;

	/**
	 * Getter for the query
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
	 * Getter for the source variable.
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
	 * Getter for query delimiter
	 * 
	 * @return
	 */
	public String getDelimiter() {
		return this.delimiter;
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
