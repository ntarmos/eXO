/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.catalog;

import java.util.Vector;

/**
 * This is a Catalog type (inverted List) with a set of CatalogEntries,
 * which correspond to tager's taged objects.
 *
 * @author andy
 */
public class SocialCatalog {
    
    // The tag of this inverted list
    private String tag;
    
   // The Content CatalogEntries
    private Vector<ContentCatalogEntry> contentCatalogEntries;
    
    // the user catalog entries
    private Vector<UserCatalogEntry> userCatalogEntries;
    
    // the user catalog entries
    private Vector<URLCatalogEntry> urlCatalogEntries;
    
    public SocialCatalog(String tag){
        this.tag = tag;
        contentCatalogEntries = new Vector<ContentCatalogEntry>();
        userCatalogEntries = new Vector<UserCatalogEntry>();
        urlCatalogEntries = new Vector<URLCatalogEntry>();
    }
    
    @SuppressWarnings("unchecked")
	public SocialCatalog(String tag,Vector catalogEntries){
        this.tag = tag;
        if(catalogEntries.firstElement()==null){
            this.contentCatalogEntries = null;
            this.userCatalogEntries = null;
        }else if(catalogEntries.firstElement() instanceof URLCatalogEntry){
            this.urlCatalogEntries = (Vector<URLCatalogEntry>) catalogEntries;
            this.contentCatalogEntries = null;
            this.userCatalogEntries = null;
        }else if(catalogEntries.firstElement() instanceof ContentCatalogEntry){
            this.contentCatalogEntries = (Vector<ContentCatalogEntry>) catalogEntries;
            this.userCatalogEntries = null;
            this.urlCatalogEntries = null;
        }else if(catalogEntries.firstElement() instanceof UserCatalogEntry){
            this.contentCatalogEntries = null;
            this.userCatalogEntries = (Vector<UserCatalogEntry>) catalogEntries;
            this.urlCatalogEntries = null;
        }
    }
    
    public void setContentCatalogEntries(Vector<ContentCatalogEntry> v){
        this.contentCatalogEntries = v;
    }
    
    public void setUserCatalogEntries(Vector<UserCatalogEntry> v){
        this.userCatalogEntries = v;
    }
    
     
    public void setURLCatalogEntries(Vector<URLCatalogEntry> v){
        this.urlCatalogEntries = v;
    }
    
    /**
     * Adds a ContentCatalogEntry in contentCatalogEntries Vector
     * 
     * @param ce
     */
    public void addContentCatalogEntry(ContentCatalogEntry ce){
        contentCatalogEntries.add(ce);
    }
    
    /**
     * Adds a UserCatalogEntry in the userCatalogEntries Vector
     * 
     * @param ue
     */
    public void addUserCatalogEntry(UserCatalogEntry ue){
        userCatalogEntries.add(ue);
    }
    
        
    /**
     * Adds a URLCatalogEntry in the urlCatalogEntries Vector
     * 
     * @param ue
     */
    public void addURLCatalogEntry(URLCatalogEntry ue){
        urlCatalogEntries.add(ue);
    }
    
    /**
     * Just removes the old entry and adds the updated one!
     * This is usefull when an update to the profile was occured!
     * 
     * @param oldCE
     * @param newCE
     */
    public void replaceContentCatalogEntry(ContentCatalogEntry oldCE, ContentCatalogEntry newCE){
        // TODO : Check the correctness of the remove operation
        contentCatalogEntries.remove(oldCE);
        contentCatalogEntries.add(newCE);
    }
    
    /**
     * Replaces the old entry with the new one by removing and adding the 
     * corresponding entries
     * 
     * @param oldUE
     * @param newUE
     */
    public void replaceUserCatalogEntry(UserCatalogEntry oldUE, UserCatalogEntry newUE){
        // TODO : Check the correctness of the remove operation
        userCatalogEntries.remove(oldUE);
        userCatalogEntries.add(newUE);
    }
    
    /**
     * Replaces the old entry with the new one by removing and adding the 
     * corresponding entries
     * 
     * @param oldUE
     * @param newUE
     */
    public void replaceURLCatalogEntry(URLCatalogEntry oldUE, URLCatalogEntry newUE){
        // TODO : Check the correctness of the remove operation
        urlCatalogEntries.remove(oldUE);
        urlCatalogEntries.add(newUE);
    }
    
    /**
     * Getter for the tag string.
     * 
     * @return
     */
    public String getTag(){
        return tag;
    }
    
    /**
     * Getter for the contentCatalogEntries vector
     * 
     * @return the entries of catalog
     */
    public Vector<ContentCatalogEntry> getContentCatalogEntries(){
        return contentCatalogEntries;
    }
    
    /**
     * Getter for the userCatalogEntries vector
     * 
     * @return
     */
    public Vector<UserCatalogEntry> getUserCatalogEntries(){
        return userCatalogEntries;
    }
    
        /**
     * Getter for the urlCatalogEntries vector
     * 
     * @return
     */
    public Vector<URLCatalogEntry> getURLCatalogEntries(){
        return urlCatalogEntries;
    }
    
    @Override
   public String toString(){
       StringBuffer buf = new StringBuffer();
       buf.append("SocialCatalog [Tag = "+this.tag+"]");
       buf.append("\n [Content Catalog Entries] \n");
       for(int i=0; i<contentCatalogEntries.size(); i++){
           buf.append(contentCatalogEntries.get(i).toString());
       }
       buf.append("\n [User Catalog Entries] \n");
       for(int i=0; i<userCatalogEntries.size(); i++){
           buf.append(userCatalogEntries.get(i).toString());
       } 
       buf.append("\n [URL Catalog Entries] \n");
       for(int i=0; i<urlCatalogEntries.size(); i++){
           buf.append(urlCatalogEntries.get(i).toString());
       }
       
       return buf.toString();
   }
}
