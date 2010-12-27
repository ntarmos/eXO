package ceid.netcins.catalog;

import java.util.Hashtable;
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
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public class Catalog extends ContentHashPastContent {

	private static final long serialVersionUID = -6819758682396530715L;

	// The Content CatalogEntries
	private Hashtable<Id, ContentCatalogEntry> contentCatalogEntries;

	// the user catalog entries
	private Hashtable<Id, UserCatalogEntry> userCatalogEntries;

	// the url catalog entries
	private Hashtable<Id, URLCatalogEntry> urlCatalogEntries;

	public Catalog(Id tid) {
		super(tid);
		contentCatalogEntries = new Hashtable<Id, ContentCatalogEntry>();
		userCatalogEntries = new Hashtable<Id, UserCatalogEntry>();
		urlCatalogEntries = new Hashtable<Id, URLCatalogEntry>();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Catalog(Id tid, Set catalogEntries) {
		super(tid);
		if (catalogEntries == null || catalogEntries.size() == 0) {
			this.contentCatalogEntries = null;
			this.userCatalogEntries = null;
			this.urlCatalogEntries = null;
		} else if (catalogEntries.iterator().next() instanceof URLCatalogEntry) {
			this.urlCatalogEntries = (Hashtable<Id, URLCatalogEntry>) catalogEntries;
			this.contentCatalogEntries = null;
			this.userCatalogEntries = null;
		} else if (catalogEntries.iterator().next() instanceof ContentCatalogEntry) {
			this.contentCatalogEntries = (Hashtable<Id, ContentCatalogEntry>) catalogEntries;
			this.userCatalogEntries = null;
			this.urlCatalogEntries = null;
		} else if (catalogEntries.iterator().next() instanceof UserCatalogEntry) {
			this.contentCatalogEntries = null;
			this.userCatalogEntries = (Hashtable<Id, UserCatalogEntry>)catalogEntries;
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
	public Hashtable getCatalogEntriesForQueryType(int type){
		switch(type){
			case QueryPDU.CONTENTQUERY:
			case QueryPDU.CONTENT_ENHANCEDQUERY:
			case QueryPDU.HYBRIDQUERY:
			case QueryPDU.HYBRID_ENHANCEDQUERY:
				return contentCatalogEntries;
			case QueryPDU.USERQUERY:
			case QueryPDU.USER_ENHANCEDQUERY:
				return userCatalogEntries;
			case QueryPDU.URLQUERY:
				return urlCatalogEntries;
		}
		return null; 
	}
	
	public void setContentCatalogEntries(Hashtable<Id, ContentCatalogEntry> v) {
		this.contentCatalogEntries = v;
	}

	public void setUserCatalogEntries(Hashtable<Id, UserCatalogEntry> v) {
		this.userCatalogEntries = v;
	}

	public void setURLCatalogEntries(Hashtable<Id, URLCatalogEntry> v) {
		this.urlCatalogEntries = v;
	}

	/**
	 * Adds a ContentCatalogEntry in contentCatalogEntries Vector
	 * 
	 * @param ce
	 */
	public void addContentCatalogEntry(ContentCatalogEntry ce) {
		contentCatalogEntries.put(ce.getUID(), ce);
	}

	/**
	 * Adds a UserCatalogEntry in the userCatalogEntries Vector
	 * 
	 * @param ue
	 */
	public void addUserCatalogEntry(UserCatalogEntry ue) {
		userCatalogEntries.put(ue.getUID(), ue);
	}

	/**
	 * Adds a URLCatalogEntry in the urlCatalogEntries Vector
	 * 
	 * @param ue
	 */
	public void addURLCatalogEntry(URLCatalogEntry ue) {
		urlCatalogEntries.put(ue.getUID(), ue);
	}

	public void addCatalogEntry(CatalogEntry ce) {
		if (ce instanceof URLCatalogEntry)
			addURLCatalogEntry((URLCatalogEntry)ce);
		else if (ce instanceof ContentCatalogEntry)
			addContentCatalogEntry((ContentCatalogEntry)ce);
		else
			addUserCatalogEntry((UserCatalogEntry)ce);
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
		if (!oldCE.getUID().equals(newCE.getUID()))
			throw new RuntimeException("Trying to replace entry with one with a different id");

		synchronized (contentCatalogEntries) {
			contentCatalogEntries.remove(oldCE.getUID());
			contentCatalogEntries.put(newCE.getUID(), newCE);
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
		if (!oldUE.getUID().equals(newUE.getUID()))
			throw new RuntimeException("Trying to replace entry with one with a different id");

		synchronized (userCatalogEntries) {
			userCatalogEntries.remove(oldUE.getUID());
			userCatalogEntries.put(newUE.getUID(), newUE);
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
		if (!oldUE.getUID().equals(newUE.getUID()))
			throw new RuntimeException("Trying to replace entry with one with a different id");

		synchronized (urlCatalogEntries) {
			urlCatalogEntries.remove(oldUE.getUID());
			urlCatalogEntries.put(newUE.getUID(), newUE);
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
	public Hashtable<Id, ContentCatalogEntry> getContentCatalogEntries() {
		return contentCatalogEntries;
	}

	/**
	 * Getter for the userCatalogEntries vector
	 * 
	 * @return
	 */
	public Hashtable<Id, UserCatalogEntry> getUserCatalogEntries() {
		return userCatalogEntries;
	}

	/**
	 * Getter for the urlCatalogEntries vector
	 * 
	 * @return
	 */
	public Hashtable<Id, URLCatalogEntry> getURLCatalogEntries() {
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
			for (ContentCatalogEntry e : contentCatalogEntries.values())
				buf.append(e.toString());
		buf.append("\n [User Catalog Entries] \n");
		if (userCatalogEntries != null)
			for (UserCatalogEntry e : userCatalogEntries.values())
				buf.append(e.toString());
		buf.append("\n [URL Catalog Entries] \n");
		if (urlCatalogEntries != null)
			for (URLCatalogEntry e : urlCatalogEntries.values())
				buf.append(e.toString());

		return buf.toString();
	}

	public double computeBytes() {
		double counter = 0;
		counter += this.myId.getByteArrayLength();

		if (contentCatalogEntries != null)
			for (ContentCatalogEntry e : contentCatalogEntries.values())
				counter += e.computeTotalBytes();

		if (userCatalogEntries != null)
			for (UserCatalogEntry e : userCatalogEntries.values())
				counter += e.computeTotalBytes();

		if (urlCatalogEntries != null)
			for (URLCatalogEntry e : urlCatalogEntries.values())
				counter += e.computeTotalBytes();

		return counter;
	}
}
