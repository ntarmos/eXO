package ceid.netcins.similarity;

/**
 * Associates a weight to a specific term.
 * A term may define a specific "Dimension" in a Vector Space Model!
 * 
 * @author Andreas Loupasakis
 */
public abstract class TermWeight implements Weight {

	// The term of the corresponding weight
	private String term;

	public TermWeight(String term) {
		this.term = term;
	}

	public abstract float getWeight();

	public String getWeightedObject() {
		return term;
	}

}
