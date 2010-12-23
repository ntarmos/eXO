package ceid.netcins.request;

import ceid.netcins.utils.SimulatorOnly;

/**
 * This Request type represents a friendship request or approval. It depends on
 * the pendingIncomingFReq and pendingOutgoingFReq in order to determine the 
 * type.
 * 
 * @author Andreas Loupasakis
 */
public class FriendsRequest extends Request {

	// The greetings message of the friendship requester
	private String message;

	// The source node number (Simulator only)
	@SimulatorOnly
	private int source;
	@SimulatorOnly
	public static final int RANDOMSOURCE = -1;

	// The destination node number (Simulator only)
	@SimulatorOnly
	private int destination;

	public FriendsRequest(String message) {
		this(message, RANDOMSOURCE, RANDOMSOURCE);
	}

	public FriendsRequest(String message, int source, int destination) {
		super();
		this.message = message;
		this.source = source;
		this.destination = destination;
	}

	/**
	 * Getter for message
	 * 
	 * @return
	 */
	public String getMessage() {
		return this.message;
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
	 * Getter for destination number
	 * 
	 * @return
	 */
	public int getDestination() {
		return this.destination;
	}
}
