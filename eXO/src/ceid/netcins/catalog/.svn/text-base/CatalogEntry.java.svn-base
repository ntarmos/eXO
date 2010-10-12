/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.catalog;

import ceid.netcins.content.ContentField;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.content.StoredField;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import rice.p2p.commonapi.Id;

/**
 * This is a sum of data in a Catalog row!
 *
 * @author andy
 * @version 1.0
 * 
 */
public class CatalogEntry implements Serializable, Comparable{

    // User identifier (or node Identifier)
    private Id uid;
    
    public CatalogEntry(Id uid){
        this.uid = uid;
    }
    
  /**
   * Used to compare two entries.
   * Two entries are the same if:
   * they have the same UID
   * (and the same SHA-1 checksum
   * if it is content entry)
   *
   * @param o DESCRIBE THE PARAMETER
   * @return DESCRIBE THE RETURN VALUE
   */
   public boolean equals(Object o) {
      if (!(o instanceof CatalogEntry)) {
        return false;
      }
      CatalogEntry ce = (CatalogEntry) o;
      
      return ce.getUID().equals(uid);
    }
  
    /**
     * Getter for the uid of User
     * 
     * @return the uid
     */
    public Id getUID(){
        return this.uid;
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("Catalog Entry : [UID] = ");
        buf.append(this.uid.toString());
        return buf.toString();
    }
    
    /**
     * Implementation for Comparable interface
     * 
     * @param arg0
     * @return
     */
    public int compareTo(Object arg0) {      
        if (!(arg0 instanceof CatalogEntry)) {
            throw new ClassCastException();
      }
      CatalogEntry ce = (CatalogEntry) arg0;
      
      if(this.equals(arg0))
          return 0;
      else{
          return this.getUID().compareTo(ce.getUID());
      }
    }
}
