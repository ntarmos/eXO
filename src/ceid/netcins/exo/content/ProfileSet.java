package ceid.netcins.exo.content;

import java.util.Set;

/**
 * Names classes which can provide a set with all the Profile terms!
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 *
 */
public interface ProfileSet {

	/**
	 * An interface method to wrap all the terms in a Set object.
	 * 
	 * @return A set of strings. Each string corresponds to a term. 
	 */
	Set<String> getTermSet();
	
	/**
	 * This declaration may be used in cases we want to reuse a pre-created 
	 * Set object to avoid memory bloating situations.
	 * 
	 * @param A reusable container Set object.
	 * @return A set of strings. Each string corresponds to a term. 
	 */
	Set<String> getTermSet(Set<String> reusableContainer);
}
