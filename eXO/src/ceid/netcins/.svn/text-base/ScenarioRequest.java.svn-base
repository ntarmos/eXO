/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins;

import java.util.Vector;

/**
 * This request initiates a Scenario object in SimDriver that handles the counters
 * which count the submitted and finished requests in the simulation.
 *
 * @author andy
 */
public class ScenarioRequest extends Request{

    // The number of submitted index requests
    public Vector<Request> index_submitted;

    // The number of submitted search requests
    public Vector<Request> search_submitted;

    // The number of submitted tag requests
    public Vector<Request> tag_submitted;

    // The number of submitted randomqueries requests
    public Vector<Request> randomQueries_submitted;

    // Only for SAX processing!!!!!!!
    public ScenarioRequest(){
        this.index_submitted = new Vector<Request>();
        this.search_submitted = new Vector<Request>();
        this.tag_submitted = new Vector<Request>();
        this.randomQueries_submitted= new Vector<Request>();
    }

    /**
     * Constructor that initializes the varibles and counters.
     *
     * @param index_submitted
     * @param search_submitted
     * @param tag_submitted
     * @param randomQueries_submitted
     */
    public ScenarioRequest(Vector<Request> index_submitted, Vector<Request> search_submitted, 
            Vector<Request> tag_submitted, Vector<Request> randomQueries_submitted){

        this.index_submitted = index_submitted;
        this.search_submitted = search_submitted;
        this.tag_submitted = tag_submitted;
        this.randomQueries_submitted = randomQueries_submitted;
    }
}
