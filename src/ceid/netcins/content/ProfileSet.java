package ceid.netcins.content;

import java.util.Set;

/**
 * Names classes which can provide a set with all the Profile terms!
 * 
 * @author Andreas Loupasakis
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
