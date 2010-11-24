/*
 * DummyContent.java
 *
 * Created on 22 �������� 2006, 1:36 ��
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ceid.netcins.content;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastException;

/**
 *
 * @author and
 */
public class DummyContent extends ContentHashPastContent {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -8449056284245401117L;

	//NodeId content;
    SortedSet<Id> nodeList;
    
    //Version of the content
    int version=0;
    
    /** Creates a new instance of DummyContent */
    public DummyContent(Id id,Id nodeId,int version) {
        super(id);
        this.version = version;
        //content=str;
        nodeList=Collections.synchronizedSortedSet(new TreeSet<Id>());
        nodeList.add(nodeId);  // During creation in the Starting Node there is no need to synchronize
    }
    
    /**
   * States if this content object is mutable. Mutable objects are not subject
   * to dynamic caching in Past.
   *
   * @return true if this object is mutable, else false
   */
  @Override
public boolean isMutable() {
    return true;
  }

  /**
   * Checks if a insert operation should be allowed. Invoked when a Past node
   * receives an insert request and it is a replica root for the id; invoked on
   * the object to be inserted. This method determines the effect of an insert
   * operation on an object that already exists: it computes the new value of
   * the stored object, as a function of the new and the existing object.
   *
   * @param id the key identifying the object
   * @param existingContent DESCRIBE THE PARAMETER
   * @return null, if the operation is not allowed; else, the new object to be
   *      stored on the local node.
   * @exception PastException DESCRIBE THE EXCEPTION
   */
  @Override
public PastContent checkInsert(Id id, PastContent existingContent) throws PastException {
    // can overwrite content hash objects
      /* TODO : edw 8a ginetai h enhmerwsh
       * tou content tou NodeLIST dhladh
       * 8a elegxetai to TreeSet kai 8a 
       * enhmerwnetai me ta nea NodeIds
       */
    if (existingContent != null) {
        if(existingContent instanceof DummyContent){
            synchronized(((DummyContent) existingContent).nodeList){
                ((DummyContent) existingContent).nodeList.addAll(nodeList);
            }
            return existingContent;
        }else
            throw new PastException("DummyContent: can't insert, object already exists"); 
    }

    // only allow correct content hash key
    if (!id.equals(getId())) {
      throw new PastException("ContentHashPastContent: can't insert, content hash incorrect");
    }
    return this;
  }
  
  /**
   * Used to compare two contents
   *
   * @param o DESCRIBE THE PARAMETER
   * @return DESCRIBE THE RETURN VALUE
   */
    @Override
	public boolean equals(Object o) {
      if (!(o instanceof DummyContent)) {
        return false;
      }

      return (((DummyContent) o).myId.equals(myId) &&
        (((DummyContent) o).version == version));
    }

  /**
   * A descriptive toString()
   */
    @Override
	public String toString(){
     return "NodeList Content : [ "+nodeList+" ]";   
    }
    
}
