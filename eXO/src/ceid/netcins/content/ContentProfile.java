/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This Class holds the Content Profile of a content object. Such an object is
 * stored in a Catalog Entry. When an indexing process is in progress this
 * object is "serialized" and send to the corresponding Catalog nodes.
 * 
 * @author andy
 */
public class ContentProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3971044346421201440L;

	List<ContentField> fields = new ArrayList<ContentField>();

	/**
	 * Adds a ContentField object (any of three types) in the List fields
	 * 
	 * @param field
	 */
	public final void add(ContentField field) {
		fields.add(field);
	}

	/**
	 * 
	 * @return a list with all ContentFields
	 */
	public List<ContentField> getFields() {
		return fields;
	}

	/**
	 * 
	 * @return A full representation of the ContentProfile data
	 */
	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		Iterator<ContentField> it = this.fields.iterator();

		while (it.hasNext()) {

			Object obj = it.next();

			if (obj instanceof TokenizedField) {

				TokenizedField tokf = (TokenizedField) obj;
				buffer.append("\nTokenized Field " + tokf.getFieldName());
				String[] terms = tokf.getTerms();
				int[] tfs = tokf.getTF();
				if (tfs != null) {
					for (int i = 0; i < tokf.getTerms().length; i++) {
						buffer.append("\nTerm : " + terms[i] + ", TF : "
								+ tfs[i]);
					}
				} else {
					for (int i = 0; i < tokf.getTerms().length; i++) {
						buffer.append("\nTerm : " + terms[i]);
					}
				}

			} else if (obj instanceof TermField) {

				TermField termf = (TermField) obj;
				buffer.append("\nTerm Field " + termf.getFieldName());
				buffer.append("\nTerm FieldData " + termf.getFieldData());

			} else if (obj instanceof StoredField) {

				StoredField storf = (StoredField) obj;
				buffer.append("\nStored Field " + storf.getFieldName());
				buffer.append("\nStored FieldData " + storf.getFieldData());

			}
		}
		return buffer.toString();
	}

	/**
	 * This is a convenient method to present the profile terms without the
	 * corresponding term frequencies.
	 * 
	 * @return A full representation of the ContentProfile data
	 */
	public String toStringWithoutTF() {

		StringBuffer buffer = new StringBuffer();
		Iterator<ContentField> it = this.fields.iterator();

		while (it.hasNext()) {

			Object obj = it.next();

			if (obj instanceof TokenizedField) {

				TokenizedField tokf = (TokenizedField) obj;
				buffer.append("\nTokenized Field " + tokf.getFieldName());
				String[] terms = tokf.getTerms();
				for (int i = 0; i < tokf.getTerms().length; i++) {
					buffer.append("\nTerm : " + terms[i]);
				}

			} else if (obj instanceof TermField) {

				TermField termf = (TermField) obj;
				buffer.append("\nTerm Field " + termf.getFieldName());
				buffer.append("\nTerm FieldData " + termf.getFieldData());

			} else if (obj instanceof StoredField) {

				StoredField storf = (StoredField) obj;
				buffer.append("\nStored Field " + storf.getFieldName());
				buffer.append("\nStored FieldData " + storf.getFieldData());

			}
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @return A sum of the ContentProfile data in bytes
	 */
	public double computeTotalBytes() {

		double counter = 0;
		Iterator<ContentField> it = this.fields.iterator();

		while (it.hasNext()) {

			Object obj = it.next();

			if (obj instanceof TokenizedField) {

				TokenizedField tokf = (TokenizedField) obj;
				String[] terms = tokf.getTerms();
				for (int i = 0; i < terms.length; i++) {
					counter += terms[i].getBytes().length;
				}

			} else if (obj instanceof TermField) {

				TermField termf = (TermField) obj;
				counter += termf.getFieldData().getBytes().length;

			}
		}
		return counter;
	}

	/**
	 * 
	 * @return A random term of the content profile
	 */
	public String randomTerm() {
		Random random = new Random();
		int i = 0;
		while (i++ < 1000) {
			Object o = this.fields.get(random.nextInt(fields.size()));
			if (o instanceof TokenizedField) {
				TokenizedField tokf = (TokenizedField) o;
				return tokf.terms[random.nextInt(tokf.terms.length)];
			} else if (o instanceof TermField) {
				TermField termf = (TermField) o;
				return termf.getFieldData();
			}
		}
		return "";
	}

}
