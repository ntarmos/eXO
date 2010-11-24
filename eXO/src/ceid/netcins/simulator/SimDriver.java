/*
 * SimDriver.java
 *
 * Created on April 19, 2008, 6:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ceid.netcins.simulator;

/**
 *
 * @author andy
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import rice.Continuation;
import rice.environment.Environment;
import rice.environment.logging.Logger;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.testing.CommonAPITest;
import rice.pastry.PastryNode;
import rice.persistence.LRUCache;
import rice.persistence.MemoryStorage;
import rice.persistence.PersistentStorage;
import rice.persistence.StorageManager;
import rice.persistence.StorageManagerImpl;
import ceid.netcins.CatalogService;
import ceid.netcins.FriendsRequest;
import ceid.netcins.IndexContentRequest;
import ceid.netcins.IndexPseydoContentRequest;
import ceid.netcins.IndexURLRequest;
import ceid.netcins.IndexUserRequest;
import ceid.netcins.RandomQueriesRequest;
import ceid.netcins.Request;
import ceid.netcins.RetrieveContRequest;
import ceid.netcins.ScenarioRequest;
import ceid.netcins.SearchContentRequest;
import ceid.netcins.SearchSocialTagsRequest;
import ceid.netcins.SearchURLRequest;
import ceid.netcins.SearchUserRequest;
import ceid.netcins.StatsRequest;
import ceid.netcins.TagContentRequest;
import ceid.netcins.catalog.ScoreCatalog;
import ceid.netcins.catalog.SocialCatalog;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.messages.FriendReqPDU;
import ceid.netcins.messages.QueryPDU;
import ceid.netcins.messages.ResponsePDU;
import ceid.netcins.similarity.Scorer;
import ceid.netcins.social.SocialBookMark;
import ceid.netcins.social.TagCloud;
import ceid.netcins.social.URLBookMark;
import ceid.netcins.user.Friend;
import ceid.netcins.user.FriendRequest;
import ceid.netcins.user.User;
import ceid.netcins.utils.UserNodeIdFactory;

public class SimDriver extends CommonAPITest {

	// Variable to avoid missed signals and spurious wakeups
	boolean wasSignalled = false;

	// the storage services in the ring
	/**
	 * DESCRIBE THE FIELD
	 */
	protected StorageManager storages[];

	/**
	 * Our application instances over past
	 */
	protected CatalogService pasts[];

	/**
	 * Flags which control the "RequestDispatcher" thread
	 */
	protected boolean main_running = true;

	/**
	 * The synchronized queue of Requests
	 */
	public Vector<Request> execRequests;

	/**
	 * the name of our application
	 */
	public static String INSTANCE = "CatalogService";

	/**
	 * the replication factor of Past objects
	 */
	public static int REPLICATION_FACTOR = 3;

	// Our Scorer thread functionality (SINGLE THREAD FOR ALL THE SIMULATION)
	private Scorer scorer;
	private Thread scorerThread;

	// The Results reference
	public Results results;

	// This reference is pointing to current Scenario object
	public Scenario currentScenario;

	// Array with the pending scenario requests to be done
	public Vector<ScenarioRequest> pendingScenarios;

	public SimDriver(Environment env) throws IOException {

		super(env);
		pasts = new CatalogService[NUM_NODES];
		storages = new StorageManager[NUM_NODES];
		execRequests = new Vector<Request>();
		results = new Results();
		results.nodes = NUM_NODES;
		pendingScenarios = new Vector<ScenarioRequest>();

	}

	/**
	 * Overrided Method which creates the nodes
	 */
	public void createNodes() {

		if (simulator != null)
			simulator.setMaxSpeed(10.0f);
		for (int i = 0; i < NUM_NODES; i++) {
			nodes[i] = createNode(i);

			// simulate();
			processNode(i, nodes[i]);
			// simulate();

			System.out.println("Created and initialized node " + i
					+ " with id " + nodes[i] + " at "
					+ environment.getTimeSource().currentTimeMillis());
		}
		if (logger.level <= Logger.INFO)
			logger.log(((PastryNode) nodes[0]).getLeafSet().toString());
		if (simulator != null)
			simulator.setFullSpeed();

		// createFiles();
	}

	/**
	 * Method which creates a single node, given it's node number (Overriden)
	 * 
	 * @param num
	 *            The number of creation order
	 * @return The created node
	 */
	@Override
	protected Node createNode(int num) {
		PastryNode ret = null;
		if (num == 0) {
			try {
				ret = factory.newNode(UserNodeIdFactory.generateNodeId("user" + num));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ret.boot((rice.pastry.NodeHandle) null);
		} else {
			try {
				ret = factory.newNode(UserNodeIdFactory
						.generateNodeId("user" + num));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ret.boot(getBootstrap());
		}
		synchronized (ret) {
			while (!ret.isReady()) {
				try {
					ret.wait(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
					return null;
				}
				if (!ret.isReady()) {
					if (logger.level <= Logger.INFO)
						logger.log("Node " + ret + " is not yet ready.");
				}
			}
		}

		return ret;
	}

	/**
	 * Overrided Method which starts the creation of nodes. This function also
	 * instantiates and run the Scorer Thread.
	 */
	public void start() {
		// simulator.start();
		beginScorer();

		createNodes();

		System.out.println("\nTest Beginning\n");

		runTest();
	}

	void beginScorer() {
		scorer = new Scorer();

		// Start the "Scorer" thread to be waiting!
		// One thread per node!
		scorerThread = new Thread(new Runnable() {

			public void run() {
				// Begin the wait-serving loop
				scorer.startScorer();
			}

		}, "Scorer");

		scorerThread.start();
	}

	/**
	 * Method which should process the given newly-created node It creates a new
	 * persistent+cache storage and a new instance of the PAST application
	 * 
	 * @param node
	 *            The newly created node
	 * @param num
	 *            The number of this node
	 */
	protected void processNode(int num, Node node) {
		try {
			storages[num] = new StorageManagerImpl(FACTORY,
					new PersistentStorage(FACTORY, "root-" + num, ".", -1,
							environment), new LRUCache(new MemoryStorage(
							FACTORY), 100000, environment));
			pasts[num] = new CatalogService(node, storages[num],
					REPLICATION_FACTOR, INSTANCE, scorer);
			// File f=new File("FreePastry-Storage-Root/root-" + num);

			// User entities created with the RandomGenerated NodeIds
			pasts[num].registerUser(new User(pasts[num].getLocalNodeHandle()
					.getId()));

			// TODO : Check the persistent scenario of simulation
			// File[] files=f.listFiles();
			// int i=0;
			// PastryIdFactory factory;
			// while(i<files.length){
			// factory=new PastryIdFactory(environment);
			// Id fileId = factory.buildId(files[i].getName());
			// MARK pasts[num].getFileMap().put(fileId,files[i].toString());
			// }
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method which should run the test - this is called once all of the nodes
	 * have been created and are ready.
	 */
	protected void runTest() {
		if (NUM_NODES < 2) {
			System.out
					.println("The simulation must be run with at least 2 nodes for proper testing.  Use the '-nodes n' to specify the number of nodes.");
			return;
		}

		// Run each test
		while (main_running) {
			try {
				while (!execRequests.isEmpty()) {
					serveRequest(execRequests.remove(0));
				}
				// simulate();
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
				e.printStackTrace();
			}
		}
	}

	/*
	 * ---------- Test methods and classes ----------
	 */

	/**
	 * Private method which initiates the replica maintenance on all of the
	 * nodes
	 */
	@SuppressWarnings("unused")
	private void runReplicaMaintence() {
		for (int i = 0; i < NUM_NODES; i++) {
			pasts[i].getReplication().replicate();
		}

		// simulate();
	}

	/**
	 * Terminates the Request Dispatcher Thread and destroys the Selector ...
	 */
	protected void cleanUp() {

		main_running = false;
		environment.destroy();
	}

	/**
	 * Getter for the environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * Stores the load to a log file to process later and also computes the
	 * average number of hits in network
	 * 
	 * @return
	 */
	public double computeLoad() {

		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("load.list"));
		} catch (IOException ex) {
			System.out.println("Error occured during writing load.list file");
			ex.printStackTrace();
		}

		double sum = 0;
		for (int i = 0; i < this.pasts.length; i++) {
			out.println(i + "\t" + pasts[i].hits);
			sum += (double) pasts[i].hits;
		}

		if (out.checkError()) {
			System.out.println("Error occured during writing load.list file");
		}

		out.close();
		return (double) (sum / ((double) pasts.length));
	}

	/**
	 * Resets to 0 all the hits counters
	 * 
	 */
	public void resetLoad() {
		for (int i = 0; i < this.pasts.length; i++) {
			pasts[i].hits = 0;
		}
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

				final int nodeNum;
				if (ireq.getSource() == IndexContentRequest.RANDOMSOURCE) {
					// In simulation mode : choose ramdomly a
					// User-CatalogService instance for SOURCE
					nodeNum = this.environment.getRandomSource().nextInt(
							this.pasts.length);
				} else {
					nodeNum = ireq.getSource();
				}
				pasts[nodeNum].indexContent(new File(ireq.getFilePath()),
						new Continuation() {

							public void receiveResult(Object result) {

								System.out.println("File : "
										+ ireq.getFilePath() + ", Source : "
										+ nodeNum + " indexed successfully");
								// TODO : Check the replicas if are updated
								// correctly!
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
									// String msg =
									// "Total "+indexedNum+" terms indexed out of "+results.length+"!";
									// try {
									// ireq.t.sendResponseHeaders(200,
									// msg.getBytes().length);
									// OutputStream os =
									// ireq.t.getResponseBody();
									// os.write(msg.getBytes());
									// os.close();
									// } catch (IOException ex) {
									// java.util.logging.Logger.getLogger(SimDriver.class.getName()).log(Level.SEVERE,
									// null, ex);
									// }

								}
							}

							public void receiveException(Exception result) {
								System.out.println("File : "
										+ ireq.getFilePath() + ", Source : "
										+ nodeNum + " indexed with errors : "
										+ result.getMessage());
							}
						});
			}

		} else if (req instanceof IndexPseydoContentRequest) {

			final IndexPseydoContentRequest ireq = (IndexPseydoContentRequest) req;
			if (ireq.getContentProfileMap() != null) {

				final int nodeNum;
				if (ireq.getSource() == IndexContentRequest.RANDOMSOURCE) {
					// In simulation mode : choose ramdomly a
					// User-CatalogService instance for SOURCE
					nodeNum = this.environment.getRandomSource().nextInt(
							this.pasts.length);
				} else {
					nodeNum = ireq.getSource();
				}

				final SimDriver sd = this;

				pasts[nodeNum].indexPseydoContent(pasts[nodeNum]
						.createContentProfile(ireq.getContentProfileMap(), ireq
								.getDelimiter()), new Continuation() {

					public void receiveResult(Object result) {

						System.out.println("File : " + ireq.getIdentifier()
								+ ", Source : " + nodeNum
								+ " indexed successfully");
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

							// If we have a whole Scenario to complete check the
							// necessary and go forward
							if (currentScenario != null
									&& currentScenario.index_submitted != null) {
								currentScenario.index_finished++;
								// if we've finished with the index requests
								// begin the next request type resolving
								if (currentScenario.index_finished == currentScenario.index_submitted
										.size()) {
									// Reset the state
									currentScenario.index_submitted = null;
									currentScenario.index_finished = 0;
									System.out
											.println("**** INDEX REQUESTS FINISHED FOR THE CURRENT SCENARIO ****");
									if (currentScenario.isFinished()) {
										System.out
												.println("**** SCENARIO FINISHED ****");
										currentScenario = null;
										// if we have some more scenarios to
										// resolve go on and feed the requests
										if (!pendingScenarios.isEmpty()) {
											execRequests.add(pendingScenarios
													.remove(0));
											if (sd != null) {
												// TODO: Check if the
												// monitorobject creates
												// deadlock when multiple
												// requests
												// are issued simultaneously
												synchronized (sd) {
													sd.wasSignalled = true;
													sd.notify();
												}
											}
										}
										return;
									}

									Iterator<Request> it;
									if (currentScenario.tag_submitted != null
											&& !currentScenario.tag_submitted
													.isEmpty()) {
										it = currentScenario.tag_submitted
												.iterator();
										while (it.hasNext()) {
											execRequests.add(it.next());
										}
									} else if (currentScenario.search_submitted != null
											&& !currentScenario.search_submitted
													.isEmpty()) {
										it = currentScenario.search_submitted
												.iterator();
										while (it.hasNext()) {
											execRequests.add(it.next());
										}
									} else if (currentScenario.randomQueries_submitted != null
											&& !currentScenario.randomQueries_submitted
													.isEmpty()) {
										it = currentScenario.randomQueries_submitted
												.iterator();
										while (it.hasNext()) {
											execRequests.add(it.next());
										}
									}

									if (sd != null) {
										// TODO: Check if the monitorobject
										// creates deadlock when multiple
										// requests
										// are issued simultaneously
										synchronized (sd) {
											sd.wasSignalled = true;
											sd.notify();
										}
									}
								}

							}

						}
					}

					public void receiveException(Exception result) {
						System.out.println("File : " + ireq.getIdentifier()
								+ ", Source : " + nodeNum
								+ " indexed with errors : "
								+ result.getMessage());
					}
				});
			}

		} else if (req instanceof IndexURLRequest) {

			final IndexURLRequest ireq = (IndexURLRequest) req;
			if (ireq.getURLProfileMap() != null && ireq.getURL() != null) {

				final int nodeNum;
				if (ireq.getSource() == IndexURLRequest.RANDOMSOURCE) {
					// In simulation mode : choose ramdomly a
					// User-CatalogService instance for SOURCE
					nodeNum = this.environment.getRandomSource().nextInt(
							this.pasts.length);
				} else {
					nodeNum = ireq.getSource();
				}

				pasts[nodeNum].indexURL(ireq.getURL(), pasts[nodeNum]
						.createURLProfile(ireq.getURLProfileMap()),
						new Continuation() {

							public void receiveResult(Object result) {

								System.out.println("URL : " + ireq.getURL()
										+ ", Source : " + nodeNum
										+ " indexed successfully");
								// TODO : Check the replicas if are updated
								// correctly!
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
											+ " index messages out of "
											+ results.length + "!");
								}
							}

							public void receiveException(Exception result) {
								System.out.println("URL : " + ireq.getURL()
										+ ", Source : " + nodeNum
										+ " indexed with errors : "
										+ result.getMessage());
							}
						});
			}

		} else if (req instanceof IndexUserRequest) {
			final IndexUserRequest ireq = (IndexUserRequest) req;
			if (ireq.getUserProfileMap() != null) {

				final int nodeNum;
				if (ireq.getSource() == IndexContentRequest.RANDOMSOURCE) {
					// In simulation mode : choose ramdomly a
					// User-CatalogService instance for SOURCE
					nodeNum = this.environment.getRandomSource().nextInt(
							this.pasts.length);
				} else {
					nodeNum = ireq.getSource();
				}

				final SimDriver sd = this;

				pasts[nodeNum].createUserProfile(ireq.getUserProfileMap(), ireq
						.getDelimiter());
				pasts[nodeNum].indexUser(new Continuation() {

					public void receiveResult(Object result) {

						System.out.println("User : "
								+ pasts[nodeNum].getUser().getUID()
								+ ", Source : " + nodeNum
								+ " indexed successfully");
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

							// If we have a whole Scenario to complete check the
							// necessary and go forward
							if (currentScenario != null
									&& currentScenario.index_submitted != null) {
								currentScenario.index_finished++;
								// if we've finished with the index requests
								// begin the next request type resolving
								if (currentScenario.index_finished == currentScenario.index_submitted
										.size()) {
									// Reset the state
									currentScenario.index_submitted = null;
									currentScenario.index_finished = 0;
									System.out
											.println("**** INDEX REQUESTS FINISHED FOR THE CURRENT SCENARIO ****");
									if (currentScenario.isFinished()) {
										System.out
												.println("**** SCENARIO FINISHED ****");
										currentScenario = null;
										// if we have some more scenarios to
										// resolve go on and feed the requests
										if (!pendingScenarios.isEmpty()) {
											execRequests.add(pendingScenarios
													.remove(0));
											if (sd != null) {
												// TODO: Check if the
												// monitorobject creates
												// deadlock when multiple
												// requests
												// are issued simultaneously
												synchronized (sd) {
													sd.wasSignalled = true;
													sd.notify();
												}
											}
										}
										return;
									}

									Iterator<Request> it;
									if (currentScenario.tag_submitted != null
											&& !currentScenario.tag_submitted
													.isEmpty()) {
										it = currentScenario.tag_submitted
												.iterator();
										while (it.hasNext()) {
											execRequests.add(it.next());
										}
									} else if (currentScenario.search_submitted != null
											&& !currentScenario.search_submitted
													.isEmpty()) {
										it = currentScenario.search_submitted
												.iterator();
										while (it.hasNext()) {
											execRequests.add(it.next());
										}
									} else if (currentScenario.randomQueries_submitted != null
											&& !currentScenario.randomQueries_submitted
													.isEmpty()) {
										it = currentScenario.randomQueries_submitted
												.iterator();
										while (it.hasNext()) {
											execRequests.add(it.next());
										}
									}

									if (sd != null) {
										// TODO: Check if the monitorobject
										// creates deadlock when multiple
										// requests
										// are issued simultaneously
										synchronized (sd) {
											sd.wasSignalled = true;
											sd.notify();
										}
									}

								}

							}
						}
					}

					public void receiveException(Exception result) {
						System.out.println("User : "
								+ pasts[nodeNum].getUser().getUID()
								+ ", Source : " + nodeNum
								+ " indexed with errors : "
								+ result.getMessage());
					}
				});
			}

		} else if (req instanceof SearchContentRequest) {

			// TODO : Rewrite a better version!
			final SearchContentRequest screq = (SearchContentRequest) req;
			String query = screq.getQuery();
			if (query != null) { // Handle the query appropriately

				final int nodeNum;
				if (screq.getSource() == SearchContentRequest.RANDOMSOURCE) {
					// In simulation mode : choose ramdomly a
					// User-CatalogService instance for SOURCE
					nodeNum = this.environment.getRandomSource().nextInt(
							this.pasts.length);
				} else {
					nodeNum = screq.getSource();
				}

				final SimDriver sd = this;

				pasts[nodeNum]
						.searchQuery(
								(screq.getQueryType() == SearchContentRequest.ENHANCED ? QueryPDU.CONTENT_ENHANCEDQUERY
										: QueryPDU.CONTENTQUERY), query, screq
										.getK(), screq.getDelimiter(),
								new Continuation() {

									public void receiveResult(Object result) {
										System.out
												.println("\n**** QUERY RESULTS ****");
										System.out
												.println("Content Query : "
														+ screq.getQuery()
														+ ", Source : "
														+ nodeNum
														+ " Top Results returned successfully");
										// Merging and printing the top results
										if (result instanceof Object[]) {
											// Separate the counting data from
											// the catalog entries
											Object[] catalogs = new Object[((Object[]) result).length];
											ResponsePDU tmp;
											for (int i = 0; i < catalogs.length; i++) {
												tmp = (ResponsePDU) ((Object[]) result)[i];
												catalogs[i] = tmp.getCatalog();

												// For each lookup of the query
												// add the hops to the total
												// value +1 for the response
												// results.totalMessageNumber +=
												// (tmp.getMessagesCounter()+1);
												results.totalMessageNumber += (tmp
														.getMessagesCounter());
											}
											// Update also the number of queries
											// that have been resolved
											results.totalQueryNumber++;

											// Compute the total number of bytes
											ScoreCatalog sc;
											int temp = 0;
											boolean toCount = true;
											for (int i = 0; i < catalogs.length; i++) {
												if (catalogs[i] != null
														&& catalogs[i] instanceof ScoreCatalog) {
													sc = (ScoreCatalog) catalogs[i];
													// Only if we have exactly
													// the number of results we
													// have chosen
													// with K we count them to
													// compute per query bytes!
													if (sc.getScores().size() < results.k) {
														toCount = false;
														break;
													}
													temp += sc.computeBytes();
												}
											}
											if (toCount && temp != 0) {
												results.totalByteNumber += temp;
												results.totalIncludedQueries++;
											}

											System.out
													.println(CatalogService
															.printTopKQueryResults(
																	catalogs,
																	CatalogService.CONTENT,
																	screq
																			.getK()));
											System.out
													.println("**** END OF RESULTS ****");

											// Print the Global Statistics if we
											// have reached the end of the
											// testing
											// Afterwards clean the stats to
											// have fresh counters!
											if (results.totalQueryNumber != 0
													&& results.totalQueryNumber == results.resolved) {
												if (currentScenario != null
														&& currentScenario.randomQueries_submitted != null) {
													currentScenario.random_finished++;
												}
												execRequests
														.add(new StatsRequest(
																StatsRequest.GLOBALSTATS));
												execRequests
														.add(new StatsRequest(
																StatsRequest.CLEANGLOBALSTATS));
												if (sd != null) {
													// TODO: Check if the
													// monitorobject creates
													// deadlock when multiple
													// requests
													// are issued simultaneously
													synchronized (sd) {
														sd.wasSignalled = true;
														sd.notify();
													}
												}
											}

											// If we have a whole Scenario to
											// complete check the necessary and
											// go forward
											if (currentScenario != null) {
												if (currentScenario.randomQueries_submitted != null
														&& !currentScenario.randomQueries_submitted
																.isEmpty()) {
													// if we've finished with
													// the search requests begin
													// the next request type
													// resolving
													if (currentScenario.random_finished == currentScenario.randomQueries_submitted
															.size()) {
														// Reset the state
														currentScenario.randomQueries_submitted = null;
														currentScenario.random_finished = 0;
														System.out
																.println("**** RANDOMQUERIES REQUESTS FINISHED FOR THE CURRENT SCENARIO ****");
														if (currentScenario
																.isFinished()) {
															System.out
																	.println("**** SCENARIO FINISHED ****");
															currentScenario = null;
															// if we have some
															// more scenarios to
															// resolve go on and
															// feed the requests
															if (!pendingScenarios
																	.isEmpty()) {
																execRequests
																		.add(pendingScenarios
																				.remove(0));
																if (sd != null) {
																	// TODO:
																	// Check if
																	// the
																	// monitorobject
																	// creates
																	// deadlock
																	// when
																	// multiple
																	// requests
																	// are
																	// issued
																	// simultaneously
																	synchronized (sd) {
																		sd.wasSignalled = true;
																		sd
																				.notify();
																	}
																}
															}
															return;
														}

														Iterator<Request> it;
														if (currentScenario.index_submitted != null
																&& !currentScenario.index_submitted
																		.isEmpty()) {
															it = currentScenario.index_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														} else if (currentScenario.search_submitted != null
																&& !currentScenario.search_submitted
																		.isEmpty()) {
															it = currentScenario.search_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														} else if (currentScenario.tag_submitted != null
																&& !currentScenario.tag_submitted
																		.isEmpty()) {
															it = currentScenario.tag_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														}
														if (sd != null) {
															// TODO: Check if
															// the monitorobject
															// creates deadlock
															// when multiple
															// requests
															// are issued
															// simultaneously
															synchronized (sd) {
																sd.wasSignalled = true;
																sd.notify();
															}
														}
													}
												} else if (currentScenario.search_submitted != null
														&& !currentScenario.search_submitted
																.isEmpty()) {
													currentScenario.search_finished++;
													// if we've finished with
													// the search requests begin
													// the next request type
													// resolving
													if (currentScenario.search_finished == currentScenario.search_submitted
															.size()) {
														// Reset the state
														currentScenario.search_submitted = null;
														currentScenario.search_finished = 0;
														System.out
																.println("**** SEARCH REQUESTS FINISHED FOR THE CURRENT SCENARIO ****");
														if (currentScenario
																.isFinished()) {
															System.out
																	.println("**** SCENARIO FINISHED ****");
															currentScenario = null;
															// if we have some
															// more scenarios to
															// resolve go on and
															// feed the requests
															if (!pendingScenarios
																	.isEmpty()) {
																execRequests
																		.add(pendingScenarios
																				.remove(0));
																if (sd != null) {
																	// TODO:
																	// Check if
																	// the
																	// monitorobject
																	// creates
																	// deadlock
																	// when
																	// multiple
																	// requests
																	// are
																	// issued
																	// simultaneously
																	synchronized (sd) {
																		sd.wasSignalled = true;
																		sd
																				.notify();
																	}
																}
															}
															return;
														}

														Iterator<Request> it;
														if (currentScenario.index_submitted != null
																&& !currentScenario.index_submitted
																		.isEmpty()) {
															it = currentScenario.index_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														} else if (currentScenario.tag_submitted != null
																&& !currentScenario.tag_submitted
																		.isEmpty()) {
															it = currentScenario.tag_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														} else if (currentScenario.randomQueries_submitted != null
																&& !currentScenario.randomQueries_submitted
																		.isEmpty()) {
															it = currentScenario.randomQueries_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														}
														if (sd != null) {
															// TODO: Check if
															// the monitorobject
															// creates deadlock
															// when multiple
															// requests
															// are issued
															// simultaneously
															synchronized (sd) {
																sd.wasSignalled = true;
																sd.notify();
															}
														}
													}
												}
											}
										} else {
											receiveException(new Exception(
													"The result is not of type Object []"));
										}

										// debugging only
										// if(result instanceof Catalog){
										// System.out.println("Catalog : "+(Catalog)result);
										// }else
										// System.out.println("Result : "+result);

										// Debugging only!!!
										// String msg =
										// "Content Query : "+screq.getQuery()+", Source : "+nodeNum+" Top Results returned successfully";
										// try {
										// screq.t.sendResponseHeaders(200,
										// msg.getBytes().length);
										// OutputStream os =
										// screq.t.getResponseBody();
										// os.write(msg.getBytes());
										// os.close();
										// } catch (IOException ex) {
										// java.util.logging.Logger.getLogger(SimDriver.class.getName()).log(Level.SEVERE,
										// null, ex);
										// }
									}

									public void receiveException(
											Exception result) {
										System.out.println("Content Query : "
												+ screq.getQuery()
												+ ", Source : " + nodeNum
												+ " result (error) "
												+ result.getMessage());
									}

								});
			}

		} else if (req instanceof SearchSocialTagsRequest) {

			// TODO : Rewrite a better version!
			final SearchSocialTagsRequest screq = (SearchSocialTagsRequest) req;
			String query = screq.getQuery();
			if (query != null) { // Handle the query appropriately

				final int nodeNum;
				if (screq.getSource() == SearchSocialTagsRequest.RANDOMSOURCE) {
					// In simulation mode : choose ramdomly a
					// User-CatalogService instance for SOURCE
					nodeNum = this.environment.getRandomSource().nextInt(
							this.pasts.length);
				} else {
					nodeNum = screq.getSource();
				}

				String[] userNodes = null;

				if (screq.getNodeNumbers() != null) {
					int[] nodeNums = screq.getNodeNumbers();
					userNodes = new String[nodeNums.length];
					for (int i = 0; i < nodeNums.length; i++) {
						userNodes[i] = pasts[nodeNums[i]].getUser().getUID()
								.toStringFull();
					}
				} else
					userNodes = screq.getUIDs();

				if (userNodes != null) {
					pasts[nodeNum].searchSocialTagsQuery(QueryPDU.CONTENTQUERY,
							pasts[nodeNum].tokenizeTags(query), userNodes,
							new Continuation() {

								public void receiveResult(Object result) {
									System.out
											.println("Content Social Query : "
													+ screq.getQuery()
													+ ", Source : "
													+ nodeNum
													+ " Top Results returned successfully");
								}

								public void receiveException(Exception result) {
									System.out
											.println("Content Social Query : "
													+ screq.getQuery()
													+ ", Source : " + nodeNum
													+ " result (error) "
													+ result.getMessage());
								}

							});
				} else
					System.out.println("The set of user nodes is empty!");
			}

		} else if (req instanceof SearchURLRequest) {

			// TODO : Rewrite a better version!
			final SearchURLRequest sureq = (SearchURLRequest) req;
			String query = sureq.getQuery();
			if (query != null) { // Handle the query appropriately

				final int nodeNum;
				if (sureq.getSource() == SearchURLRequest.RANDOMSOURCE) {
					// In simulation mode : choose ramdomly a
					// User-CatalogService instance for SOURCE
					nodeNum = this.environment.getRandomSource().nextInt(
							this.pasts.length);
				} else {
					nodeNum = sureq.getSource();
				}

				pasts[nodeNum].searchQuery(QueryPDU.URLQUERY, query, sureq
						.getK(), new Continuation() {

					public void receiveResult(Object result) {
						System.out.println("URL Query : " + sureq.getQuery()
								+ ", Source : " + nodeNum
								+ " Top Results returned successfully");
						// debugging only
						// if(result instanceof Catalog){
						// System.out.println("Catalog : "+(Catalog)result);
						// }else
						// System.out.println("Result : "+result);

						// Debugging only!!!
						// String msg =
						// "Content Query : "+screq.getQuery()+", Source : "+nodeNum+" Top Results returned successfully";
						// try {
						// screq.t.sendResponseHeaders(200,
						// msg.getBytes().length);
						// OutputStream os = screq.t.getResponseBody();
						// os.write(msg.getBytes());
						// os.close();
						// } catch (IOException ex) {
						// java.util.logging.Logger.getLogger(SimDriver.class.getName()).log(Level.SEVERE,
						// null, ex);
						// }
					}

					public void receiveException(Exception result) {
						System.out.println("URL Query : " + sureq.getQuery()
								+ ", Source : " + nodeNum + " result (error) "
								+ result.getMessage());
					}

				});
			}
		} else if (req instanceof SearchUserRequest) {

			// TODO : Rewrite a better version!
			final SearchUserRequest sureq = (SearchUserRequest) req;
			String query = sureq.getQuery();
			if (query != null) { // Handle the query appropriately

				final int nodeNum;
				if (sureq.getSource() == SearchUserRequest.RANDOMSOURCE) {
					// In simulation mode : choose ramdomly a
					// User-CatalogService instance for SOURCE
					nodeNum = this.environment.getRandomSource().nextInt(
							this.pasts.length);
				} else {
					nodeNum = sureq.getSource();
				}

				final SimDriver sd = this;

				pasts[nodeNum]
						.searchQuery(
								(sureq.getQueryType() == SearchUserRequest.ENHANCED ? QueryPDU.USER_ENHANCEDQUERY
										: QueryPDU.USERQUERY), query, sureq
										.getK(), sureq.getDelimiter(),
								new Continuation() {

									public void receiveResult(Object result) {
										System.out
												.println("**** QUERY RESULTS ****");
										System.out
												.println("User Query : "
														+ sureq.getQuery()
														+ ", Source : "
														+ nodeNum
														+ " Top Results returned successfully");
										// Merging and printing the top results
										if (result instanceof Object[]) {
											// Separate the counting data from
											// the catalog entries
											Object[] catalogs = new Object[((Object[]) result).length];
											ResponsePDU tmp;
											for (int i = 0; i < catalogs.length; i++) {
												tmp = (ResponsePDU) ((Object[]) result)[i];
												catalogs[i] = tmp.getCatalog();

												// For each lookup of the query
												// add the hops to the total
												// value +1 for the response
												// results.totalMessageNumber +=
												// (tmp.getMessagesCounter()+1);
												results.totalMessageNumber += (tmp
														.getMessagesCounter());
											}
											// Update also the number of queries
											// that have been resolved
											results.totalQueryNumber++;

											// Compute the total number of bytes
											ScoreCatalog sc;
											int temp = 0;
											boolean toCount = true;
											for (int i = 0; i < catalogs.length; i++) {
												if (catalogs[i] != null
														&& catalogs[i] instanceof ScoreCatalog) {
													sc = (ScoreCatalog) catalogs[i];
													// Only if we have exactly
													// the number of results we
													// have chosen
													// with K we count them to
													// compute per query bytes!
													if (sc.getScores().size() < results.k) {
														toCount = false;
														break;
													}
													temp += sc.computeBytes();
												}
											}
											if (toCount && temp != 0) {
												results.totalByteNumber += temp;
												results.totalIncludedQueries++;
											}

											System.out
													.println(CatalogService
															.printTopKQueryResults(
																	catalogs,
																	CatalogService.USER,
																	sureq
																			.getK()));
											System.out
													.println("**** END OF RESULTS ****");

											// Print the Global Statistics if we
											// have reached the end of the
											// testing
											// Afterwards clean the stats to
											// have fresh counters!
											if (results.totalQueryNumber != 0
													&& results.totalQueryNumber == results.resolved) {
												if (currentScenario != null
														&& currentScenario.randomQueries_submitted != null) {
													currentScenario.random_finished++;
												}
												execRequests
														.add(new StatsRequest(
																StatsRequest.GLOBALSTATS));
												execRequests
														.add(new StatsRequest(
																StatsRequest.CLEANGLOBALSTATS));
												if (sd != null) {
													// TODO: Check if the
													// monitorobject creates
													// deadlock when multiple
													// requests
													// are issued simultaneously
													synchronized (sd) {
														sd.wasSignalled = true;
														sd.notify();
													}
												}
											}

											// If we have a whole Scenario to
											// complete check the necessary and
											// go forward
											if (currentScenario != null) {
												if (currentScenario.randomQueries_submitted != null
														&& !currentScenario.randomQueries_submitted
																.isEmpty()) {
													// if we've finished with
													// the search requests begin
													// the next request type
													// resolving
													if (currentScenario.random_finished == currentScenario.randomQueries_submitted
															.size()) {
														// Reset the state
														currentScenario.randomQueries_submitted = null;
														currentScenario.random_finished = 0;
														System.out
																.println("**** RANDOMQUERIES REQUESTS FINISHED FOR THE CURRENT SCENARIO ****");
														if (currentScenario
																.isFinished()) {
															System.out
																	.println("**** SCENARIO FINISHED ****");
															currentScenario = null;
															// if we have some
															// more scenarios to
															// resolve go on and
															// feed the requests
															if (!pendingScenarios
																	.isEmpty()) {
																execRequests
																		.add(pendingScenarios
																				.remove(0));
																if (sd != null) {
																	// TODO:
																	// Check if
																	// the
																	// monitorobject
																	// creates
																	// deadlock
																	// when
																	// multiple
																	// requests
																	// are
																	// issued
																	// simultaneously
																	synchronized (sd) {
																		sd.wasSignalled = true;
																		sd
																				.notify();
																	}
																}
															}
															return;
														}

														Iterator<Request> it;
														if (currentScenario.index_submitted != null
																&& !currentScenario.index_submitted
																		.isEmpty()) {
															it = currentScenario.index_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														} else if (currentScenario.search_submitted != null
																&& !currentScenario.search_submitted
																		.isEmpty()) {
															it = currentScenario.search_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														} else if (currentScenario.tag_submitted != null
																&& !currentScenario.tag_submitted
																		.isEmpty()) {
															it = currentScenario.tag_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														}
														if (sd != null) {
															// TODO: Check if
															// the monitorobject
															// creates deadlock
															// when multiple
															// requests
															// are issued
															// simultaneously
															synchronized (sd) {
																sd.wasSignalled = true;
																sd.notify();
															}
														}
													}
												} else if (currentScenario.search_submitted != null
														&& !currentScenario.search_submitted
																.isEmpty()) {
													currentScenario.search_finished++;
													// if we've finished with
													// the search requests begin
													// the next request type
													// resolving
													if (currentScenario.search_finished == currentScenario.search_submitted
															.size()) {
														// Reset the state
														currentScenario.search_submitted = null;
														currentScenario.search_finished = 0;
														System.out
																.println("**** SEARCH REQUESTS FINISHED FOR THE CURRENT SCENARIO ****");
														if (currentScenario
																.isFinished()) {
															System.out
																	.println("**** SCENARIO FINISHED ****");
															currentScenario = null;
															// if we have some
															// more scenarios to
															// resolve go on and
															// feed the requests
															if (!pendingScenarios
																	.isEmpty()) {
																execRequests
																		.add(pendingScenarios
																				.remove(0));
																if (sd != null) {
																	// TODO:
																	// Check if
																	// the
																	// monitorobject
																	// creates
																	// deadlock
																	// when
																	// multiple
																	// requests
																	// are
																	// issued
																	// simultaneously
																	synchronized (sd) {
																		sd.wasSignalled = true;
																		sd
																				.notify();
																	}
																}
															}
															return;
														}

														Iterator<Request> it;
														if (currentScenario.index_submitted != null
																&& !currentScenario.index_submitted
																		.isEmpty()) {
															it = currentScenario.index_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														} else if (currentScenario.tag_submitted != null
																&& !currentScenario.tag_submitted
																		.isEmpty()) {
															it = currentScenario.tag_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														} else if (currentScenario.randomQueries_submitted != null
																&& !currentScenario.randomQueries_submitted
																		.isEmpty()) {
															it = currentScenario.randomQueries_submitted
																	.iterator();
															while (it.hasNext()) {
																execRequests
																		.add(it
																				.next());
															}
														}
														if (sd != null) {
															// TODO: Check if
															// the monitorobject
															// creates deadlock
															// when multiple
															// requests
															// are issued
															// simultaneously
															synchronized (sd) {
																sd.wasSignalled = true;
																sd.notify();
															}
														}
													}
												}
											}
										} else {
											receiveException(new Exception(
													"The result is not of type ResponsePDU"));
										}
									}

									public void receiveException(
											Exception result) {
										System.out.println("User Query : "
												+ sureq.getQuery()
												+ ", Source : " + nodeNum
												+ " result (error) "
												+ result.getMessage());
										result.printStackTrace();
									}

								});
			}

		} else if (req instanceof FriendsRequest) {

			final FriendsRequest freq = (FriendsRequest) req;
			String message = freq.getMessage();
			if (message == null) { // If empty put the default greeting message
				message = "Greetings! Do you want us to be friends?";
			}

			// The source node
			final int nodeNum;
			if (freq.getSource() == FriendsRequest.RANDOMSOURCE) {
				// In simulation mode : choose ramdomly a User-CatalogService
				// instance for SOURCE
				nodeNum = this.environment.getRandomSource().nextInt(
						this.pasts.length);
			} else {
				nodeNum = freq.getSource();
			}

			// The destination node
			final int destNodeNum;
			if (freq.getDestination() == FriendsRequest.RANDOMSOURCE) {
				// In simulation mode : choose ramdomly a User-CatalogService
				// instance for DESTINATION
				destNodeNum = this.environment.getRandomSource().nextInt(
						this.pasts.length);
			} else {
				destNodeNum = freq.getDestination();
			}

			final Id destUID = pasts[destNodeNum].getUser().getUID();

			// Check if they are already friends!
			if (pasts[nodeNum].getUser().getFriends().contains(
					new Friend(destUID, null))) {
				System.out.println("User with number " + nodeNum
						+ ", and user with number " + destNodeNum
						+ " are already Friends!");
				return;
			}

			// Determine the type of request Friend Request or Approval.
			Iterator<Id> it2 = pasts[nodeNum].getUser().getPendingFAppr()
					.iterator();
			while (it2.hasNext()) {
				if (it2.next().equals(destUID)) {
					System.out
							.println("This request for Friendship has been already issued from user number "
									+ nodeNum
									+ ", to user number "
									+ destNodeNum);
					return;
				}
			}

			int type = 0; // 0 = Request, 1 = Approval
			FriendRequest f = null;
			Iterator<FriendRequest> it = pasts[nodeNum].getUser()
					.getPendingFReq().iterator();
			while (it.hasNext()) {
				f = it.next();
				if (f.getUID().equals(destUID)) {
					System.out
							.println("Approval request issued from user number"
									+ nodeNum + ", to user number "
									+ destNodeNum);
					type = 1;
					break;
				}
			}

			if (type == 1) {
				pasts[nodeNum].friendApproval(f, new Continuation() {

					public void receiveResult(Object result) {
						System.out.println("Friend Approval : "
								+ freq.getMessage() + ", Source : " + nodeNum
								+ ", Destination : " + destNodeNum
								+ " sent successfully");
					}

					public void receiveException(Exception result) {
						System.out.println("Friend Approval : "
								+ freq.getMessage() + ", Source : " + nodeNum
								+ ", Destination : " + destNodeNum
								+ " result (error) " + result.getMessage());
					}

				});
			} else {
				pasts[nodeNum].friendRequest(destUID, message,
						new Continuation() {

							public void receiveResult(Object result) {
								System.out.println("Friend Request : "
										+ freq.getMessage() + ", Source : "
										+ nodeNum + ", Destination : "
										+ destNodeNum + " sent successfully");
							}

							public void receiveException(Exception result) {
								System.out.println("Friend Request : "
										+ freq.getMessage() + ", Source : "
										+ nodeNum + ", Destination : "
										+ destNodeNum + " result (error) "
										+ result.getMessage());
							}

						});
			}

		} else if (req instanceof TagContentRequest) {

			final TagContentRequest treq = (TagContentRequest) req;
			String tags = treq.getTags();
			if (tags == null) { // If empty put the default greeting message
				System.out
						.println("No tags given! Please type again the command!");
				return;
			}

			// The source node
			final int nodeNum;
			if (treq.getSource() == TagContentRequest.RANDOMSOURCE) {
				// In simulation mode : choose ramdomly a User-CatalogService
				// instance for SOURCE
				nodeNum = this.environment.getRandomSource().nextInt(
						this.pasts.length);
			} else {
				nodeNum = treq.getSource();
			}

			// The number corresponding to the pasts array
			final int destNodeNum = treq.getUID();

			// And the real UID :-)
			final Id destUID = pasts[destNodeNum].getUser().getUID();

			// The contentId for the object we want to tag
			// CAUTION! Normally this should be given from the user through the
			// interface
			// This is a WORKAROUND
			final Id contentId = pasts[nodeNum].getFactory().buildId(
					treq.getCID());

			final SimDriver sd = this;

			pasts[nodeNum].tagContent(destUID, contentId, pasts[nodeNum]
					.tokenizeTags(tags), new Continuation() {

				public void receiveResult(Object result) {
					System.out.println("Content  with Id : " + contentId
							+ " and owner UID : " + destUID
							+ " tagged successfully!");
					System.out.println("Tagee/Source : " + nodeNum
							+ ", Tags : " + treq.getTags());

					// If we have a whole Scenario to complete check the
					// necessary and go forward
					if (currentScenario != null
							&& currentScenario.tag_submitted != null) {
						currentScenario.tag_finished++;
						// if we've finished with the tag content requests begin
						// the next request type resolving
						if (currentScenario.tag_finished == currentScenario.tag_submitted
								.size()) {
							// Reset the state
							currentScenario.tag_submitted = null;
							currentScenario.tag_finished = 0;
							System.out
									.println("**** TAG REQUESTS FINISHED FOR THE CURRENT SCENARIO ****");
							if (currentScenario.isFinished()) {
								System.out
										.println("**** SCENARIO FINISHED ****");
								currentScenario = null;
								// if we have some more scenarios to resolve go
								// on and feed the requests
								if (!pendingScenarios.isEmpty()) {
									execRequests
											.add(pendingScenarios.remove(0));
									if (sd != null) {
										// TODO: Check if the monitorobject
										// creates deadlock when multiple
										// requests
										// are issued simultaneously
										synchronized (sd) {
											sd.wasSignalled = true;
											sd.notify();
										}
									}
								}
								return;
							}

							Iterator<Request> it;
							if (currentScenario.index_submitted != null
									&& !currentScenario.index_submitted
											.isEmpty()) {
								it = currentScenario.index_submitted.iterator();
								while (it.hasNext()) {
									execRequests.add(it.next());
								}
							} else if (currentScenario.search_submitted != null
									&& !currentScenario.search_submitted
											.isEmpty()) {
								it = currentScenario.search_submitted
										.iterator();
								while (it.hasNext()) {
									execRequests.add(it.next());
								}
							} else if (currentScenario.randomQueries_submitted != null
									&& !currentScenario.randomQueries_submitted
											.isEmpty()) {
								it = currentScenario.randomQueries_submitted
										.iterator();
								while (it.hasNext()) {
									execRequests.add(it.next());
								}
							}

							if (sd != null) {
								// TODO: Check if the monitorobject creates
								// deadlock when multiple requests
								// are issued simultaneously
								synchronized (sd) {
									sd.wasSignalled = true;
									sd.notify();
								}
							}
						}

					}

				}

				public void receiveException(Exception result) {
					System.out.println("Tag Content Request : " + contentId
							+ " and owner UID : " + destUID
							+ ", result (error) " + result.getMessage());
				}
			});

		} else if (req instanceof RetrieveContRequest) {

			final RetrieveContRequest rcreq = (RetrieveContRequest) req;

			// The source node
			final int nodeNum;
			if (rcreq.getSource() == RetrieveContRequest.RANDOMSOURCE) {
				// In simulation mode : choose ramdomly a User-CatalogService
				// instance for SOURCE
				nodeNum = this.environment.getRandomSource().nextInt(
						this.pasts.length);
			} else {
				nodeNum = rcreq.getSource();
			}

			// The number corresponding to the pasts array
			final int destNodeNum = rcreq.getUID();

			// And the real UID :-)
			final Id destUID = pasts[destNodeNum].getUser().getUID();

			// The contentId for the object we want to fetch
			// CAUTION! Normally this should be given from the user through the
			// interface
			// This is a WORKAROUND
			final Id contentId = pasts[nodeNum].getFactory()
					.buildIdFromToString(rcreq.getCID());

			pasts[nodeNum].retrieveContent(destUID, contentId,
					rcreq.getCloud(), new Continuation() {

						public void receiveResult(Object result) {
							System.out.println("Content  with Id : "
									+ contentId + " and owner UID : " + destUID
									+ " retrieved successfully!");
						}

						public void receiveException(Exception result) {
							System.out.println("Content  with Id : "
									+ contentId + " and owner UID : " + destUID
									+ " , result (error) "
									+ result.getMessage());
						}
					});

		} else if (req instanceof StatsRequest) {

			final StatsRequest streq = (StatsRequest) req;
			int type = streq.getType();

			if (type == StatsRequest.GLOBALSTATS) {
				System.out
						.println("\n***************************** Global Stats *****************************************\n");
				System.out.println("* Total Queries : "
						+ results.totalQueryNumber + " \n");
				System.out.println("* Total Messages : "
						+ results.totalMessageNumber + " \n");
				System.out.println("* Average Messages per Query : "
						+ results.computeAVGMessages() + " \n");
				System.out.println("* Number of nodes : " + results.nodes
						+ " \n");
				System.out
						.println("* Number of search results parameter (k) : "
								+ results.k + " \n");
				System.out.println("* Number of search keywords per query : "
						+ results.keywords + " \n");
				System.out
						.println("* Total number of bytes from result lists (only > k ): "
								+ results.totalByteNumber + " \n");
				System.out
						.println("* Average number of bytes per query (only > k ): "
								+ results.computeQueryOverhead() + " \n");
				System.out
						.println("* Average Network load computed as node hits : "
								+ this.computeLoad() + "\n");
				System.out
						.println("**************************************************************************************\n");
				return;
			} else if (type == StatsRequest.CLEANGLOBALSTATS) {
				results.totalQueryNumber = 0;
				results.totalMessageNumber = 0;
				results.totalByteNumber = 0;
				results.totalIncludedQueries = 0;
				results.resolved = 0;
				System.out.println("\n Global Statistics have been cleaned!\n");
				return;
			}

			// The source node
			final int nodeNum;
			if (streq.getSource() == StatsRequest.RANDOMSOURCE) {
				// In simulation mode : choose ramdomly a User-CatalogService
				// instance for SOURCE
				nodeNum = this.environment.getRandomSource().nextInt(
						this.pasts.length);
			} else {
				nodeNum = streq.getSource();
			}

			// PENDING, SHARED OR PROFILES
			System.out.println("\n\n >>> Node " + nodeNum);
			if (type == StatsRequest.PENDING) {
				System.out
						.println("\n*********** Pending Friend Requests ***********\n");
				Iterator<FriendRequest> it = pasts[nodeNum].getUser()
						.getPendingFReq().iterator();
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
				Iterator<Id> it2 = pasts[nodeNum].getUser().getPendingFAppr()
						.iterator();
				while (it2.hasNext()) {
					System.out.println("\n - UID : " + it2.next());
				}
				System.out
						.println("\n**************************************************\n");
			} else if (type == StatsRequest.SHARED) {
				System.out
						.println("\n\n*********** User Shared Content ***********\n");
				Map<Id, File> sc = pasts[nodeNum].getUser().getSharedContent();
				Map<Id, TagCloud> ctc = pasts[nodeNum].getUser()
						.getContentTagClouds();
				Iterator<Id> it = sc.keySet().iterator();
				int i = 0;
				Id tmp;
				TagCloud tc;
				while (it.hasNext()) {
					tmp = it.next();
					System.out.println("\n - " + (++i) + ". File : "
							+ sc.get(tmp).toString() + ", Content Id : "
							+ tmp.toStringFull());
					tc = ctc.get(tmp);
					if (tc != null) {
						System.out.println(tc.toString());
					}
				}
				System.out
						.println("\n*******************************************\n");
			} else if (type == StatsRequest.PROFILES) {
				System.out
						.println("\n\n*********** User Profile ***********\n");
				ContentProfile cp = pasts[nodeNum].getUser().getUserProfile();
				if (cp != null) {
					System.out.println(cp.toStringWithoutTF());
				}
				System.out
						.println("\n*********** Social Tagged Content ***********\n");
				Map<String, SocialCatalog> tcl = pasts[nodeNum].getUser()
						.getTagContentList();
				Iterator<String> iter = tcl.keySet().iterator();
				String tag;
				SocialCatalog sc;
				while (iter.hasNext()) {
					tag = iter.next();
					sc = tcl.get(tag);
					if (sc != null)
						System.out.println(sc.toString());
				}
				System.out
						.println("\n\n*********** User Friends ***********\n");
				Iterator<Friend> it = pasts[nodeNum].getUser().getFriends()
						.iterator();
				while (it.hasNext()) {
					System.out.println(it.next());
				}
				System.out
						.println("\n**************************************************\n");
			} else if (type == StatsRequest.BOOKMARKS) {
				System.out
						.println("\n\n*********** User BookMarks ***********\n");
				Map<Id, SocialBookMark> bm = pasts[nodeNum].getUser()
						.getBookMarks();
				Iterator<Id> it = bm.keySet().iterator();
				while (it.hasNext()) {
					SocialBookMark sb = bm.get(it.next());
					if (sb instanceof URLBookMark) {
						URLBookMark ubm = (URLBookMark) sb;
						System.out.println("::: URL ::: " + ubm.getAddress());
						System.out.println("::: Tags/Decription ::: "
								+ ubm.getTags().toStringWithoutTF());
					}
				}
				System.out
						.println("\n**************************************************\n");
			}

		} else if (req instanceof RandomQueriesRequest) {
			final RandomQueriesRequest rqreq = (RandomQueriesRequest) req;

			// Compute the size of the array we want to fill with source nodes
			int size = rqreq.getQueries();

			// The container of used source nums
			Vector<Integer> qRequests = new Vector<Integer>();

			int num = 0, num2 = 0;
			int type = rqreq.getType();

			// Update the results stats
			results.keywords = rqreq.getKeywords();
			results.k = rqreq.getK();
			results.resolved = size;

			while (qRequests.size() < size) {
				num = this.environment.getRandomSource().nextInt(
						this.pasts.length);
				// if(qRequests.contains(new Integer(num)))
				// continue;
				// else{
				qRequests.add(new Integer(num));

				if (type == QueryPDU.CONTENTQUERY
						|| type == QueryPDU.CONTENT_ENHANCEDQUERY) {
					// Find the users which have indexed some content :)
					Vector<Integer> nodeNums = new Vector<Integer>();
					for (int z = 0; z < pasts.length; z++) {
						if (!pasts[z].getUser().getSharedContentProfile()
								.isEmpty())
							nodeNums.add(new Integer(z));
					}

					// Find req.keywords random keywords to query the network
					String query = "";
					boolean done;
					String term = "";
					int keyNum;
					Id cId = null;
					Map<Id, ContentProfile> tmpMap;
					for (int j = 0; j < rqreq.getKeywords(); j++) {
						done = false;
						do {
							cId = null;
							num2 = nodeNums.get(
									this.environment.getRandomSource().nextInt(
											nodeNums.size())).intValue();
							tmpMap = pasts[num2].getUser()
									.getSharedContentProfile();

							// The random chosen content which will be searched
							// for some of its terms
							keyNum = this.environment.getRandomSource()
									.nextInt(tmpMap.keySet().size());
							Iterator<Id> it = tmpMap.keySet().iterator();
							for (int k = 0; k < keyNum; k++) {
								if (it.hasNext())
									cId = it.next();
							}

							if (it.hasNext())
								cId = it.next();

							// With the Delimiter " "
							if (cId != null) {
								term = tmpMap.get(cId).randomTerm();
								if (term != null && !term.equals("")) {
									if (j != 0)
										query += "::";
									done = true;
									query += term;
								}
							}
						} while (done == false);
					}

					execRequests.add(new SearchContentRequest(query, type, num,
							rqreq.getK()));
				} else if (type == QueryPDU.USERQUERY
						|| type == QueryPDU.USER_ENHANCEDQUERY) {
					// TODO : FIND THE NON-NULL USER PROFILES AND MAKE THE
					// QUERIES ONLY FOR THEM!!!!
					// Find req.keywords random keywords to query the network
					String query = "";
					boolean done;
					String term = "";
					for (int j = 0; j < rqreq.getKeywords(); j++) {
						done = false;
						do {
							num2 = this.environment.getRandomSource().nextInt(
									this.pasts.length);
							// TODO : With the Delimiter
							term = pasts[num2].getUser().getUserProfile()
									.randomTerm();
							if (term != null && !term.equals("")) {
								if (j != 0)
									query += "::";
								done = true;
								query += term;
							}
						} while (done == false); // To ensure that no empty
													// query will occur!!
					}
					execRequests.add(new SearchUserRequest(query, type, num,
							rqreq.getK(), "::"));
				}
				// }
			}
		} else if (req instanceof ScenarioRequest) {
			final ScenarioRequest sreq = (ScenarioRequest) req;

			// Feed the currentScenario with the appropriate data
			this.currentScenario = new Scenario(sreq.index_submitted,
					sreq.search_submitted, sreq.tag_submitted,
					sreq.randomQueries_submitted);

			System.out.println("**** SCENARIO STARTED ****");

			// Now Add the request to the queue and begin execution
			// Begin with the indexing requests
			Iterator<Request> it;
			if (currentScenario.index_submitted != null
					&& !currentScenario.index_submitted.isEmpty()) {
				it = currentScenario.index_submitted.iterator();
				while (it.hasNext()) {
					execRequests.add(it.next());
				}
			} else if (currentScenario.tag_submitted != null
					&& !currentScenario.tag_submitted.isEmpty()) {
				it = currentScenario.tag_submitted.iterator();
				while (it.hasNext()) {
					execRequests.add(it.next());
				}
			} else if (currentScenario.search_submitted != null
					&& !currentScenario.search_submitted.isEmpty()) {
				it = currentScenario.search_submitted.iterator();
				while (it.hasNext()) {
					execRequests.add(it.next());
				}
			} else if (currentScenario.randomQueries_submitted != null
					&& !currentScenario.randomQueries_submitted.isEmpty()) {
				it = currentScenario.randomQueries_submitted.iterator();
				while (it.hasNext()) {
					execRequests.add(it.next());
				}
			}

			// No need to notify cause we already have woken up the thread!!

		}

	}

}