/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.user;

import rice.p2p.commonapi.Id;
import rice.pastry.commonapi.PastryIdFactory;

/**
 * This is utility class to create a user entity in the network
 * 
 * @author andy
 */
public class UserFactory {

	/**
	 * Static method which is used mainly to create the uid
	 * 
	 * @param credential
	 * @param username
	 * @param factory
	 * @return a new User entity
	 */
	public static User createUser(String credential, String username,
			PastryIdFactory factory) {

		// TODO : Check the existance of the specific UID!!!
		final Id uid = factory.buildId(credential);
		return new User(uid, username);
	}

}
