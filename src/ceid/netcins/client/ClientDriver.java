package ceid.netcins.client;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.IPNodeIdFactory;
import rice.persistence.LRUCache;
import rice.persistence.MemoryStorage;
import rice.persistence.PersistentStorage;
import rice.persistence.Storage;
import rice.persistence.StorageManager;
import rice.persistence.StorageManagerImpl;
import ceid.netcins.CatalogService;
import ceid.netcins.FriendsRequest;
import ceid.netcins.IndexContentRequest;
import ceid.netcins.IndexUserRequest;
import ceid.netcins.Request;
import ceid.netcins.SearchContentRequest;
import ceid.netcins.SearchUserRequest;
import ceid.netcins.StatsRequest;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.messages.FriendReqPDU;
import ceid.netcins.messages.QueryPDU;
import ceid.netcins.user.Friend;
import ceid.netcins.user.FriendRequest;
import ceid.netcins.user.User;

/**
 * ClientDriver is a driver class between the real frontend and the
 * decentrelized social network services. It contains the methods which resolve
 * the high level user requests. Theser operations are handled by the
 * REQUESTDISPATCHER Thread.
 * 
 * @author Andreas Loupasakis
 */
public class ClientDriver {

	// RequestDispatcher thread reference
	private Thread requestDispatcher;

	// Our Scorer thread functionality (SINGLE THREAD)
	// TODO : Think and implement a multiple scorer threading scenario
	// private Scorer scorer;
	// private Thread scorerThread;

	// Signals the end of init()
	boolean initializationStateOK = false;

	// Variable to avoid missed signals and spurious wakeups
	boolean wasSignalled = false;

	/**
	 * Flags which control the "RequestDispatcher" thread
	 */
	protected boolean main_running = true;

	/**
	 * The synchronized queue of Requests
	 */
	public Vector<Request> execRequests;

	/**
	 * the environment
	 */
	protected Environment environment;

	/**
	 * the storage services in the ring
	 */
	protected StorageManager storage;

	/**
	 * the impl instance
	 */
	protected CatalogService past;

	// TODO : Store the properties somewhere globally like:
	/****************
	 * Global Properties **************
	 * 
	 * Windows --> Registry Linux --> ~/.java/....
	 *************************************************/

	/****************
	 * Default Values ***************** /** the port of this Application
	 */
	public int BINDPORT = 1821;

	/**
	 * the boot IP address
	 */
	public static final String BOOTIP = "150.140.143.226";

	/**
	 * the boot port
	 */
	public static final int BOOTPORT = 1821;

	/**
	 * the path to downloading folder
	 */
	public static final String DOWNLOADINGPATH = "downloading";

	/**
	 * the path to completed downloadings
	 */
	public static final String COMPLETEDPATH = "completed";

	/**************************************************/

	/**
	 * The name of the registered Application over FP
	 */
	public static String INSTANCE = "CatalogService";

	/**
	 * the replication factor in Past (K)
	 */
	public static int REPLICATION_FACTOR = 3;

	public ClientDriver(Environment env) {

		this.environment = env;
	}

	ClientDriver(Environment env, int bindPort) {
		this.environment = env;
		this.BINDPORT = bindPort;
	}

	/**
	 * Method which starts the connection of this node and the initialization.
	 */
	public void start() {

		System.out.println("\nConnection Beginning ...\n");
		try {
			init();
		} catch (Exception e) {
			System.out.println(e + " Occured during node initialization");
			e.printStackTrace();
		}

		System.out.println("\nRequest Resolving Beginning ...\n");
		runMainLoop();
	}

	private void init() throws Exception {

		InetAddress localAddress = InetAddress.getLocalHost();
		if (localAddress.isLoopbackAddress()) {
			// try the internet to determine the real IP Address
			Socket temp = new Socket(environment.getParameters().getString(
					"pastry_socket_known_network_address"), environment
					.getParameters().getInt(
							"pastry_socket_known_network_address_port"));
			if (temp.getLocalAddress().equals(localAddress))
				throw new IllegalStateException(
						"No connection to the Internet: " + localAddress);
			localAddress = temp.getLocalAddress();
			temp.close();
		}

		// Generate the NodeIds Randomly RandomNodeIdFactory, IPNodeIdFactory
		// (alternative)
		NodeIdFactory nidFactory = new IPNodeIdFactory(localAddress, BINDPORT,
				environment);

		// used for generating PastContent object Ids.
		// this implements the "hash function" for our DHT
		PastryIdFactory idf = new rice.pastry.commonapi.PastryIdFactory(
				environment);

		// construct the PastryNodeFactory, this is how we use
		// rice.pastry.socket
		PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory,
				BINDPORT, environment);

		// Bootaddress creation
		InetAddress bootaddr = InetAddress.getByName(BOOTIP);
		InetSocketAddress bootaddress = new InetSocketAddress(bootaddr,
				BOOTPORT);

		// This will return null if we there is no node at that location
		NodeHandle bootHandle = ((SocketPastryNodeFactory) factory)
				.getNodeHandle(bootaddress);

		// construct a node, passing the null boothandle on the first loop will
		// cause the node to start its own ring
		PastryNode node = factory.newNode();
		node.boot((rice.pastry.NodeHandle) bootHandle);

		// the node may require sending several messages to fully boot into the
		// ring
		while (!node.isReady()) {
			// delay so we don't busy-wait
			Thread.sleep(100);
		}
		System.out.println("Finished creating new node " + node);
		Storage stor = new PersistentStorage(idf, "SoNATA_MetadataFile_"
				+ this.BINDPORT, ".", -1, node.getEnvironment());
		storage = new StorageManagerImpl(idf, stor, new LRUCache(
				new MemoryStorage(idf), 512 * 1024, node.getEnvironment()));

		// Queue for Requests
		execRequests = new Vector<Request>();

		// TOO : Fix the User UIDs registration!!!!
		past = new CatalogService(node, storage, REPLICATION_FACTOR, INSTANCE,
				new User(node.getNodeId()));

		// Thread.sleep(5000);
	}

	/**
	 * Method which should run the main loop -
	 * 
	 */
	protected void runMainLoop() {

		while (main_running) {
			try {
				initializationStateOK = true;
				while (!execRequests.isEmpty()) {
					serveRequest(execRequests.remove(0));
				}
				// Wait until the FrontendThread notify!
				while (!wasSignalled) {
					synchronized (this) {
						try {
							wait();
						} catch (InterruptedException ex) {
							System.out.println("RequestDispatcher woke up!");
						}
					}
				}
				// clear signal and continue running.
				wasSignalled = false;
			} catch (Exception e) {
				System.out.println("Error : " + e.getMessage());
			}
		}
	}

	/**
	 * Private method which initiates the replica maintenance on all of the
	 * nodes
	 */
	@SuppressWarnings("unused")
	private void runReplicaMaintence() {
		past.getReplication().replicate();
	}

	/**
	 * Terminates the contstructor Thread and destroys the Selector ...
	 */
	protected void cleanUp() throws Exception {

		main_running = false;
		environment.destroy();
	}

	/**
	 * Getter for the environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	public Thread getRequestDispatcher() {
		return requestDispatcher;
	}

	/**
	 * The RequestDispatcher thread is responsible to resolve the requests from
	 * the execRequest queue using this method! For every type of user request
	 * the appropriate functionality is implemented by the Selector thread with
	 * asynchronous calls.
	 * 
	 * @param req
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("unchecked")
	protected void serveRequest(Request req) throws Exception {

		if (req instanceof IndexContentRequest) {

			final IndexContentRequest ireq = (IndexContentRequest) req;
			if (ireq.getFilePath() != null) {

				past.indexContent(new File(ireq.getFilePath()),
						new Continuation() {

							public void receiveResult(Object result) {

								System.out.println("File : "
										+ ireq.getFilePath()
										+ ", indexed successfully");
								// TODO : Check the replicas if are updated correctly!
								// run replica maintenance
								// runReplicaMaintence();
								if (result instanceof Boolean[]) {
									Boolean[] results = (Boolean[]) result;
									int indexedNum = 0;
									for (Boolean isIndexedTerm : results) {
										if (isIndexedTerm)
											indexedNum++;
									}
									System.out.println("Total " + indexedNum
											+ " terms indexed out of "
											+ results.length + "!");

									// Debugging only!!!
									if (ireq.t != null) {
										String msg = "Total " + indexedNum
												+ " terms indexed out of "
												+ results.length + "!";
										try {
											ireq.t.sendResponseHeaders(200, msg
													.getBytes().length);
											OutputStream os = ireq.t
													.getResponseBody();
											os.write(msg.getBytes());
											os.close();
										} catch (IOException ex) {
											java.util.logging.Logger.getLogger(
													ClientDriver.class
															.getName()).log(
													Level.SEVERE, null, ex);
										}
									}

								}
							}

							public void receiveException(Exception result) {
								System.out.println("File : "
										+ ireq.getFilePath()
										+ ", indexed with errors : "
										+ result.getMessage());
							}
						});
			}

		} else if (req instanceof IndexUserRequest) {
			final IndexUserRequest ireq = (IndexUserRequest) req;
			if (ireq.getUserProfileMap() != null) {

				past.createUserProfile(ireq.getUserProfileMap());
				past.indexUser(new Continuation() {

					public void receiveResult(Object result) {

						System.out.println("User : " + past.getUser().getUID()
								+ ", indexed successfully");
						// TODO : Check the replicas if are updated correctly!
						// run replica maintenance
						// runReplicaMaintence();
						if (result instanceof Boolean[]) {
							Boolean[] results = (Boolean[]) result;
							int indexedNum = 0;
							for (Boolean isIndexedTerm : results) {
								if (isIndexedTerm)
									indexedNum++;
							}
							System.out.println("Total " + indexedNum
									+ " terms indexed out of " + results.length
									+ "!");
						}
					}

					public void receiveException(Exception result) {
						System.out.println("User : " + past.getUser().getUID()
								+ ", indexed with errors : "
								+ result.getMessage());
					}
				});
			}

		} else if (req instanceof SearchContentRequest) {

			// TODO : Rewrite a better version!
			final SearchContentRequest screq = (SearchContentRequest) req;
			String query = screq.getQuery();
			if (query != null) { // Handle the query appropriately

				past.searchQuery(QueryPDU.CONTENT_ENHANCEDQUERY, query, screq
						.getK(), new Continuation() {

					public void receiveResult(Object result) {
						System.out.println("Content Query : "
								+ screq.getQuery()
								+ ", Top Results returned successfully");
						// debugging only
						// if(result instanceof Catalog){
						// System.out.println("Catalog : "+(Catalog)result);
						// }else
						// System.out.println("Result : "+result);

						// Debugging only!!!
						if (screq.t != null) {
							String msg = "Content Query : " + screq.getQuery()
									+ ", Top Results returned successfully";
							try {
								screq.t.sendResponseHeaders(200,
										msg.getBytes().length);
								OutputStream os = screq.t.getResponseBody();
								os.write(msg.getBytes());
								os.close();
							} catch (IOException ex) {
								java.util.logging.Logger.getLogger(
										ClientDriver.class.getName()).log(
										Level.SEVERE, null, ex);
							}
						}
					}

					public void receiveException(Exception result) {
						System.out.println("Content Query : "
								+ screq.getQuery() + ", result (error) "
								+ result.getMessage());
					}

				});
			}
		} else if (req instanceof SearchUserRequest) {

			// TODO : Rewrite a better version!
			final SearchUserRequest sureq = (SearchUserRequest) req;
			String query = sureq.getQuery();
			if (query != null) { // Handle the query appropriately

				past.searchQuery(QueryPDU.USERQUERY, query, sureq.getK(),
						new Continuation() {

							public void receiveResult(Object result) {
								System.out
										.println("User Query : "
												+ sureq.getQuery()
												+ ", Top Results returned successfully");
								// debugging only
								// if(result instanceof Catalog){
								// System.out.println("Catalog : "+(Catalog)result);
								// }else
								// System.out.println("Result : "+result);
							}

							public void receiveException(Exception result) {
								System.out.println("User Query : "
										+ sureq.getQuery()
										+ ", result (error) "
										+ result.getMessage());
							}

						});
			}

		} else if (req instanceof FriendsRequest) {

			final FriendsRequest freq = (FriendsRequest) req;
			String message = freq.getMessage();
			if (message == null) { // If empty put the default greeting message
				message = "Greetings! Do you want us to be friends?";
			}

			// TODO : Implement this just like in Simulation but find a way to
			// compute destUID!

		} else if (req instanceof StatsRequest) {

			final StatsRequest streq = (StatsRequest) req;

			// PENDING, SHARED OR PROFILES
			int type = streq.getType();
			System.out.println("\n\n >>> Node Stats Request");
			if (type == StatsRequest.PENDING) {
				System.out
						.println("\n*********** Pending Friend Requests ***********\n");
				Iterator<FriendRequest> it = past.getUser().getPendingFReq()
						.iterator();
				FriendReqPDU frpdu = null;
				FriendRequest fr = null;
				while (it.hasNext()) {
					fr = it.next();
					System.out.println("\n - UID : " + fr.getUID());
					frpdu = fr.getFriendReqPDU();
					if (frpdu != null) {
						if (frpdu.getScreenName() != null)
							System.out.println("\n - Screen Name : "
									+ fr.getUID());
						else
							System.out.println("\n - Screen Name : n/a");
						if (frpdu.getMessage() != null)
							System.out.println("\n - Greetings Message : "
									+ frpdu.getMessage());
						else
							System.out
									.println("\n - Greetings Message : No message");
					}
				}
				System.out
						.println("\n\n*********** Pending Friend Approvals ***********\n");
				Iterator<Id> it2 = past.getUser().getPendingFAppr().iterator();
				while (it2.hasNext()) {
					System.out.println("\n - UID : " + it2.next());
				}
				System.out
						.println("\n**************************************************\n");
			} else if (type == StatsRequest.SHARED) {
				System.out
						.println("\n\n*********** User Shared Content ***********\n");
				Map<Id, File> sc = past.getUser().getSharedContent();
				Iterator<Id> it = sc.keySet().iterator();
				int i = 0;
				while (it.hasNext()) {
					System.out.println("\n - " + (++i) + ". File : "
							+ sc.get(it.next()).toString());
				}
				System.out
						.println("\n*******************************************\n");
			} else if (type == StatsRequest.PROFILES) {
				System.out.println("\n\n*********** User Profile***********\n");
				ContentProfile cp = past.getUser().getUserProfile();
				if (cp != null) {
					System.out.println(cp.toStringWithoutTF());
				}
				System.out
						.println("\n\n*********** User Friends ***********\n");
				Iterator<Friend> it = past.getUser().getFriends().iterator();
				while (it.hasNext()) {
					System.out.println(it.next());
				}
				System.out
						.println("\n**************************************************\n");
			}
		}

	}

}
