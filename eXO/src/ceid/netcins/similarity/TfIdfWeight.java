/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.similarity;

/**
 * This class represents a weight computed with the tf/idf.
 * 
 * @author andy
 */
public class TfIdfWeight extends TermWeight {

	public TfIdfWeight(String term) {
		super(term);
	}

	/**
	 * Always returns 1 (as the rem is presented) when this method is called
	 * with a term in our set!
	 * 
	 * @return
	 */
	@Override
	public float getWeight() {
		return 1;
	}
}
