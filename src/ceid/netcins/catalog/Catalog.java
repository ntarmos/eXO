package ceid.netcins.catalog;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastException;
import ceid.netcins.messages.QueryPDU;

/**
 * A Catalog is a table of CatalogEntries for a specific term identifier(TID).
 * TID is a SHA-1 output of an indexing term. This class is very fundamental!
 * 
 * @author Andreas Loupasakis
 */
public class Catalog extends ContentHashPastContent {

	private static final long serialVersionUID = -6819758682396530715L;

	// The Content CatalogEntries
	private Set<ContentCatalogEntry> contentCatalogEntries;

	// the user catalog entries
	private Set<UserCatalogEntry> userCatalogEntries;

	// the url catalog entries
	private Set<URLCatalogEntry> urlCatalogEntries;

	public Catalog(Id tid) {
		super(tid);
		contentCatalogEntries = Collections.synchronizedSet(new HashSet<ContentCatalogEntry>());
		userCatalogEntries = Collections.synchronizedSet(new HashSet<UserCatalogEntry>());
		urlCatalogEntries = Collections.synchronizedSet(new HashSet<URLCatalogEntry>());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Catalog(Id tid, Set catalogEntries) {
		super(tid);
		if (catalogEntries == null || catalogEntries.size() == 0) {
			this.contentCatalogEntries = null;
			this.userCatalogEntries = null;
			this.urlCatalogEntries = null;
		} else if (catalogEntries.iterator().next() instanceof URLCatalogEntry) {
			this.urlCatalogEntries = Collections.synchronizedSet((Set<URLCatalogEntry>) catalogEntries);
			this.contentCatalogEntries = null;
			this.userCatalogEntries = null;
		} else if (catalogEntries.iterator().next() instanceof ContentCatalogEntry) {
			this.contentCatalogEntries = Collections.synchronizedSet((Set<ContentCatalogEntry>) catalogEntries);
			this.userCatalogEntries = null;
			this.urlCatalogEntries = null;
		} else if (catalogEntries.iterator().next() instanceof UserCatalogEntry) {
			this.contentCatalogEntries = null;
			this.userCatalogEntries = Collections.synchronizedSet((Set<UserCatalogEntry>) catalogEntries);
			this.urlCatalogEntries = null;
		}
	}
	
	/**
	 * Helper to get the proper entries corresponding to the query type issued
	 * by the user.
	 * 
	 * @param type One of the types defined in QueryPDU
	 * @return Return the corresponding vector of catalog entries.
	 */
	@SuppressWarnings("rawtypes")
	public Set getCatalogEntriesForQueryType(int type){
		Set v = null;
		switch(type){
			case QueryPDU.CONTENTQUERY:
				v = contentCatalogEntries;
				break;
			case QueryPDU.CONTENT_ENHANCEDQUERY:
				v = contentCatalogEntries;
				break;
			case QueryPDU.USERQUERY:
				v = userCatalogEntries;
				break;
			case QueryPDU.USER_ENHANCEDQUERY:
				v = userCatalogEntries;
				break;
			case QueryPDU.HYBRIDQUERY:
				v = contentCatalogEntries;
				break;
			case QueryPDU.HYBRID_ENHANCEDQUERY:
				v = contentCatalogEntries;
				break;
			case QueryPDU.URLQUERY:
				v = urlCatalogEntries;
				break;
		}
		return v; 
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
	 * an update to the profile was occurred!
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
	 * Just returns the TID of the specific Catalog Wrapper function.
	 * 
	 * @return Id of ContentHashPastContent
	 */
	public Id getTID() {
		return getId();
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

	/**
	 * States if this content object is mutable. Mutable objects are not subject
	 * to dynamic caching in Past.
	 * 
	 * @return true if this object is mutable, else false
	 */
	@Override
	public boolean isMutable() {
		return true;
	}

	/**
	 * Just an override version of the ContentHashPastContent.checkInsert that
	 * is doing nothing at all. It is just to disable the method.
	 * 
	 * @param id
	 *            the key identifying the object
	 * @param existingContent
	 *            DESCRIBE THE PARAMETER
	 * @return null, if the operation is not allowed; else, the new object to be
	 *         stored on the local node.
	 * @exception PastException
	 *                DESCRIBE THE EXCEPTION
	 */

	@Override
	public PastContent checkInsert(Id id, PastContent existingContent)
			throws PastException {

		// only allow correct content hash key
		if (!id.equals(getId())) {
			throw new PastException(
					"ContentHashPastContent: can't insert, content hash incorrect");
		}
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Catalog [TID=" + this.myId + "]");
		buf.append("\n [Content Catalog Entries] \n");
		if (contentCatalogEntries != null)
			for (ContentCatalogEntry e : contentCatalogEntries)
				buf.append(e.toString());
		buf.append("\n [User Catalog Entries] \n");
		if (userCatalogEntries != null)
			for (UserCatalogEntry e : userCatalogEntries)
				buf.append(e.toString());
		buf.append("\n [URL Catalog Entries] \n");
		if (urlCatalogEntries != null)
			for (URLCatalogEntry e : urlCatalogEntries)
				buf.append(e.toString());

		return buf.toString();
	}

	public double computeBytes() {
		double counter = 0;
		counter += this.myId.getByteArrayLength();

		if (contentCatalogEntries != null)
			for (ContentCatalogEntry e : contentCatalogEntries)
				counter += e.computeTotalBytes();

		if (userCatalogEntries != null)
			for (UserCatalogEntry e : userCatalogEntries)
				counter += e.computeTotalBytes();

		if (urlCatalogEntries != null)
			for (URLCatalogEntry e : urlCatalogEntries)
				counter += e.computeTotalBytes();

		return counter;
	}
}
