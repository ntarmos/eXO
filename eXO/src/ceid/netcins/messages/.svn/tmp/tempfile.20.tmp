/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.messages;

import ceid.netcins.utils.JavaSerializer;
import java.io.IOException;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.OutputBuffer;
import rice.p2p.past.messaging.ContinuationMessage;
import rice.p2p.past.rawserialization.PastContentDeserializer;
import rice.p2p.past.rawserialization.RawPastContent;

/**
 * Represents a message for a social query request (friends/neighbor etc. query)
 *
 * @author andy
 */
public class SocialQueryMessage extends ContinuationMessage {
    
public static final short TYPE = 11;

  // the id to fetch (The destination node)
  private Id id;

  // whether or not this message has been cached
  private boolean cached = false;

  // the list of nodes where this message has been
  private NodeHandle handle;
  
  // SocialQueryPDU holds the query terms and maybe a set of user ids
  private SocialQueryPDU socialQueryPDU;
  
  /**
   * Constructor
   *
   * @param uid The unique id
   * @param id The location to be stored
   * @param source The source address
   * @param dest The destination address
   */
  public SocialQueryMessage(int uid, Id id, NodeHandle source, Id dest, SocialQueryPDU qpdu) {
    super(uid, source, dest);

    this.id = id;
    this.socialQueryPDU = qpdu;
  }

  /**
   * Method which returns the id
   *
   * @return The contained id
   */
  public Id getId() {
    return id;
  }

  /**
   * Returns whether or not this message has been cached
   *
   * @return Whether or not this message has been cached
   */
  public boolean isCached() {
    return cached;
  }

  /**
   * Sets this message as having been cached.
   */
  public void setCached() {
    cached = true;
  }

  /**
   * Method which is designed to be overridden by subclasses if they need
   * to keep track of where they've been.
   *
   * @param handle The current local handle
   */
  public void addHop(NodeHandle handle) {
    this.handle = handle;
  }

  /**
   * Method which returns the previous hop (where the message was just at)
   *
   * @return The previous hop
   */
  public NodeHandle getPreviousNodeHandle() {
    return handle;
  }
  
  /**
   * Getter for PDU
   * 
   * @return
   */
  public SocialQueryPDU getQueryPDU(){
      return socialQueryPDU;
  }

  /**
    * Returns a string representation of this message
   *
   * @return A string representing this message
   */
  public String toString() {
    return "[SocialSocialQueryMessage for " + id + " data " + response + "]";
  }
  
  /***************** Raw Serialization ***************************************/
  public short getType() {
    return TYPE;
  }
  
  public void serialize(OutputBuffer buf) throws IOException {
    buf.writeByte((byte)0); // version        
    if (response != null && response instanceof RawPastContent) {
      super.serialize(buf, false); 
      RawPastContent rpc = (RawPastContent)response;
      buf.writeShort(rpc.getType());
      rpc.serialize(buf);
    } else {
      super.serialize(buf, true);       
    }
    
    buf.writeBoolean(handle != null);
    if (handle != null) handle.serialize(buf);
    
    buf.writeShort(id.getType());
    id.serialize(buf);      
    buf.writeBoolean(cached);
    
    // Java serialization is used for the serialization of the SocialQueryPDU
    // TODO: optimization
    JavaSerializer.serialize(buf, socialQueryPDU);
  }

  public static SocialQueryMessage build(InputBuffer buf, Endpoint endpoint, PastContentDeserializer pcd) throws IOException {
    byte version = buf.readByte();
    switch(version) {
      case 0:
        return new SocialQueryMessage(buf, endpoint, pcd);
      default:
        throw new IOException("Unknown Version: "+version);        
    }
  }  
  
  private SocialQueryMessage(InputBuffer buf, Endpoint endpoint, PastContentDeserializer pcd) throws IOException {
    super(buf, endpoint);
    if (serType == S_SUB) {
      short contentType = buf.readShort();
      response = pcd.deserializePastContent(buf, endpoint, contentType);
    }
    if (buf.readBoolean())handle = endpoint.readNodeHandle(buf); 
    try {
      id = endpoint.readId(buf, buf.readShort());
    } catch (IllegalArgumentException iae) {
      System.out.println(iae+" "+this+" serType:"+serType+" UID:"+getUID()+" d:"+dest+" s:"+source);
      throw iae; 
    }
    cached = buf.readBoolean();
    
    // Java deserialization
    // TODO: optimization)
    socialQueryPDU = (SocialQueryPDU) JavaSerializer.deserialize(buf, endpoint);
  }
}
