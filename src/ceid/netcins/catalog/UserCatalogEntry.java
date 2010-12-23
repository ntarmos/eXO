package ceid.netcins.catalog;

import java.io.Serializable;

import rice.p2p.commonapi.Id;
import ceid.netcins.content.ContentProfile;

/**
 * An extension of the CatalogEntry which is used to offer user indexing
 * functionality
 * 
 * @author Andreas Loupasakis
 */
@SuppressWarnings("rawtypes")
public class UserCatalogEntry extends CatalogEntry implements Serializable,
		Comparable {

	private static final long serialVersionUID = 7850151060272447739L;
	// The profile of the user we want to have indexed.
	private ContentProfile userProfile;

	public UserCatalogEntry(Id uid, ContentProfile usrp) {
		super(uid);
		this.userProfile = usrp;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof UserCatalogEntry &&
				((CatalogEntry)this).equals((CatalogEntry)o) &&
				userProfile.equals(((UserCatalogEntry)o).userProfile));
	}

	@Override
	public int hashCode() {
		return super.hashCode() + userProfile.hashCode();
	}

	/**
	 * Getter for the userProfile
	 * 
	 * @return userProfile
	 */
	public ContentProfile getUserProfile() {
		return this.userProfile;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("UserCatalog Entry : [UID] = ");
		buf.append(getUID().toString());
		buf.append("[userProfile] = ");
		if (userProfile == null)
			buf.append("null");
		else
			buf.append(userProfile.toString());
		return buf.toString();
	}

	/**
	 * 
	 * @return A sum of the UserCatalogEntry data in bytes
	 */
	public double computeTotalBytes() {
		double counter = 0;
		counter += this.getUID().getByteArrayLength();
		counter += this.userProfile.computeTotalBytes();

		return counter;
	}
}
