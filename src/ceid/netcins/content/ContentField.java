package ceid.netcins.content;

import java.io.Serializable;

/**
 * Provide categorization of data just like in Apache Lucene's Field Class.
 * ContentFields consists of :
 * 	 "name" : A category name such as "contents", "filename", "modified" etc. 
 *   "fieldData" : 
 *   	 1) a sorted set of "terms" for TOKENIZED fields with an array of "tf"
 *   	    values.
 *    	 2) a "String" object for STORED and UNTOKENIZED fields
 * 
 * @author Andreas Loupasakis
 */
public abstract class ContentField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3881344031651483680L;
	public static final boolean defaultAccessMode = true;

	// No setters for the fields below; remove and readd to update
	String name;
	Boolean isPublic;

	public ContentField(String name, boolean isPublic) {
		this.name = name;
		this.isPublic = isPublic;
	}

	public ContentField(String name) {
		this(name, defaultAccessMode);
	}

	public String getFieldName() {
		return name;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isPrivate() {
		return !isPublic;
	}

	public int size() {
		// Don't count isPublic in the object's size as it is not sent out to the network
		return name.getBytes().length;
	}
}
