package ceid.netcins.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * This Class holds the Content Profile of a content object. Such an object is
 * stored in a Catalog Entry. When an indexing process is in progress this
 * object is "serialized" and send to the corresponding Catalog nodes.
 * 
 * @author Andreas Loupasakis
 */
public class ContentProfile implements Serializable {

	private static final long serialVersionUID = -3971044346421201440L;

	List<ContentField> fields;

	/**
	 * Default constructor
	 */
	public ContentProfile() {
		this.fields = new ArrayList<ContentField>();
	}

	/**
	 * Copy constructor
	 *
	 * @param cp the ContentProfile to copy from
	 */
	public ContentProfile(ContentProfile cp) {
		this.fields = new ArrayList<ContentField>(cp.fields);
	}

	/**
	 * Copy constructor, initializing its fields by copying all of the elements of c
	 * @param c a {@link Collection} of {@link ContentField}s to make up the new {@link ContentProfile} 
	 */
	public ContentProfile(Collection<ContentField> c) {
		this();
		fields.addAll(c);
	}

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
	 * @return a list with all public ContentFields
	 */
	public List<ContentField> getPublicFields() {
		List<ContentField> ret = new ArrayList<ContentField>();
		for (ContentField f : fields)
			if (f.isPublic())
				ret.add(f);
		return ret;
	}

	/**
	 *
	 * @return a list with all (public and private) ContentFields
	 */
	public List<ContentField> getAllFields() {
		// TODO: which is better to return: a live reference or a copy?
		return new ArrayList<ContentField>(fields);
	}

	/**
	 * @return a new ContentProfile with just the public fields
	 */
	public ContentProfile getPublicPart() {
		return new ContentProfile(getPublicFields());
	}

	/**
	 * 
	 * @return A full representation of the ContentProfile data
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (ContentField obj : fields) {
			if (obj instanceof TokenizedField)
				buffer.append(((TokenizedField)obj).toString());
			else if (obj instanceof TermField)
				buffer.append(((TermField)obj).toString());
			else if (obj instanceof StoredField)
				buffer.append(((StoredField)obj).toString());
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
		for (ContentField obj : fields) {
			if (obj instanceof TokenizedField)
				buffer.append(((TokenizedField)obj).toStringWithoutTF());
			else if (obj instanceof TermField)
				buffer.append(((TermField)obj).toString());
			else if (obj instanceof StoredField)
				buffer.append(((StoredField)obj).toString());
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @return A sum of the ContentProfile data in bytes
	 */
	public double computeTotalBytes() {
		double counter = 0;
		for (ContentField obj : fields) {
			// Don't count bytes of private data
			if (obj.isPrivate())
				continue;

			if (obj instanceof TokenizedField)
				counter += ((TokenizedField)obj).size();
			else if (obj instanceof TermField)
				counter += ((TermField)obj).size();
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

	public boolean equals(ContentProfile cp) {
		HashSet<ContentField> our = new HashSet<ContentField>(fields);
		HashSet<ContentField> theirs = new HashSet<ContentField>(cp.fields);
		return (our.equals(theirs));
	}

	public boolean equalsPublic(ContentProfile cp) {
		return this.getPublicPart().equals(cp.getPublicPart());
	}
}
