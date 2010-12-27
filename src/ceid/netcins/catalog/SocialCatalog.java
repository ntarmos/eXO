

package ceid.netcins.catalog;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * This is a Catalog type (inverted List) with a set of CatalogEntries, which
 * correspond to tager's taged objects.
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public class SocialCatalog {

	// The tag of this inverted list
	private String tag;

	// The Content CatalogEntries
	private Set<ContentCatalogEntry> contentCatalogEntries;

	// the user catalog entries
	private Set<UserCatalogEntry> userCatalogEntries;

	// the url catalog entries
	private Set<URLCatalogEntry> urlCatalogEntries;

	public SocialCatalog(String tag) {
		this.tag = tag;
		contentCatalogEntries = Collections.synchronizedSet(new HashSet<ContentCatalogEntry>());
		userCatalogEntries = Collections.synchronizedSet(new HashSet<UserCatalogEntry>());
		urlCatalogEntries = Collections.synchronizedSet(new HashSet<URLCatalogEntry>());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SocialCatalog(String tag, Vector catalogEntries) {
		this.tag = tag;
		if (catalogEntries.firstElement() == null) {
			this.contentCatalogEntries = null;
			this.userCatalogEntries = null;
		} else if (catalogEntries.firstElement() instanceof URLCatalogEntry) {
			this.urlCatalogEntries = Collections.synchronizedSet((Set<URLCatalogEntry>) catalogEntries);
			this.contentCatalogEntries = null;
			this.userCatalogEntries = null;
		} else if (catalogEntries.firstElement() instanceof ContentCatalogEntry) {
			this.contentCatalogEntries = Collections.synchronizedSet((Set<ContentCatalogEntry>) catalogEntries);
			this.userCatalogEntries = null;
			this.urlCatalogEntries = null;
		} else if (catalogEntries.firstElement() instanceof UserCatalogEntry) {
			this.contentCatalogEntries = null;
			this.userCatalogEntries = Collections.synchronizedSet((Set<UserCatalogEntry>) catalogEntries);
			this.urlCatalogEntries = null;
		}
	}

	public void setContentCatalogEntries(Set<ContentCatalogEntry> v) {
		this.contentCatalogEntries = Collections.synchronizedSet(v);
	}

	public void setUserCatalogEntries(Set<UserCatalogEntry> v) {
		this.userCatalogEntries = Collections.synchronizedSet(v);
	}

	public void setURLCatalogEntries(Set<URLCatalogEntry> v) {
		this.urlCatalogEntries = Collections.synchronizedSet(v);
	}

	/**
	 * Adds a ContentCatalogEntry in contentCatalogEntries Vector
	 * 
	 * @param ce
	 */
	public void addContentCatalogEntry(ContentCatalogEntry ce) {
		contentCatalogEntries.add(ce);
	}

	/**
	 * Adds a UserCatalogEntry in the userCatalogEntries Vector
	 * 
	 * @param ue
	 */
	public void addUserCatalogEntry(UserCatalogEntry ue) {
		userCatalogEntries.add(ue);
	}

	/**
	 * Adds a URLCatalogEntry in the urlCatalogEntries Vector
	 * 
	 * @param ue
	 */
	public void addURLCatalogEntry(URLCatalogEntry ue) {
		urlCatalogEntries.add(ue);
	}

	/**
	 * Just removes the old entry and adds the updated one! This is usefull when
	 * an update to the profile was occured!
	 * 
	 * @param oldCE
	 * @param newCE
	 */
	public void replaceContentCatalogEntry(ContentCatalogEntry oldCE,
			ContentCatalogEntry newCE) {
		synchronized (contentCatalogEntries) {
			contentCatalogEntries.remove(oldCE);
			contentCatalogEntries.add(newCE);
		}
	}

	/**
	 * Replaces the old entry with the new one by removing and adding the
	 * corresponding entries
	 * 
	 * @param oldUE
	 * @param newUE
	 */
	public void replaceUserCatalogEntry(UserCatalogEntry oldUE,
			UserCatalogEntry newUE) {
		synchronized (userCatalogEntries) {
			userCatalogEntries.remove(oldUE);
			userCatalogEntries.add(newUE);
		}
	}

	/**
	 * Replaces the old entry with the new one by removing and adding the
	 * corresponding entries
	 * 
	 * @param oldUE
	 * @param newUE
	 */
	public void replaceURLCatalogEntry(URLCatalogEntry oldUE,
			URLCatalogEntry newUE) {
		synchronized (urlCatalogEntries) {
			urlCatalogEntries.remove(oldUE);
			urlCatalogEntries.add(newUE);
		}
	}

	/**
	 * Getter for the tag string.
	 * 
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Getter for the contentCatalogEntries vector
	 * 
	 * @return the entries of catalog
	 */
	public Set<ContentCatalogEntry> getContentCatalogEntries() {
		return contentCatalogEntries;
	}

	/**
	 * Getter for the userCatalogEntries vector
	 * 
	 * @return
	 */
	public Set<UserCatalogEntry> getUserCatalogEntries() {
		return userCatalogEntries;
	}

	/**
	 * Getter for the urlCatalogEntries vector
	 * 
	 * @return
	 */
	public Set<URLCatalogEntry> getURLCatalogEntries() {
		return urlCatalogEntries;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("SocialCatalog [Tag = " + this.tag + "]");
		buf.append("\n [Content Catalog Entries] \n");
		for (ContentCatalogEntry cce : contentCatalogEntries) {
			buf.append(cce.toString());
		}
		buf.append("\n [User Catalog Entries] \n");
		for (UserCatalogEntry uce: userCatalogEntries) {
			buf.append(uce.toString());
		}
		buf.append("\n [URL Catalog Entries] \n");
		for (URLCatalogEntry urlce : urlCatalogEntries) {
			buf.append(urlce.toString());
		}

		return buf.toString();
	}
}
