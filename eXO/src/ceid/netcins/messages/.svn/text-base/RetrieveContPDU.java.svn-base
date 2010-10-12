/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.messages;

import java.io.Serializable;
import rice.p2p.commonapi.Id;

/**
 * Holds the File Checksum
 * (the File identifier).
 *
 * @author andy
 */
public class RetrieveContPDU implements Serializable{
    
    // The user Id
    //private Id uid; 
    
    // The checksum
    private Id checksum;
    
    // Flag for Tagclouds
    // TODO : Implement it in the requests
    private boolean cloudflag;
    
    public RetrieveContPDU(Id checksum){
        //this.uid  = uid;
        this.checksum = checksum;
        this.cloudflag = true;
    }
    
//    public Id getUID(){
//        return uid;
//    }
    
    public Id getCheckSum(){
        return checksum;
    }
    
    public boolean getCloudFlag(){
        return cloudflag;
    }
}
