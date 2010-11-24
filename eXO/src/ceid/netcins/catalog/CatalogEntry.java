/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.catalog;

import java.io.Serializable;

import rice.p2p.commonapi.Id;

/**
 * This is a sum of data in a Catalog row!
 *
 * @author andy
 * @version 1.0
 * 
 */
@SuppressWarnings("unchecked")
public class CatalogEntry implements Serializable, Comparable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6930057763768157893L;
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
   @Override
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
    
    @Override
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
