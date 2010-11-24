/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins;

/**
 * This is the request to the dispatcher thread for statistic previews such as
 * shared content observation, pending user requests etc.
 * 
 * @author andy
 */
public class StatsRequest extends Request {

	// TYPES of StatsRequest
	public final static int PENDING = 0;
	public final static int SHARED = 1;
	public final static int PROFILES = 2;
	public final static int BOOKMARKS = 3;
	public final static int GLOBALSTATS = 4;
	public final static int CLEANGLOBALSTATS = 5;

	private int type;

	// The source node number (Simulator only)
	private int source;
	public static final int RANDOMSOURCE = -1;

	public StatsRequest(int type) {
		this(type, RANDOMSOURCE);
	}

	public StatsRequest(int type, int source) {
		super();
		this.type = type;
		this.source = source;
	}

	/**
	 * Getter for type
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Getter for source number
	 * 
	 * @return
	 */
	public int getSource() {
		return this.source;
	}
}
