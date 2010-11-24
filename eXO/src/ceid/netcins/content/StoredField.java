/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.content;

import java.io.Serializable;

/**
 * The main difference of this Class in contrast with TermField is that this
 * fieldData is not going to be hashed and indexed in a corresponding Catalog
 * node. This fieldData is just stored in a Catalog entry. Examples of such
 * ContentField are "filesize", "SHA-1", "modified" etc.
 * 
 * @author andy
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
}
