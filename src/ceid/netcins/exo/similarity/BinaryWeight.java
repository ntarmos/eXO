package ceid.netcins.exo.similarity;

/**
 * Represents a simple binary weight! 1 = term exists, 0 = term does not exist
 * in document
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
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
