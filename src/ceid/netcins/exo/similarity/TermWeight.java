package ceid.netcins.exo.similarity;

/**
 * Associates a weight to a specific term.
 * A term may define a specific "Dimension" in a Vector Space Model!
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
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

	@Override
	public boolean equals(Object o) {
		return (o instanceof TermWeight &&
				term.equals(((TermWeight)o).term));
	}

	@Override
	public int hashCode() {
	    return (term != null) ? term.hashCode() : 0;
	}
}
