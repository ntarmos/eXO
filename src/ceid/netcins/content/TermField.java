package ceid.netcins.content;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

/**
 * This Content Field is also "indexed" in the overlay. The main difference of 
 * this Class with the StoredField is that the fieldData member is going to be 
 * hashed and indexed to the corresponding Catalog node.
 * 
 * @author Andreas Loupasakis
 */
public class TermField extends ContentField implements Serializable {

	private static final long serialVersionUID = 5325607261905749524L;
	private static final String FieldDataTag = "eXO::FieldData";
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

	@Override
	@SuppressWarnings("unchecked")
	public void fromJSON(Map arg0) {
		name = (String)arg0.get(FieldNameTag);
		fieldData = (String)arg0.get(FieldDataTag);
		isPublic = (Boolean)arg0.get(FieldIsPublicTag);
	}

	@Override
	public void toJSON(Output arg0) {
		arg0.add(FieldNameTag, name);
		arg0.add(FieldDataTag, fieldData);
		arg0.add(FieldIsPublicTag, (Boolean)isPublic);
	}
}
