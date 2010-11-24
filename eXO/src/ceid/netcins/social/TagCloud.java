/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.social;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * This is a set of social terms/tags with a corresponding weight = TF
 * 
 * @author andy
 */
public class TagCloud {

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
