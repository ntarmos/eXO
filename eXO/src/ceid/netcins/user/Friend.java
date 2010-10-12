/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.user;

import java.net.InetAddress;
import rice.p2p.commonapi.Id;

/**
 * This class contains the necessary data of an entry in the friends list.
 * Important parts of such an entry are the UID, the IP address and the 
 * screen name of the user's friend. IP address may be null indicating that
 * we are in simulation mode.
 *
 * @author andy
 */
public class Friend {
    
    // User unique identifier created by SHA-1 hash function
    private Id uid;
    
    // IP address of the friend
    private InetAddress ip = null;
    
    // The screen name of the friend. This is 
    private String screenName = "n/a";
    
    
    public Friend(Id uid, String screenName){
        this(uid,screenName,null);
    }
    
    public Friend(Id uid, String screenName, InetAddress ip){
        this.uid = uid;
        this.ip = ip;
        this.screenName = screenName;
    }

    public void setName(String name){
        screenName = name;
    }
    
    public void setIp(InetAddress ip){
        this.ip = ip;
    }
    
    public Id getUID(){
        return uid;
    }
    
    public String getName(){
        return screenName;
    }
    
    public InetAddress getIp(){
        return ip;
    }
    
      /**
   * Used to compare two friends.
   * Two friends are the same if:
   * they have the same UID.
   *
   * @param o The other friend to compare
   * @return True if are the same friend.
   */
   public boolean equals(Object o) {
      if (!(o instanceof Friend)) {
        return false;
      }
      Friend f = (Friend) o;
      
      return f.getUID().equals(uid); 
   }
   
   public String toString(){
       StringBuffer buffer = new StringBuffer();
       buffer.append("\n - Friend UID : "+this.uid);
       if(screenName!=null)
           buffer.append("\n - Friend's Screen Name : "+this.screenName);
       else
           buffer.append("\n - Friend's Screen Name : n/a");
       if(ip!=null)
           buffer.append("\n - Friend's IP Address :"+this.ip);
       else
           buffer.append("\n - Friend's IP Address : n/a\n");
       
       return buffer.toString();
   }
}
