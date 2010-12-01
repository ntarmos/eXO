

package ceid.netcins;

import ceid.netcins.messages.QueryPDU;

/**
 * 
 * @author Andreas Loupasakis
 */
public class RandomQueriesRequest extends Request {

	// All the ENHANCED QUERIES use the similarity of source user's profile
	// 0,1,2,3 ...
	// default user
	private int type;

	// default -1 = ALL
	private int k = -1;

	// The total number of queries
	// >1
	// default 10
	private int queries;

	// The number of keywords used per query
	// default 2
	private int keywords;

	public RandomQueriesRequest() {
		this(QueryPDU.USERQUERY, 2, 100);
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setKeywords(int keywords) {
		this.keywords = keywords;
	}

	public void setQueries(int queries) {
		this.queries = queries;
	}

	public void setK(int k) {
		this.k = k;
	}

	public RandomQueriesRequest(int type) {
		this(type, 2, 100);
	}

	public RandomQueriesRequest(int type, int keywords) {
		this(type, keywords, 100);
	}

	public RandomQueriesRequest(int type, int keywords, int queries) {
		this(type, keywords, queries, QueryPDU.RETURN_ALL);
	}

	public RandomQueriesRequest(int type, int keywords, int queries, int k) {
		this.queries = queries;
		this.keywords = keywords;
		this.type = type;
		this.k = k;
	}

	/**
	 * Getter for the type
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Getter for the type
	 * 
	 * @return
	 */
	public int getK() {
		return k;
	}

	/**
	 * Getter for the percent number
	 * 
	 * @return
	 */
	public int getQueries() {
		return queries;
	}

	/**
	 * Getter for the keywords number
	 * 
	 * @return
	 */
	public int getKeywords() {
		return keywords;
	}

}
