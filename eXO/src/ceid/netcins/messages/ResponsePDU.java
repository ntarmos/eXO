/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.messages;

import java.io.Serializable;

import ceid.netcins.catalog.Catalog;

/**
 * This class is used to provide a container for the data that is returned by
 * the Catalog nodes or User nodes generally.
 * 
 * @author andy
 */
public class ResponsePDU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7913630514597439259L;

	// This var holds the hops or messages for the whole roundtrip of the
	// request-response
	private int messagesCounter;

	// This holds the entries returned
	private Catalog cat;

	public ResponsePDU(int messagesCounter) {
		this.messagesCounter = messagesCounter;
	}

	public ResponsePDU(int messagesCounter, Catalog cat) {
		this.messagesCounter = messagesCounter;
		this.cat = cat;
	}

	public void setMessagesCounter(int messagesCounter) {
		this.messagesCounter = messagesCounter;
	}

	public void setCatalog(Catalog cat) {
		this.cat = cat;
	}

	public int getMessagesCounter() {
		return messagesCounter;
	}

	public Catalog getCatalog() {
		return cat;
	}
}
