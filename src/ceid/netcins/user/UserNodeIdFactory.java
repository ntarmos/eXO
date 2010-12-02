package ceid.netcins.user;

import rice.pastry.Id;
import rice.pastry.NodeIdFactory;

/**
 * Node ID factory, to be used in place of {@link NodeIdFactory} and friends.
 * 
 * <br/><br/>
 * Ideally each user should be allowed to select an ID and
 * be able to run instances of our software on multiple nodes
 * at the same time. Moreover, we need the actual node ID to
 * be equal to the current user's ID. In order to accomplish both
 * of these, we need the user's ID to depend on both the selected
 * username and some node-specific information. Thus, the actual
 * user/node ID is computed using the concatenation of the username
 * and some node-specific information (e.g. a node ID as computed
 * by {@link IPNodeIdFactory} or {@link RandomNodeIdFactory}).
 *
 * @author Nikos Ntarmos &lt;<a href="mailto:ntarmos@cs.uoi.gr">ntarmos@cs.uoi.gr</a>&gt;
 */
public class UserNodeIdFactory implements NodeIdFactory {

	private NodeIdFactory nif;

	private String username;

	/**
	 * Construct a new UserNodeIdFactory
	 * 
	 * @param username the selected username
	 * @param nif the NodeIDFactory implementation of choice (e.g., {@link IPNodeIdFactory}, {@link RandomNodeIdFactory} etc.)
	 */
	public UserNodeIdFactory(String username, NodeIdFactory nif) {
		this.username = username;
		this.nif = nif;
	}

	/* (non-Javadoc)
	 * @see rice.pastry.NodeIdFactory#generateNodeId()
	 */
	@Override
	/**
	 * Computes a user/node id, given a username and node ID factory.
	 * 
	 * @return a new ID
	 */
	public Id generateNodeId() {
		byte ubytes[] = username.getBytes();
		byte nbytes[] = nif.generateNodeId().toByteArray();
		byte idbytes[] = new byte[ubytes.length + nbytes.length];
		for (int i = 0; i < ubytes.length; i++)
			idbytes[i] = ubytes[i];
		for (int i = 0; i < nbytes.length; i++)
			idbytes[ubytes.length + i] = nbytes[i];
		return rice.pastry.Id.build(idbytes);
	}
}
