package ceid.netcins.exo.similarity;

/**
 * This interface includes the function weight which computes the term weight
 * based on a specific implementation in another Class
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public interface Weight {

    /**
     * Returns the computed weight of the weighted object on the document.
     *
     * @return
     */
    public float getWeight();

    /**
     * Returns the corresponding object to this weight
     *
     * @return
     */
    public Object getWeightedObject();

}
