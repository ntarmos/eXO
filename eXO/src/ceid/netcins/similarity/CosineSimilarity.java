/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.similarity;

/**
 * This class implements the Vector Space Model.
 * 
 * 
 * @author andy
 * @version 1.0
 */
public class CosineSimilarity implements Similarity {

	// The vectors we want to compare.
	private TermWeight docWeights[], queryWeights[];

	public CosineSimilarity(TermWeight docWeights[], TermWeight queryWeights[]) {
		this.docWeights = docWeights;
		this.queryWeights = queryWeights;
	}

	/**
	 * This is the scoring function!!!!
	 * 
	 * @return
	 */
	public float getScore() {
		int i, j;
		float score = 0;

		for (i = 0; i < queryWeights.length; i++) {
			for (j = 0; j < docWeights.length; j++) {
				// TODO : check that indeed the |Q| and |E| are SETS!
				if (this.queryWeights[i].getWeightedObject().equals(
						docWeights[j].getWeightedObject())) {
					score += this.docWeights[j].getWeight()
							* this.queryWeights[i].getWeight();
					// DEBUGGING System.out.println("Score "+i+" : "+score);
					break; // the innermost loop only
				}
			}
		}
		score /= norm();
		// DEBUGGING System.out.println("Final : "+score);
		return score;
	}

	public float norm() {
		if (queryWeights[0] instanceof BinaryWeight) {
			return queryWeights.length;
		} else { // sum of squares
			return 1;
		}
	}

	public Object[] getSimilarityFactors() {
		return new Object[] { this.docWeights, this.queryWeights };
	}

	/**
	 * In order to reuse the same instance of CosineSimilarity!!
	 * 
	 * @param docWeights
	 */
	public void setDocWeights(TermWeight[] docWeights) {
		this.docWeights = docWeights;
	}

}
