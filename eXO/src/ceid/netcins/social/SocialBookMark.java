

package ceid.netcins.social;

import ceid.netcins.content.ContentProfile;

/**
 * Social bookmarks are stored "addresses" of interest to the user content or
 * people or other. These addresses are stored together with some user defined
 * set of keywords or description text to describe the bookmark. CHECK : If we
 * can merge the content profile/user profile.
 * 
 * @author Andreas Loupasakis
 */
public interface SocialBookMark {

	public Object getAddress();

	public ContentProfile getTags();

}
