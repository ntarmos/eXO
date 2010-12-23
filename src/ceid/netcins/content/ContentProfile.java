package ceid.netcins.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * This Class holds the Content Profile of a content object. Such an object is
 * stored in a Catalog Entry. When an indexing process is in progress this
 * object is "serialized" and send to the corresponding Catalog nodes.
 * 
 * @author Andreas Loupasakis
 */
public class ContentProfile implements Serializable, ProfileSet {

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
		if (cp != null)
			this.fields = new ArrayList<ContentField>(cp.fields);
		else
			this.fields = null;
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
		if (fields != null)
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
		if (fields != null)
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
		if (fields != null)
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
		if (fields != null)
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
				return tokf.randomTerm();
			} else if (o instanceof TermField) {
				TermField termf = (TermField) o;
				return termf.getFieldData();
			}
		}
		return "";
	}

	@Override
	public int hashCode() {
		return fields.hashCode();
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ContentProfile))
			return false;
		return equals((ContentProfile)o);
	}
	
	public boolean equals(ContentProfile cp) {
		HashSet<ContentField> our = new HashSet<ContentField>(fields);
		HashSet<ContentField> theirs = new HashSet<ContentField>(cp.fields);
		return (our.equals(theirs));
	}

	public boolean equalsPublic(ContentProfile cp) {
		return this.getPublicPart().equals(cp.getPublicPart());
	}

	@Override
	public Set<String> getTermSet() {
		List<ContentField> contentFields = this.getAllFields();
		// FIXME: Any objection on TreeSet? Maybe we can use HashSet!? 
		Set<String> profileTerms = new TreeSet<String>(); 
		// Put all the terms in the Set object to "discard" duplicates
		for(ContentField cf : contentFields) {
			if (cf instanceof TokenizedField) {
				TokenizedField tkf = (TokenizedField) cf;
				String[] fieldterms = tkf.getTerms();
				if (fieldterms != null)
					for (String t : fieldterms) {
						profileTerms.add(t);
					}
			} else if (cf instanceof TermField) {
				profileTerms.add(((TermField) cf).getFieldData());
			}
		}
		return profileTerms;
	}

	@Override
	public Set<String> getTermSet(Set<String> reusableContainer) {
		List<ContentField> contentFields = this.getAllFields();
		// Ensure container is empty 
		reusableContainer.clear();
		// Put all the terms in the Set object to "discard" duplicates
		for(ContentField cf : contentFields) {
			if (cf instanceof TokenizedField) {
				TokenizedField tkf = (TokenizedField) cf;
				String[] fieldterms = tkf.getTerms();
				if (fieldterms != null)
					for (String t : fieldterms) {
						reusableContainer.add(t);
					}
			} else if (cf instanceof TermField) {
				reusableContainer.add(((TermField) cf).getFieldData());
			}
		}
		return reusableContainer;
	}
}
