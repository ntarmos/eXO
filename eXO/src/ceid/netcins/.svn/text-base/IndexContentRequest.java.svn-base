/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins;

import ceid.netcins.content.ContentProfile;
import com.sun.net.httpserver.HttpExchange;

/**
 * Request for content indexing.
 * 
 * @author andy
 */
public class IndexContentRequest extends Request{
   
    // The path (relative or absolute) to the indexing file
    private String filePath;
    
    // The source user profile to store in Catalog with this content
    //private ContentProfile userProfile;
    
    // The source node number (Simulator only)
    private int source;
    public static final int RANDOMSOURCE = -1;
            
    public IndexContentRequest(String filePath){
        this(filePath, RANDOMSOURCE);
    }
    
    public IndexContentRequest(String filePath, int source){  
        super();
        this.filePath = filePath;
        this.source = source;
    }
    
    public IndexContentRequest(String filePath, HttpExchange t){
        super();
        this.filePath = filePath;
        this.t = t;
    }
    
    public HttpExchange t;
    
    /**
     * Getter for filePath
     * 
     * @return
     */
    public String getFilePath(){
        return this.filePath;
    }
    
    /**
     * Getter for source number
     * 
     * @return
     */
    public int getSource(){
        return this.source;
    }
}
