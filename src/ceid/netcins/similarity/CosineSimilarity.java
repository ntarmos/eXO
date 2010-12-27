package ceid.netcins.similarity;

/**
 * This class implements the cosine similarity equation.
 * 
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
public class CosineSimilarity implements Similarity {

	// The vectors we want to compare.
	private TermWeight docWeights[], queryWeights[];

	/*
	 * Constructor wrapper for cases we haven't yet docWeights computed .
	 */
	public CosineSimilarity(TermWeight queryWeights[]) {
		this(null, queryWeights);
	}
	
	/**
	 * General Constructor 
	 * 
	 * @param docWeights Array of document weight values
	 * @param queryWeights Array of query weight values
	 */
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

		if(queryWeights!=null && docWeights!=null){
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
		}
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
