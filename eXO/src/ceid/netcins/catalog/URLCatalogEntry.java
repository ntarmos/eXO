

package ceid.netcins.catalog;

import java.io.Serializable;
import java.net.URL;

import rice.p2p.commonapi.Id;
import ceid.netcins.content.ContentProfile;

/**
 * This class is used as an entry in the catalog
 * 
 * @author Andreas Loupasakis
 */
@SuppressWarnings("unchecked")
public class URLCatalogEntry extends ContentCatalogEntry implements
		Serializable, Comparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1571553467344918199L;
	// The url for this entry
	private URL url;

	/**
	 * Constructor
	 * 
	 * @param uid
	 *            The user unique identifier
	 * @param tags
	 *            The set of tags annotating url
	 * @param usrp
	 *            The user's profile, used for similarity based scoring
	 * @param url
	 *            The address of the annotating resource
	 */
	public URLCatalogEntry(Id uid, ContentProfile tags, ContentProfile usrp,
			URL url) {
		super(uid, tags, usrp);
		this.url = url;
	}

	/**
	 * Getter for the url address
	 * 
	 * @return
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Used to compare two entries. Two entries are the same if: just they have
	 * the same UID This is correct as we have a catalog per URL (TID).
	 * 
	 * @param o
	 *            DESCRIBE THE PARAMETER
	 * @return DESCRIBE THE RETURN VALUE
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof URLCatalogEntry)) {
			return false;
		}
		URLCatalogEntry ue = (URLCatalogEntry) o;

		return (ue.getUID().equals(getUID()));
	}

	/**
	 * A representation of the entry sewed to the specific class
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Catalog Entry : [UID] = ");
		buf.append(this.getUID().toString());
		buf.append(", [URL] = ");
		if (this.url == null)
			buf.append("null");
		else
			buf.append(this.url);
		buf.append(", [contentProfile] = ");
		if (this.getContentProfile() == null)
			buf.append("null");
		else
			buf.append(this.getContentProfile().toString());
		buf.append("[userProfile] = ");
		if (this.getUserProfile() == null)
			buf.append("null");
		else
			buf.append(this.getUserProfile().toString());
		return buf.toString();
	}

	/**
	 * Same with the comparison on UserCatalogEntry/CatalogEntry
	 * 
	 * @param arg0
	 * @return
	 */
	@Override
	public int compareTo(Object arg0) {
		if (!(arg0 instanceof URLCatalogEntry)) {
			throw new ClassCastException();
		}
		URLCatalogEntry ue = (URLCatalogEntry) arg0;

		if (this.equals(arg0))
			return 0;
		else {
			return this.getUID().compareTo(ue.getUID());
		}
	}

}
