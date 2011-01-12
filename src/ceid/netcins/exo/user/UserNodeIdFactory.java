package ceid.netcins.exo.user;

import rice.environment.Environment;
import rice.p2p.commonapi.IdFactory;
import rice.pastry.Id;
import rice.pastry.NodeIdFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.standard.IPNodeIdFactory;
import rice.pastry.standard.RandomNodeIdFactory;

/**
 * Node ID factory, to be used in place of {@link NodeIdFactory} and friends.
 * 
 * <br/><br/>
 * Ideally each user should be allowed to select an ID and
 * be able to run instances of our software on multiple nodes
 * at the same time. Moreover, we need the actual node ID to
 * be equal to the current user's ID. This would make mapping users to
 * nodes somewhat strange (having users handle 128-bit hashes). Instead
 * we choose a Jabber/XMPP-style naming (i.e.
 * &lt;username&gt;/&lt;resource&gt;) where users may run multiple
 * nodes, each with a different resource identifier. Thus, the actual
 * user/node ID is computed using the concatenation of the username and
 * resource strings information, fed through FreePastry's ID generator,
 * resulting in a a node ID (e.g., as computed by {@link
 * IPNodeIdFactory} or {@link RandomNodeIdFactory}).
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 */
public class UserNodeIdFactory implements NodeIdFactory {
	private String username;
	private String resource;
	private Environment env;

	/**
	 * Construct a new UserNodeIdFactory
	 * 
	 * @param username the selected username
	 * @param resource a Jabber-style resource descriptor (e.g., "Home", "Work", etc.)
	 */
	public UserNodeIdFactory(String username, String resource, Environment env) {
		this.username = username;
		this.resource = resource;
		this.env = env;
	}

	/* (non-Javadoc)
	 * @see rice.pastry.NodeIdFactory#generateNodeId()
	 */
	@Override
	/**
	 * Computes a user/node id, given a username and resource.
	 * 
	 * @return a new ID
	 */
	public Id generateNodeId() {
		return generateNodeId(username, resource, env);
	}

	public static Id generateNodeId(String username, String resource, Environment env) {
		if (username == null || username.contains(User.ScreennameDelimiter) ||
			resource == null || resource.contains(User.ScreennameDelimiter))
			throw new RuntimeException(
					"Username and resource name cannot be null or contain '" +
					User.ScreennameDelimiter + "'");
		StringBuffer buf = new StringBuffer();
		buf.append(username);
		buf.append(User.ScreennameDelimiter);
		buf.append(resource);
		IdFactory factory = new PastryIdFactory(env);
		return (Id)factory.buildId(buf.toString());
	}
}
