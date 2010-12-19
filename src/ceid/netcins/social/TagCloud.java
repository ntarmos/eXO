package ceid.netcins.social;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jetty.util.ajax.JSON.Convertible;
import org.eclipse.jetty.util.ajax.JSON.Output;

/**
 * This is a set of social terms/tags with a corresponding weight = TF.
 * The tag cloud consists of terms contributed by users other than the owner.
 * 
 * @author Andreas Loupasakis
 */
public class TagCloud {

	// Term frequency map
	private TreeMap<String, Integer> tfm;

	/**
	 * Constructor
	 * 
	 * @param tfm
	 */
	public TagCloud(TreeMap<String, Integer> tfm) {
		this.tfm = tfm;
	}

	/**
	 * Constructor
	 * 
	 */
	public TagCloud() {
		this.tfm = new TreeMap<String, Integer>();
	}

	/**
	 * Getter for tfm
	 * 
	 * @return
	 */
	public TreeMap<String, Integer> getTagTFMap() {
		return tfm;
	}

	/**
	 * Insert a new tag or add +1 to TF
	 * 
	 * @param tag
	 */
	public void addTagTFMap(String tag) {

		// Increase the TF
		if (tfm.containsKey(tag)) {
			int num = tfm.get(tag);
			tfm.put(tag, (num + 1));
		} else {
			tfm.put(tag, new Integer(1));
		}
	}

	/**
	 * Remove the named tag from the Map
	 * 
	 * @param tag The tag to be removed.
	 */
	public void removeTag(String tag){
		if (tfm.containsKey(tag)) {
			tfm.remove(tag);
		}
	}
	
	/**
	 * String representation of TagCloud
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("TagCloud : [");
		Iterator<String> it = tfm.keySet().iterator();
		String tag;
		while (it.hasNext()) {
			tag = it.next();
			buf.append("{ tag : " + tag + ", freq : " + tfm.get(tag) + " }"
					+ (it.hasNext() ? "," : ""));
		}
		buf.append("]");
		return buf.toString();
	}
}
