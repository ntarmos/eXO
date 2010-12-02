package ceid.netcins.content;

import java.io.Serializable;

/**
 * This Content Field is also "indexed" in the overlay. The main difference of 
 * this Class with the StoredField is that the fieldData member is going to be 
 * hashed and indexed to the corresponding Catalog node.
 * 
 * @author Andreas Loupasakis
 */
public class TermField extends ContentField implements Serializable {

	private static final long serialVersionUID = 5325607261905749524L;
	String fieldData;

	public TermField(String name, String fieldData, Boolean isPublic) {
		super(name, isPublic);
		this.fieldData = fieldData;
	}

	public TermField(String name, String fieldData) {
		super(name);
		this.fieldData = fieldData;
	}

	public String getFieldData() {
		return this.fieldData;
	}

	/* (non-Javadoc)
	 * @see ceid.netcins.content.ContentField#size()
	 */
	public int size() {
		return super.size() + fieldData.getBytes().length;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String("Term Field " + name + "\nTerm FieldData " + fieldData + "\n");
	}
}
