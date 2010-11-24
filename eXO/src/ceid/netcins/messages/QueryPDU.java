/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.messages;

import java.io.Serializable;

import ceid.netcins.content.ContentProfile;

/**
 * Holds the query terms to be searched
 * 
 * @author andy
 */
public class QueryPDU implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4695549942897761938L;
	// All the ENHANCED QUERIES use the similarity of source user's profile
    private int type = CONTENTQUERY; //default
    public static final int CONTENTQUERY = 0;
    public static final int CONTENT_ENHANCEDQUERY =1;
    public static final int USERQUERY = 2;
    public static final int USER_ENHANCEDQUERY =3;
    public static final int HYBRIDQUERY = 4;
    public static final int HYBRID_ENHANCEDQUERY = 5;
    public static final int URLQUERY = 6;
    
    // The number of results to be returned
    private int k;
    public static final int RETURN_ALL = -1;

    // The packet data (query terms)
    private String[] data; 
    
    // Source user's profile.
    private ContentProfile userProfile;
    
    public QueryPDU(String[] data){
        this(data,CONTENTQUERY);
    }
    
    public QueryPDU(String[] data, int type){
        this(data,type,RETURN_ALL);
    }
    
    public QueryPDU(String[] data, int type, int k){
        this(data,type,k,null);
    }
    
    public QueryPDU(String[] data, int type, ContentProfile userProfile){
        this(data,type,RETURN_ALL,userProfile);
    }
    
    public QueryPDU(String[] data, int type, int k, ContentProfile userProfile){
        this.data = data;
        this.type = type;
        this.k = k;
        this.userProfile = userProfile;
    }
    
    public String[] getData(){
        return data;
    }
    
    public int getType(){
        return type;
    }
    
    public int getK(){
        return k;
    }
    
    public ContentProfile getSourceUserProfile(){
        return userProfile;
    }
}
