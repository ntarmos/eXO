/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.messages;

import ceid.netcins.catalog.Catalog;
import ceid.netcins.catalog.CatalogEntry;
import ceid.netcins.catalog.ContentCatalogEntry;
import ceid.netcins.catalog.URLCatalogEntry;
import ceid.netcins.catalog.UserCatalogEntry;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastException;

/**This class represents the Protocol Data Unit (PDU) of the insert message of
 * our DHTService. This class will include mostly CatalogEntries or data 
 * for the update of the Catalog entries.
 *
 * @author andy
 * @version 1.0
 */
public class InsertPDU extends ContentHashPastContent implements Serializable{
    
    Object data; // The packet data
    
    
    public InsertPDU(Id tid, Object obj){
        super(tid);
        this.data = obj;
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
   * Checks if an insert operation should be allowed. Invoked when a Past node
   * receives an insert request and it is a replica root for the id; invoked on
   * the object to be inserted. This method determines the effect of an insert
   * operation on an object that already exists: it computes the new value of
   * the stored object, as a function of the new and the existing object.
   * 
   * This is an overriden version of this method. It is called in the indexing
   * process when a new content object has arrived to the destination Catalog
   * node. The Catalog is checked to ensure that no exactly same CatalogEntry
   * exists. If it is so, then the new entry is added to the current Catalog of
   * the specific TID which is being indexed.
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
    
    CatalogEntry entry;
    if(this.data instanceof CatalogEntry){        
        entry = (CatalogEntry) this.data;
    }else{
        throw new PastException("Catalog : can't insert, the insertion object for the TID("+this.myId+") is of unknown class type"); 
    }
       
    if (existingContent != null) {
        if(existingContent instanceof Catalog){
            Catalog catalog = (Catalog)existingContent;
            // TODO : check if the appropriate synchronization is done!
            // Here is the main processing of new data
            if(entry instanceof URLCatalogEntry){   // URL entry
                URLCatalogEntry newue = (URLCatalogEntry) entry;
                List<URLCatalogEntry> list = catalog.getURLCatalogEntries();
                Iterator<URLCatalogEntry> it = list.iterator();
                URLCatalogEntry ue;
                while(it.hasNext()){
                    ue = it.next();
                    // TODO : Update of the existing entry!
                    if(ue.equals(newue)){ // We must do some update here or we have a duplicate!
                        catalog.replaceURLCatalogEntry(ue, newue);
                        return existingContent; // The same reference as catalog!
                    } 
                }
                // A new entry for our catalog, as we didn't find the same entry!
                catalog.addURLCatalogEntry(newue);
               
            }else if(entry instanceof ContentCatalogEntry){   // Content entry
                ContentCatalogEntry newce = (ContentCatalogEntry) entry;
                List<ContentCatalogEntry> list = catalog.getContentCatalogEntries();
                Iterator<ContentCatalogEntry> it = list.iterator();
                ContentCatalogEntry ce;
                while(it.hasNext()){
                    ce = it.next();
                    // TODO : Update of the existing entry!
                    if(ce.equals(newce)){ // We must do some update here or we have a duplicate!
                        catalog.replaceContentCatalogEntry(ce, newce);
                        return existingContent; // The same reference as catalog!
                    } 
                }
                // A new entry for our catalog, as we didn't find the same entry!
                catalog.addContentCatalogEntry(newce);
                
            }else if(entry instanceof UserCatalogEntry){ // User entry
                UserCatalogEntry newue = (UserCatalogEntry) entry;
                List<UserCatalogEntry> list = catalog.getUserCatalogEntries();
                Iterator<UserCatalogEntry> it = list.iterator();
                UserCatalogEntry ue;
                while(it.hasNext()){
                    ue = it.next();
                    // TODO : Update of the existing entry!
                    if(ue.equals(newue)){ // We must do some update here or we have a duplicate!
                        catalog.replaceUserCatalogEntry(ue, newue);
                        return existingContent; // The same reference as catalog!
                    } 
                }
                // A new entry for our catalog, as we didn't find the same entry!
                catalog.addUserCatalogEntry(newue);
            }
            return existingContent; // The same reference as catalog!
        }else
            throw new PastException("Catalog : can't insert, existing object for the TID("+this.myId+") is of unknown class type");
        
    }else{      // There is no Catalog for this TID! Let's create one :-)
        
        Catalog c = new Catalog(id);
        if(entry instanceof URLCatalogEntry)
            c.addURLCatalogEntry((URLCatalogEntry)entry);
        else if(entry instanceof ContentCatalogEntry)
            c.addContentCatalogEntry((ContentCatalogEntry)entry);
        else if(entry instanceof UserCatalogEntry){
            c.addUserCatalogEntry((UserCatalogEntry)entry);
        }
        return c;
    }
  }
   
}
