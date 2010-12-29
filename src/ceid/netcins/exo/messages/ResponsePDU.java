

package ceid.netcins.exo.messages;

import java.io.Serializable;

import ceid.netcins.exo.catalog.ScoreBoard;

/**
 * This class is used to provide a container for the data that is returned by
 * the Catalog nodes or User nodes generally.
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public class ResponsePDU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7913630514597439259L;

	// This var holds the hops or messages for the whole roundtrip of the
	// request-response
	private int messagesCounter;

	// This holds the returned entries
	private ScoreBoard scoreBoard;

	public ResponsePDU(int messagesCounter) {
		this.messagesCounter = messagesCounter;
	}

	public ResponsePDU(int messagesCounter, ScoreBoard scoreBoard) {
		this.messagesCounter = messagesCounter;
		this.scoreBoard = scoreBoard;
	}

	public void setMessagesCounter(int messagesCounter) {
		this.messagesCounter = messagesCounter;
	}

	public void setScoreBoard(ScoreBoard scoreBoard) {
		this.scoreBoard = scoreBoard;
	}

	public int getMessagesCounter() {
		return messagesCounter;
	}

	public ScoreBoard getScoreBoard() {
		return scoreBoard;
	}
}
