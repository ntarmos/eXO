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
 * This class extends the user catalog entries to contain content information
 * bundled with the user indexed content.
 *
 * 
 * @author andy
 */
public class ContentCatalogEntry extends UserCatalogEntry implements Serializable, Comparable{
    
    // The profile for a specific shared content object 
    private ContentProfile contentProfile;
    
    public ContentCatalogEntry(Id uid, ContentProfile cp, ContentProfile usrp){
        super(uid,usrp);
        this.contentProfile = cp;
    }
    
  /**
   * Used to compare two entries.
   * Two entries are the same if:
   * they have the same UID
   * andh the same SHA-1 checksum
   *
   * @param o DESCRIBE THE PARAMETER
   * @return DESCRIBE THE RETURN VALUE
   */
   public boolean equals(Object o) {
      if (!(o instanceof ContentCatalogEntry)) {
        return false;
      }
      ContentCatalogEntry ce = (ContentCatalogEntry) o;

      String checksum = getCheckSum();
      // TODO : FieldException!!!!!
      if(checksum == null || ce.getCheckSum() == null){ // This is a problematic occasion. It should be marked with an exception
          return false;
      }
      
      return (ce.getUID().equals(getUID()) ? ce.getCheckSum().equals(checksum) : false) ;
    }
   
   /**
    * 
    * @return the ContentField "SHA-1", null if there is no such a field.
    */
   public String getCheckSum() {
       
      List<ContentField> list = contentProfile.getFields();
      Iterator<ContentField> it = contentProfile.getFields().iterator();
      ContentField cf;
      while(it.hasNext()){
          cf =it.next();
          if(cf.getFieldName().equals("SHA-1") && cf instanceof StoredField){
              return ((StoredField)cf).getFieldData();
          }
      }
      return null;
   }
    
    /**
     * Getter for the contentProfile
     * 
     * @return contentProfile
     */
    public ContentProfile getContentProfile(){
            return this.contentProfile;
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("Catalog Entry : [UID] = ");
        buf.append(this.getUID().toString());
        buf.append(", [contentProfile] = ");
        if(contentProfile == null)
            buf.append("null");
        else
            buf.append(this.contentProfile.toString());
        buf.append("[userProfile] = ");
        if(this.getUserProfile() == null)
            buf.append("null");
        else
            buf.append(this.getUserProfile().toString());
        return buf.toString();
    }
    
    // TODO : implement this
    public int compareTo(Object arg0) {      
        if (!(arg0 instanceof ContentCatalogEntry)) {
            throw new ClassCastException();
      }
      ContentCatalogEntry ce = (ContentCatalogEntry) arg0;
      
      if(this.equals(arg0))
          return 0;
      else{
          if(this.getUID().compareTo(ce.getUID())==0){
              if(this.getCheckSum()==null)
                  return (ce.getCheckSum()==null ? 0 : -1);
              else
                  return (ce.getCheckSum() == null ? 1 : this.getCheckSum().compareTo(ce.getCheckSum()));
          }else{
              return this.getUID().compareTo(ce.getUID());
          }
      }
    }
    
    /**
     * 
     * @return A sum of the ContentCatalogEntry data in bytes
     */
    public double computeTotalBytes(){
        double counter = 0;
        counter += this.getUID().getByteArrayLength();
        if(this.getUserProfile()!=null)
            counter += this.getUserProfile().computeTotalBytes();
        counter += this.contentProfile.computeTotalBytes();
        
        return counter;
    }
}
