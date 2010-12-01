

package ceid.netcins.content;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This ContentField type contains a set of "terms" (String values) obtained
 * from the analysis of some file and the corresponding "term frequencies" (tf)
 * 
 * @author Andreas Loupasakis
 */
public class TokenizedField extends ContentField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3889036995034671146L;

	// Terms are provided in sorted order;
	String[] terms;

	// Tf are one-to-one associated with term positions in terms array
	int[] tf;

	// TODO : some error checking
	public TokenizedField(String name, TreeMap<String, Integer> tfm) {
		super(name);

		// Allocate the necessary size
		int size = tfm.size();
		terms = new String[size];
		tf = new int[size];

		// Transfer the terms and tfs in the static arrays
		Iterator<String> it = tfm.keySet().iterator();

		String tmp;
		int i = 0;
		while (it.hasNext()) {
			tmp = it.next();
			terms[i] = tmp;
			tf[i] = tfm.get(tmp).intValue();
			i++;
		}
	}

	/**
	 * Constructor which must be used if we dont have term frequencies
	 * 
	 * @param name
	 * @param tfm
	 */
	public TokenizedField(String name, TreeSet<String> tfm) {
		super(name);

		// Allocate the necessary size
		int size = tfm.size();
		terms = new String[size];
		tf = null;

		// Transfer the terms and tfs in the static arrays
		Iterator<String> it = tfm.iterator();

		String tmp;
		int i = 0;
		while (it.hasNext()) {
			tmp = it.next();
			terms[i] = tmp;
			i++;
		}
	}

	/**
	 * Resets the terms array TODO : Check the tf array if it is affected!
	 * 
	 * @param terms
	 */
	public void setTerms(String[] terms) {
		this.terms = terms;
	}

	public String[] getTerms() {
		return this.terms;
	}

	public int[] getTF() {
		return this.tf;
	}
}
