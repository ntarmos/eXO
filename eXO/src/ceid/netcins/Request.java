/*
 * Request.java
 *
 * Created on 7 ����� 2006, 8:51 ��
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ceid.netcins;

/**
 * Requests are sent by the Frontend thread to RequestDispatcher
 * and they are executed asynchronously by the Selector thread.
 *
 * @author andy
 */
public abstract class Request {
    
    // Holds the reply code number
    private int reply = -1;
    
    // RETURN/REPLY VALUES 
    public static final int RET_NOT_YET = -1;
    public static final int RET_SUCCESS = 0;
    public static final int RET_ERROR1 = 1;
    public static final int RET_ERROR2 = 2;
    
    // Holds the reply message of the request
    private String replyMsg;
    
    /** Creates a new instance of Request 
     * 
     */
    public Request() {
    }
    
    /**
     * After a request has been executed usually the Selector thread of FP
     * will set the reply code number and message of the Request
     * 
     * @param reply the new code number of response
     */
    public void setReply(int reply){
        this.reply = reply;
    }
    
    /**
     * Getter for the reply code
     * 
     * @return
     */
    public int getReply(){
        return reply;
    }
    
    /**
     * Set the reply message (Selector thread)
     * 
     * @param replyMsg
     */
    public void setReplyMsg(String replyMsg){
        this.replyMsg = replyMsg;
    }
    
    /**
     * Getter for the reply message
     * 
     * @return
     */
    public String getReplyMsg(){
        return replyMsg;
    }
}
