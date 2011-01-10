package ceid.netcins.exo.catalog;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import rice.p2p.commonapi.Id;
import ceid.netcins.exo.content.ContentField;
import ceid.netcins.exo.content.ContentProfile;
import ceid.netcins.exo.content.StoredField;

/**
 * This class extends the user catalog entries to contain content information
 * bundled with the user indexed content.
 * 
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
@SuppressWarnings("rawtypes")
public class ContentCatalogEntry extends UserCatalogEntry implements
		Serializable, Comparable {

	private static final long serialVersionUID = 2355147651372821335L;
	// The profile for a specific shared content object
	private ContentProfile contentProfile;

	public ContentCatalogEntry(Id uid, ContentProfile cp, ContentProfile usrp) {
		super(uid, usrp);
		this.contentProfile = cp;
	}

	@Override
	public int hashCode() {
		return super.hashCode() + contentProfile.hashCode();
	}
	
	/**
	 * Used to compare two entries. Two entries are the same if: they have the
	 * same UID and the same SHA-1 checksum
	 * 
	 * @param o
	 *            DESCRIBE THE PARAMETER
	 * @return DESCRIBE THE RETURN VALUE
	 */
	@Override
	public boolean equals(Object o) {
		return (o instanceof ContentCatalogEntry &&
				super.equals((UserCatalogEntry)o) &&
				contentProfile.equals(((ContentCatalogEntry)o).contentProfile));
	}

	/**
	 * 
	 * @return the ContentField "SHA-1", null if there is no such a field.
	 */
	public String getCheckSum() {

		Set<ContentField> list = contentProfile.getAllFields();
		Iterator<ContentField> it = list.iterator();
		ContentField cf;
		while (it.hasNext()) {
			cf = it.next();
			if (cf.getFieldName().equals("SHA-1") && cf instanceof StoredField) {
				return ((StoredField) cf).getFieldData();
			}
		}
		return null;
	}

	/**
	 * Getter for the contentProfile
	 * 
	 * @return contentProfile
	 */
	public ContentProfile getContentProfile() {
		return this.contentProfile;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Catalog Entry : [UID] = ");
		buf.append(this.getUID().toString());
		buf.append(", [contentProfile] = ");
		if (contentProfile == null)
			buf.append("null");
		else
			buf.append(this.contentProfile.toString());
		buf.append("[userProfile] = ");
		if (this.getUserProfile() == null)
			buf.append("null");
		else
			buf.append(this.getUserProfile().toString());
		return buf.toString();
	}

	@Override
	public int compareTo(Object arg0) {
		if (!(arg0 instanceof ContentCatalogEntry)) {
			throw new ClassCastException();
		}
		ContentCatalogEntry ce = (ContentCatalogEntry) arg0;

		if (this.equals(arg0))
			return 0;
		else {
			if (this.getUID().compareTo(ce.getUID()) == 0) {
				if (this.getCheckSum() == null)
					return (ce.getCheckSum() == null ? 0 : -1);
				else
					return (ce.getCheckSum() == null ? 1 : this.getCheckSum()
							.compareTo(ce.getCheckSum()));
			} else {
				return this.getUID().compareTo(ce.getUID());
			}
		}
	}

	/**
	 * 
	 * @return A sum of the ContentCatalogEntry data in bytes
	 */
	@Override
	public double computeTotalBytes() {
		double counter = 0;
		counter += this.getUID().getByteArrayLength();
		if (this.getUserProfile() != null)
			counter += this.getUserProfile().computeTotalBytes();
		counter += this.contentProfile.computeTotalBytes();

		return counter;
	}

	@Override
	public CatalogEntry add(CatalogEntry additions) {
		if (additions != null && (
				!(additions instanceof ContentCatalogEntry) ||
				!(getUID().equals(additions.getUID())))
		) {
			return null;
		}
		if (additions == null)
			return this;

		ContentCatalogEntry addCE = ((ContentCatalogEntry)additions);
		Set<ContentField> add = null;
		super.add(new UserCatalogEntry(getUID(), addCE.getUserProfile()));
		if (addCE.contentProfile == null ||
				(add = addCE.contentProfile.getAllFields()).size() == 0)
			return this;

		for (ContentField cf : add)
			contentProfile.add(cf);
		return this;
	}

	@Override
	public CatalogEntry subtract(CatalogEntry deletions) {
		if (deletions != null && (
				!(deletions instanceof ContentCatalogEntry) ||
				!(getUID().equals(deletions.getUID())))
		) {
			return null;
		}
		if (deletions == null)
			return this;

		ContentCatalogEntry delCE = ((ContentCatalogEntry)deletions);
		Set<ContentField> del = null;
		super.subtract(new UserCatalogEntry(getUID(), delCE.getUserProfile()));
		if (delCE.contentProfile == null ||
				(del = delCE.contentProfile.getAllFields()).size() == 0)
			return this;

		for (ContentField cf : del)
			contentProfile.remove(cf);
		return this;
	}
}
