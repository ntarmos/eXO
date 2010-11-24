/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.similarity;

import rice.Continuation;
import ceid.netcins.catalog.Catalog;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.messages.QueryPDU;

/**
 * A request to the Scorer thread
 * 
 * @author andy
 * @version 1.0
 */
public class SimilarityRequest {

	// TODO: change the class to Query
	private String[] query;
	private int type = QueryPDU.CONTENTQUERY; // default is content query

	// Source user's profile
	private ContentProfile sourceUserProfile = null;

	// The Catalog which contains the entries (user or content) we want to score
	private Catalog catalog;

	// The result which will fill in the Scorer thread and the response will be
	// routed
	@SuppressWarnings("unchecked")
	private Continuation result;

	// The number of results to return
	private int k;

	// This var holds the hops or messages for the whole roundtrip of the
	// request-response
	private int messagesCounter;

	/**
	 * Constructor with the default params
	 * 
	 * @param catalog
	 * @param query
	 * @param result
	 *            Feeds the result back to Selector through a call to
	 *            endpoint.route
	 */
	@SuppressWarnings("unchecked")
	public SimilarityRequest(Catalog catalog, String[] query,
			Continuation result) {
		this.catalog = catalog;
		this.query = query;
		this.result = result;
		this.sourceUserProfile = null;
		this.k = QueryPDU.RETURN_ALL;
	}

	/**
	 * Constructor which defines a specific query type
	 * 
	 * @param catalog
	 * @param query
	 * @param type
	 *            It defines a specific type of issued query
	 * @param result
	 *            Feeds the result back to Selector through a call to
	 *            endpoint.route
	 */
	@SuppressWarnings("unchecked")
	public SimilarityRequest(Catalog catalog, String[] query, int type, int k,
			Continuation result) {
		this.catalog = catalog;
		this.query = query;
		this.result = result;
		this.type = type;
		this.sourceUserProfile = null;
		this.k = k;
	}

	/**
	 * Constructor for enhanced scoring, using the source user's profile to
	 * enhance the results.
	 * 
	 * @param catalog
	 * @param query
	 * @param type
	 *            It defines a specific type of issued query
	 * @param userProfile
	 *            Defines the source user's profile
	 * @param result
	 *            Feeds the result back to Selector through a call to
	 *            endpoint.route
	 */
	@SuppressWarnings("unchecked")
	public SimilarityRequest(Catalog catalog, String[] query, int type, int k,
			ContentProfile userProfile, Continuation result) {
		this.catalog = catalog;
		this.query = query;
		this.result = result;
		this.type = type;
		this.sourceUserProfile = userProfile;
		this.k = k;
	}

	/**
	 * Constructor for enhanced scoring, using the source user's profile to
	 * enhance the results.
	 * 
	 * @param catalog
	 * @param query
	 * @param type
	 *            It defines a specific type of issued query
	 * @param userProfile
	 *            Defines the source user's profile
	 * @param result
	 *            Feeds the result back to Selector through a call to
	 *            endpoint.route
	 */
	@SuppressWarnings("unchecked")
	public SimilarityRequest(Catalog catalog, String[] query, int type, int k,
			ContentProfile userProfile, Continuation result, int msgcounter) {
		this.catalog = catalog;
		this.query = query;
		this.result = result;
		this.type = type;
		this.sourceUserProfile = userProfile;
		this.messagesCounter = msgcounter;
		this.k = k;
	}

	public String[] getQuery() {
		return query;
	}

	public int getType() {
		return type;
	}

	public int getK() {
		return k;
	}

	public ContentProfile getSourceUserProfile() {
		return sourceUserProfile;
	}

	public Catalog getCatalog() {
		return catalog;
	}

	@SuppressWarnings("unchecked")
	public Continuation getContinuation() {
		return result;
	}

	public int getMessagesCounter() {
		return messagesCounter;
	}

}
