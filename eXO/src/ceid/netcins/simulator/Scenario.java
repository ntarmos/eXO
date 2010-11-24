/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.simulator;

import java.util.Vector;

import ceid.netcins.Request;

/**
 * This class holds counters to mark the end and start of a set of request. The
 * permitted requests are listed in scenarios.dtd
 * 
 * @author andy
 */
public class Scenario {

	// The number of submitted index requests
	public Vector<Request> index_submitted;

	// The number of submitted search requests
	public Vector<Request> search_submitted;

	// The number of submitted tag requests
	public Vector<Request> tag_submitted;

	// The number of submitted randomqueries requests
	public Vector<Request> randomQueries_submitted;

	// The number of finished index requests
	public int index_finished;

	// The number of finished search requests
	public int search_finished;

	// The number of finished tag requests
	public int tag_finished;

	// The number of finished randomqueries requests
	public int random_finished;

	/**
	 * Constructor that initializes the varibles and counters.
	 */
	public Scenario(Vector<Request> index_submitted,
			Vector<Request> search_submitted, Vector<Request> tag_submitted,
			Vector<Request> randomQueries_submitted) {
		this.index_submitted = index_submitted;
		this.search_submitted = search_submitted;
		this.tag_submitted = tag_submitted;
		this.randomQueries_submitted = randomQueries_submitted;

		this.index_finished = 0;
		this.search_finished = 0;
		this.tag_finished = 0;
		this.random_finished = 0;
	}

	/**
	 * Wrapper to signal the end of the current Scenario.
	 * 
	 * @return
	 */
	public boolean isFinished() {
		if ((this.index_submitted == null || this.index_submitted.isEmpty())
				&& (this.search_submitted == null || this.search_submitted
						.isEmpty())
				&& (this.tag_submitted == null || this.tag_submitted.isEmpty())
				&& (this.randomQueries_submitted == null || this.randomQueries_submitted
						.isEmpty())) {
			return true;
		}
		return false;
	}
}
