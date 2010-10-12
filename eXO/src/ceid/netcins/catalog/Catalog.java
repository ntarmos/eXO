/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.catalog;

import java.util.Vector;
import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastException;

/**A Catalog is a table of CatalogEntries for a specific term identifier(TID).
 * TID is a SHA-1 output of an indexing term. This class is very fundamental!
 *
 * @author andy
 * @version 1.0
 */
public class Catalog extends ContentHashPastContent{
    
    // The Content CatalogEntries
    private Vector<ContentCatalogEntry> contentCatalogEntries;
    
    // the user catalog entries
    private Vector<UserCatalogEntry> userCatalogEntries;
    
    // the user catalog entries
    private Vector<URLCatalogEntry> urlCatalogEntries;
    
    public Catalog(Id tid){
        super(tid);
        contentCatalogEntries = new Vector<ContentCatalogEntry>();
        userCatalogEntries = new Vector<UserCatalogEntry>();
        urlCatalogEntries = new Vector<URLCatalogEntry>();
    }
    
    public Catalog(Id tid, Vector catalogEntries){
        super(tid);
        if(catalogEntries.firstElement()==null){
            this.contentCatalogEntries = null;
            this.userCatalogEntries = null;
        }else if(catalogEntries.firstElement() instanceof URLCatalogEntry){
            this.urlCatalogEntries = (Vector<URLCatalogEntry>)catalogEntries;
            this.contentCatalogEntries = null;
            this.userCatalogEntries = null;
        }else if(catalogEntries.firstElement() instanceof ContentCatalogEntry){
            this.contentCatalogEntries = (Vector<ContentCatalogEntry>)catalogEntries;
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
     * Just returns the TID of the specific Catalog
     * Wrapper function.
     * 
     * @return Id of ContentHashPastContent
     */
    public Id getTID(){
        return getId();
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
    
    /**
     * States if this content object is mutable. Mutable objects are not subject
     * to dynamic caching in Past.
     *
     * @return true if this object is mutable, else false
     */
    public boolean isMutable() {
        return true;
    }
    
  /**
   * Just an override version of the ContentHashPastContent.checkInsert
   * that is doing nothing at all. It is just to disable the method.
   * 
   * @param id the key identifying the object
   * @param existingContent DESCRIBE THE PARAMETER
   * @return null, if the operation is not allowed; else, the new object to be
   *      stored on the local node.
   * @exception PastException DESCRIBE THE EXCEPTION
   */

   public PastContent checkInsert(Id id, PastContent existingContent) throws PastException {

    // only allow correct content hash key
    if (!id.equals(getId())) {
      throw new PastException("ContentHashPastContent: can't insert, content hash incorrect");
    }
    return this;
   }
   
   public String toString(){
       StringBuffer buf = new StringBuffer();
       buf.append("Catalog [TID="+this.myId+"]");
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
   
       
   public double computeBytes(){
       double counter = 0;
       counter += this.myId.getByteArrayLength();
        
       if(contentCatalogEntries!=null){
           for(int i=0; i<contentCatalogEntries.size(); i++){
                  counter += contentCatalogEntries.get(i).computeTotalBytes();
           }
       }
       
       if(userCatalogEntries!=null){
           for(int i=0; i<userCatalogEntries.size(); i++){
                  counter += userCatalogEntries.get(i).computeTotalBytes();
           }
       }
       
       return counter;
    }
}