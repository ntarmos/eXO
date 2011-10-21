package ceid.netcins.exo.similarity;

/**
 * This class represents a weight computed with the tf/idf.
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
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
