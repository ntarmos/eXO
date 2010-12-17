package ceid.netcins.content;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

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
	private static final String FieldDataTag = "eXO::FieldData";
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
		return new String("Stored Field " + name + "\nStored FieldData " + fieldData + "\n");
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
