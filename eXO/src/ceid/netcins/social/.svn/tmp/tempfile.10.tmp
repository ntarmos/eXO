/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.social;

import ceid.netcins.content.ContentProfile;
import java.net.URL;

/**
 * URL bookmarks are stored "addresses" of interesting to the user content.
 * Specifically, they are references to web content that is in general
 * out of the system (users does not store this content locally).
 * This content is accessed through web servers using http/ftp protocol.
 * These addresses are stored together with some user defined 
 * set of keywords or description text called tags to describe the bookmark.
 * 
 * @author andy
 */
public class URLBookMark implements SocialBookMark{
    
    // The resource URL
    private URL address;
    
    // the set of tags describing the URL
    private ContentProfile tags;
    
    /**
     * Constructor
     */
    public URLBookMark(URL url,ContentProfile tags) {
        
        address = url;
        this.tags = tags;
    }
    
    /**
     * Getter for the set of tags
     * 
     * @return
     */
    public ContentProfile getTags(){
        return tags;
    }

    /**
     * Getter for the URL address
     * 
     * @return
     */
    public URL getAddress(){
        return address;
    }
}
