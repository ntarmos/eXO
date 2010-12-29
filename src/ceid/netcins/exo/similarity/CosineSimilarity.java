package ceid.netcins.exo.similarity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	private Set<TermWeight> docWeights, queryWeights;

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
		this.docWeights = (docWeights != null) ? Collections.synchronizedSet(new HashSet<TermWeight>(Arrays.asList(docWeights))) : null;
		this.queryWeights = (queryWeights != null) ? Collections.synchronizedSet(new HashSet<TermWeight>(Arrays.asList(queryWeights))) : null;
	}

	/**
	 * This is the scoring function!!!!
	 * 
	 * @return
	 */
	public float getScore() {
		float score = 0;

		if(queryWeights!=null && docWeights!=null){
			for (TermWeight twq : queryWeights) {
				for (TermWeight twd : docWeights) {
					if (twq.getWeightedObject().equals(
							twd.getWeightedObject())) {
						score += twd.getWeight()
								* twq.getWeight();
						break;
					}
				}
			}
			score /= norm();
		}
		return score;
	}

	public float norm() {
		if (queryWeights.iterator().next() instanceof BinaryWeight) {
			return queryWeights.size();
		}
		// sum of squares
		return 1;
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
		this.docWeights = (docWeights != null) ? Collections.synchronizedSet(new HashSet<TermWeight>(Arrays.asList(docWeights))) : null;
	}
}
