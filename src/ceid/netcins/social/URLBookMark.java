

package ceid.netcins.social;

import java.net.URL;

import ceid.netcins.content.ContentProfile;

/**
 * URL bookmarks are stored "addresses" of interest to the user content.
 * Specifically, they are references to web content that is in general out of
 * the system (users does not store this content locally). This content is
 * accessed through web servers using http/ftp protocol. These addresses are
 * stored together with some user defined set of keywords or description text
 * called tags to describe the bookmark.
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public class URLBookMark implements SocialBookMark {

	// The resource URL
	private URL address;

	// the set of tags describing the URL
	private ContentProfile tags;

	/**
	 * Constructor
	 */
	public URLBookMark(URL url, ContentProfile tags) {

		address = url;
		this.tags = tags;
	}

	/**
	 * Getter for the set of tags
	 * 
	 * @return
	 */
	public ContentProfile getTags() {
		return tags;
	}

	/**
	 * Getter for the URL address
	 * 
	 * @return
	 */
	public URL getAddress() {
		return address;
	}
}
