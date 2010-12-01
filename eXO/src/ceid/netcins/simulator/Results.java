

package ceid.netcins.simulator;

/**
 * This class will hold the total and average results.
 * 
 * @author Andreas Loupasakis
 */
public class Results {

	// Total number of queries that have been sent to DispatcherThread!
	public int resolved;

	// Performance Results

	// X axis

	// Number of keywords per query
	public static final int DEFAULTKEYWORDSNUM = 2;
	int keywords;

	// Number of results per query
	public static final int DEFAULTKSNUM = -1;
	int k;

	// Number of nodes in the network N
	public static final int DEFAULTNNUM = 1000;
	int nodes;

	// Y axis

	// Parameters for the computation of Average Message Number per Query
	public double totalQueryNumber;
	public double totalMessageNumber;
	public double averageMessageNumber;

	// Parameters for the bandwidth computation
	public double totalByteNumber;

	// Counted queries. This is used to compute query overhead!
	public double totalIncludedQueries;

	// The byte nymber per query results
	public double averageByteNumber;

	public Results() {
		keywords = DEFAULTKEYWORDSNUM;
		k = DEFAULTKSNUM;
		nodes = DEFAULTNNUM;
		resolved = 0;
		totalIncludedQueries = 0;
	}

	public double computeAVGMessages() {
		return (totalMessageNumber / totalQueryNumber);
	}

	public double computeQueryOverhead() {
		return (totalByteNumber / totalIncludedQueries);
	}

}
