/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.similarity;

/**
 * Abstract interface which defines in general the Similarity measurement.
 * Subclasses implement search scoring functions.
 * 
 * @author andy
 * @version 1.0
 */
public abstract interface Similarity {

	/**
	 * This is the function that summarizes all the functionality of similarity
	 * matching. It computes the total score from the previous level factors
	 * multiplied with the appropriate weights.
	 * 
	 * @return The total score of the similarity between document and query.
	 */
	public abstract float getScore();

	/**
	 * This function returns the previous level factors of scoring computation
	 * 
	 * @return
	 */
	public abstract Object[] getSimilarityFactors();

}
