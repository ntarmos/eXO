package ceid.netcins.content;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jetty.util.ajax.JSON.Output;

/**
 * This ContentField type contains a set of "terms" (String values) and the 
 * corresponding "term frequencies" (tf), obtained from a source such as a file.
 * 
 * @author Andreas Loupasakis
 */
public class TokenizedField extends ContentField implements Serializable {

	private static final long serialVersionUID = 3889036995034671146L;
	private static final String TermsTag = "eXO::Terms";
	private static final String TFScoresTag = "eXO::TFScores";

	// Terms are provided in sorted order;
	String[] terms;

	// Tf are one-to-one associated with term positions in terms array
	int[] tf;

	// TODO : some error checking
	public TokenizedField(String name, TreeMap<String, Integer> tfm, boolean isPublic) {
		super(name, isPublic);

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

	public TokenizedField(String name, TreeMap<String, Integer> tfm) {
		this(name, tfm, ContentField.defaultAccessMode);
	}

	/**
	 * Constructor which must be used if we dont have term frequencies
	 * 
	 * @param name
	 * @param tfm
	 */
	public TokenizedField(String name, TreeSet<String> tfm, boolean isPublic) {
		super(name, isPublic);

		// Allocate the necessary size
		int size = tfm.size();
		terms = new String[size];
		tf = null;

		// Transfer the terms in the static array
		Iterator<String> it = tfm.iterator();

		String tmp;
		int i = 0;
		while (it.hasNext()) {
			tmp = it.next();
			terms[i] = tmp;
			i++;
		}
	}

	public TokenizedField(String name, TreeSet<String> tfm) {
		this(name, tfm, ContentField.defaultAccessMode);
	}

	/**
	 * Resets the terms array 
	 * TODO : Check the tf array if it is affected!
	 * FIXME: Not valid since we don't set the corresponding frequencies!
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

	/* (non-Javadoc)
	 * @see ceid.netcins.content.ContentField#size()
	 */
	public int size() {
		int sum = 0;
		for (int i = 0; i < terms.length; i++)
			sum += terms[i].getBytes().length;
		return super.size() + sum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Tokenized Field " + name);
		for (int i = 0; i < terms.length; i++) {
			buffer.append("\n Term: " + terms[i]);
			if (tf != null)
				buffer.append(", TF : " + tf[i]);
		}
		buffer.append("\n");
		return buffer.toString();
	}

	public String toStringWithoutTF() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Tokenized Field " + name);
		for (int i = 0; i < terms.length; i++) {
			buffer.append("\n Term: " + terms[i]);
		}
		buffer.append("\n");
		return buffer.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void fromJSON(Map arg0) {
		name = (String)arg0.get(FieldNameTag);
		terms = (String[])arg0.get(TermsTag);
		tf = (int[])arg0.get(TFScoresTag);
		isPublic = (Boolean)arg0.get(FieldIsPublicTag);
	}

	@Override
	public void toJSON(Output arg0) {
		arg0.add(FieldNameTag, name);
		arg0.add(TermsTag, terms);
		arg0.add(TFScoresTag, tf);
		arg0.add(FieldIsPublicTag, (Boolean)isPublic);
	}
}
