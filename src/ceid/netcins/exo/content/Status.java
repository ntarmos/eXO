package ceid.netcins.exo.content;

import java.io.Serializable;

/**
 *
 */

public class Status extends ContentField implements Serializable{
    public final  static String STATUS_ID = "::status";

	/**
	 *
	 */
	private static final long serialVersionUID = 1163562213103678794L;
	String statusText;

	public Status(String statusText, boolean isPublic) {
		super(STATUS_ID, isPublic);
		this.statusText= statusText;
	}
	public Status(String fieldData) {
		super("STATUS_ID");
		this.statusText = fieldData;
	}

	public String statusText() {
		return this.statusText;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		  return statusText;
        //return "SF{ \"" + name + "\" : { \"" + fieldData + "\" , " + (isPublic ? "public" : "private") + " }}";
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof StoredField &&
				super.equals((ContentField)o) &&
				statusText.equals(((StoredField)o).fieldData));
	}

	@Override
	public int hashCode() {
		return super.hashCode() + statusText.hashCode();
	}
}
