package ceid.netcins.content;

import java.io.Serializable;

/**
 * The main difference of this Class in contrast with TermField is that the
 * fieldData is not going to be hashed and indexed in the corresponding Catalog
 * node. This StoredField's fieldData is just stored in a Catalog entry. 
 * Examples of such ContentField are "filesize", "SHA-1", "modified" etc.
 * 
 * @author Andreas Loupasakis
 */
public class StoredField extends ContentField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1163562213103678794L;
	String fieldData;

	public StoredField(String name, String fieldData) {
		super(name);
		this.fieldData = fieldData;
	}

	public String getFieldData() {
		return this.fieldData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Stored Field " + name + "\nStored FieldData " + fieldData + "\n";
	}
}
