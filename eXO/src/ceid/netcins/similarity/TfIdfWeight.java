package ceid.netcins.similarity;

/**
 * This class represents a weight computed with the tf/idf.
 * 
 * @author Andreas Loupasakis
 */
public class TfIdfWeight extends TermWeight {

	public TfIdfWeight(String term) {
		super(term);
	}

	/**
	 * Always returns 1 (if the term is presented) when this method is called
	 * with a term in our set!
	 * 
	 * @return
	 */
	@Override
	public float getWeight() {
		return 1;
	}
}
