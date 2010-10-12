/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.utils;

import rice.environment.Environment;
import rice.environment.logging.Logger;
import rice.environment.random.RandomSource;
import rice.pastry.*;

import java.security.*;

/**
 * This class contains methods to generate a unique identifier (nodeID)
 * for a node in the pastry overlay network. The nodeId is also the so
 * called "UID". In other words represents both the network address and
 * the user unique identifier. Thus, it can be used to lookup and search
 * queries.
 * TODO : uniqueness should be guaranteed through a centralized mechanism!
 * 
 * @author andy
 */
public class UserNodeIdFactory  implements NodeIdFactory {

  private String unique;  
  protected Logger logger;
  
  /**
   * Constructor.
   */

  public UserNodeIdFactory(Environment env, String unique) {
    this.logger = env.getLogManager().getLogger(getClass(), null);
    this.unique = unique;
  }
  
  public void setUnique(String unique){
      this.unique = unique;
  }

  /**
   * generate a nodeId
   * 
   * @return the new nodeId
   */

  public Id generateNodeId() {

    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException e) {
      if (logger.level <= Logger.SEVERE) logger.log(
          "No SHA support!");
      throw new RuntimeException("No SHA support!",e);
    }

    // TODO : Caution!! Check : different Charsets may conclude to the same uid
    md.update(unique.getBytes());
    byte[] digest = md.digest();

    Id nodeId = Id.build(digest);

    return nodeId;
  }
  
    /**
   * generate a nodeId
   * 
   * @return the new nodeId
   */

  public static Id generateNodeId(String unique) {

    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("No SHA support!",e);
    }

    // TODO : Caution!! Check : different Charsets may conclude to the same uid
    md.update(unique.getBytes());
    byte[] digest = md.digest();

    Id nodeId = Id.build(digest);

    return nodeId;
  }

}
