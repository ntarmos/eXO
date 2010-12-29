

package ceid.netcins.exo.social;

import ceid.netcins.exo.content.ContentProfile;

/**
 * Social bookmarks are stored "addresses" of interest to the user content or
 * people or other. These addresses are stored together with some user defined
 * set of keywords or description text to describe the bookmark. CHECK : If we
 * can merge the content profile/user profile.
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public interface SocialBookMark {

	public Object getAddress();

	public ContentProfile getTags();

}
