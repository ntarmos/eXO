package ceid.netcins.similarity;

/**
 * Represents a simple binary weight! 1 = term exists, 0 = term does not exist
 * in document
 * 
 * @author Andreas Loupasakis
 * @version 1.0
 */
public class BinaryWeight extends TermWeight {

	public BinaryWeight(String term) {
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
