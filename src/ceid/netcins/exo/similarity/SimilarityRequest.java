package ceid.netcins.exo.similarity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import rice.Continuation;
import ceid.netcins.exo.content.ContentProfile;
import ceid.netcins.exo.messages.QueryPDU;

/**
 * A request to the Scorer thread
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 * @version 1.0
 */
public class SimilarityRequest {

	// TODO: change the class to Query
	private String[] query;
	private int type = QueryPDU.CONTENTQUERY; // default is content query

	// Source user's profile
	private ContentProfile sourceUserProfile = null;

	// The collection of (user or content) entries we want to score
	@SuppressWarnings("rawtypes")
	private Collection profileEntries;

	// The result which will fill in the Scorer thread and the response will be
	// routed
	@SuppressWarnings("rawtypes")
	private Continuation result;

	// The number of results to return
	private int k;

	// This var holds the hops or messages for the whole roundtrip of the
	// request-response
	private int messagesCounter;

	/**
	 * Constructor with the default params
	 * 
	 * @param profileEntries
	 * @param query
	 * @param result
	 *            Feeds the result back to Selector through a call to
	 *            endpoint.route
	 */
	@SuppressWarnings("rawtypes")
	public SimilarityRequest(Set profileEntries, String[] query,
			Continuation result) {
		this.profileEntries = profileEntries;
		this.query = query;
		this.result = result;
		this.sourceUserProfile = null;
		this.k = QueryPDU.RETURN_ALL;
	}

	/**
	 * Constructor which defines a specific query type
	 * 
	 * @param profileEntries
	 * @param query
	 * @param type
	 *            It defines a specific type of issued query
	 * @param result
	 *            Feeds the result back to Selector through a call to
	 *            endpoint.route
	 */
	@SuppressWarnings("rawtypes")
	public SimilarityRequest(Set profileEntries, String[] query, int type,
			int k,	Continuation result) {
		this.profileEntries = profileEntries;
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
	 * @param profileEntries
	 * @param query
	 * @param type
	 *            It defines a specific type of issued query
	 * @param userProfile
	 *            Defines the source user's profile
	 * @param result
	 *            Feeds the result back to Selector through a call to
	 *            endpoint.route
	 */
	@SuppressWarnings("rawtypes")
	public SimilarityRequest(Set profileEntries, String[] query, int type,
			int k, ContentProfile userProfile, Continuation result) {
		this.profileEntries = profileEntries;
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
	 * @param profileEntries
	 * @param query
	 * @param type
	 *            It defines a specific type of issued query
	 * @param userProfile
	 *            Defines the source user's profile
	 * @param result
	 *            Feeds the result back to Selector through a call to
	 *            endpoint.route
	 */
	@SuppressWarnings("rawtypes")
	public SimilarityRequest(Collection profileEntries, String[] query, int type,
			int k, ContentProfile userProfile, Continuation result, 
			int msgcounter) {
		this.profileEntries = profileEntries;
		this.query = query;
		this.result = result;
		this.type = type;
		this.sourceUserProfile = userProfile;
		this.messagesCounter = msgcounter;
		this.k = k;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SimilarityRequest(Vector profileEntries, String[] query, int type,
			int k, ContentProfile userProfile, Continuation result, 
			int msgcounter) {
		this((Set)null, query, type, k, userProfile, result, msgcounter);
		if (profileEntries == null)
			this.profileEntries = null;
		else {
			this.profileEntries = new HashSet();
			for (Object o : profileEntries)
				this.profileEntries.add(o);
		}
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

	@SuppressWarnings("rawtypes")
	public Collection getProfileEntries() {
		return profileEntries;
	}

	@SuppressWarnings("rawtypes")
	public Continuation getContinuation() {
		return result;
	}

	public int getMessagesCounter() {
		return messagesCounter;
	}

}
