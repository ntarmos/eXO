package ceid.netcins.similarity;

/**
 * This interface includes the function weight which computes the term weight
 * based on a specific implementation in another Class
 * 
 * @author Andreas Loupasakis
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
