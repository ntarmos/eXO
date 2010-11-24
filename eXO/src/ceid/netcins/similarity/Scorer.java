/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

import ceid.netcins.catalog.Catalog;
import ceid.netcins.catalog.ContentCatalogEntry;
import ceid.netcins.catalog.ScoreCatalog;
import ceid.netcins.catalog.UserCatalogEntry;
import ceid.netcins.content.ContentField;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.content.TermField;
import ceid.netcins.content.TokenizedField;
import ceid.netcins.messages.QueryPDU;
import ceid.netcins.messages.ResponsePDU;

/**
 * This object defines the methods of the "Scorer" thread which is waiting until
 * a scoring request has been issued to the scoringRequest queue! The thread
 * itself is created in the CatalogService class!
 * 
 * TODO : REFINEMENT
 * 
 * @author andy
 * @version 1.0
 */
public class Scorer {

	// Coefficients to compute the ENHANCED scores
	public static final float A1 = (float) 0.5;
	public static final float A2 = (float) 0.5;

	// Variable to avoid missed signals and spurious wakeups
	private boolean wasSignalled = false;

	// The Vector with the requests for the Scorer thread
	private Vector<SimilarityRequest> similarityRequests;

	// Controls the running loop of the "Scorer" thread!
	private boolean main_running;

	public Scorer() {

		similarityRequests = new Vector<SimilarityRequest>();
		main_running = true;
	}

	/**
	 * notify the Scorer Thread that a request has been received
	 */
	public void doNotify() {
		if (this != null) {
			// TODO: Check if the monitorobject creates deadlock when multiple
			// requests
			// are issued simultaneously - when the thread is in a function!
			synchronized (this) {
				wasSignalled = true;
				this.notify();
			}
		}
	}

	/**
	 * This method executes the corresponding scoring request!
	 * 
	 * @param remove
	 */
	private void serveRequest(SimilarityRequest req) {

		Catalog catalog = req.getCatalog();
		String[] query = req.getQuery();

		// ************** CONTENT SEARCHING PART **************
		if ((req.getType() == QueryPDU.CONTENTQUERY || req.getType() == QueryPDU.CONTENT_ENHANCEDQUERY)
				&& catalog.getContentCatalogEntries() != null
				&& !catalog.getContentCatalogEntries().isEmpty()) {

			// Compute Global Set of terms and feed in the CosineSimilarity
			Iterator<ContentCatalogEntry> it = catalog
					.getContentCatalogEntries().iterator();
			ContentCatalogEntry entry;
			ContentProfile cprof;// The check "instanceof" is important
			Iterator<ContentField> itcf;
			ContentField cfield;
			// They must be reused
			TreeSet<String> docTerms = new TreeSet<String>();
			CosineSimilarity cossim = null, cossimUserProfiles = null;

			// HashMap which contains the CatalogEntries with the corresponding
			// score
			HashMap<ContentCatalogEntry, Float> scoreBoard = new HashMap<ContentCatalogEntry, Float>();

			// TODO : Fix the query part!
			// 1. QUERY WEIGHTS
			BinaryWeight[] queryWeights = new BinaryWeight[query.length];
			int z = 0;
			for (z = 0; z < query.length; z++) {
				queryWeights[z] = new BinaryWeight(query[z]);
			}

			// Computation of source and destination users' global term set!
			// 2. SOURCE USER PROFILE WEIGHTS (ENHANCED QUERY)
			if (req.getType() == QueryPDU.CONTENT_ENHANCEDQUERY
					|| req.getType() == QueryPDU.USER_ENHANCEDQUERY
					|| req.getType() == QueryPDU.HYBRID_ENHANCEDQUERY) {

				// *** Source User ***
				// Important to clear in order to be reused!
				docTerms.clear();

				// get Source User Profile and compute global term set
				cprof = req.getSourceUserProfile();
				if (cprof != null) {
					itcf = cprof.getFields().iterator();
					// Iterate through every ContentField
					while (itcf.hasNext()) {
						cfield = itcf.next();
						if (cfield instanceof TokenizedField) {
							TokenizedField tkzf = (TokenizedField) cfield;
							String[] fieldterms = tkzf.getTerms();
							for (int q = 0; q < fieldterms.length; q++) {
								docTerms.add(fieldterms[q]);
							}
						} else if (cfield instanceof TermField) {
							docTerms.add(((TermField) cfield).getFieldData());
						}
					}

					// Create the Weights!
					Iterator<String> it3 = docTerms.iterator();
					BinaryWeight[] profileWeights1 = new BinaryWeight[docTerms
							.size()];
					z = 0;
					while (it3.hasNext()) {
						profileWeights1[z] = new BinaryWeight(it3.next());
						z++;
					}

					// This should be reused with every entry's user profile
					cossimUserProfiles = new CosineSimilarity(null,
							profileWeights1);
				}
			}

			// For every entry of Catalog compute 1)global term set and 2) the
			// CosineSimilarity
			while (it.hasNext()) {
				// Important to clear in order to be reused!
				docTerms.clear();
				entry = it.next();

				// Content Profile
				cprof = entry.getContentProfile();
				itcf = cprof.getFields().iterator();
				// Iterate through every ContentField
				while (itcf.hasNext()) {
					cfield = itcf.next();
					if (cfield instanceof TokenizedField) {
						TokenizedField tkzf = (TokenizedField) cfield;
						String[] fieldterms = tkzf.getTerms();
						for (int q = 0; q < fieldterms.length; q++) {
							docTerms.add(fieldterms[q]);
						}
					} else if (cfield instanceof TermField) {
						docTerms.add(((TermField) cfield).getFieldData());
					}
				}
				// Create the Weights!
				// 3. CONTENT PROFILE WEIGHTS
				Iterator<String> it3 = docTerms.iterator();
				BinaryWeight[] docWeights = new BinaryWeight[docTerms.size()];
				z = 0;
				while (it3.hasNext()) {
					docWeights[z] = new BinaryWeight(it3.next());
					z++;
				}
				if (cossim != null)
					cossim.setDocWeights(docWeights); // reuse!
				else
					cossim = new CosineSimilarity(docWeights, queryWeights);

				// *** User Profile of this entry - global term computation ***
				// Important to clear in order to be reused!
				docTerms.clear();
				// 4. ENTRY's USER PROFILE WEIGHTS (ENHANCED QUERY)
				if (cossimUserProfiles != null) {
					// User Profile
					cprof = entry.getUserProfile();
					if (cprof != null) {
						itcf = cprof.getFields().iterator();
						// Iterate through every ContentField
						while (itcf.hasNext()) {
							cfield = itcf.next();
							if (cfield instanceof TokenizedField) {
								TokenizedField tkzf = (TokenizedField) cfield;
								String[] fieldterms = tkzf.getTerms();
								for (int q = 0; q < fieldterms.length; q++) {
									docTerms.add(fieldterms[q]);
								}
							} else if (cfield instanceof TermField) {
								docTerms.add(((TermField) cfield)
										.getFieldData());
							}
						}
						// Create the Weights!
						Iterator<String> it4 = docTerms.iterator();
						BinaryWeight[] profileWeights2 = new BinaryWeight[docTerms
								.size()];
						z = 0;
						while (it4.hasNext()) {
							profileWeights2[z] = new BinaryWeight(it4.next());
							z++;
						}

						cossimUserProfiles.setDocWeights(profileWeights2); // reuse!

						// TODO : Sorting and adding to the new Catalog-Response
						scoreBoard.put(entry, new Float(0.5 * cossim.getScore()
								+ 0.5 * cossimUserProfiles.getScore()));
					} else
						scoreBoard.put(entry, new Float(
								0.5 * cossim.getScore() + 0.5 * 0));
				} else {
					scoreBoard.put(entry, new Float(cossim.getScore()));
				}

			} // End of Entry Similarity

			// Sort by float score value
			LinkedHashMap<ContentCatalogEntry, Float> sortedScoreBoard = this
					.sortHashMapByValuesD(scoreBoard);

			// TODO: Fix this part!!!!
			Vector<ContentCatalogEntry> v1 = new Vector<ContentCatalogEntry>();
			v1.addAll(sortedScoreBoard.keySet());
			Vector<Float> v2 = new Vector<Float>();
			v2.addAll(sortedScoreBoard.values());

			// Select the k results that will be returned
			int kst = req.getK(), startOfTies = 0;
			if (kst != QueryPDU.RETURN_ALL && v1.size() > kst) {
				Vector<ContentCatalogEntry> randomSet = new Vector<ContentCatalogEntry>();
				// As we count from 0 and not from 1
				kst--;
				Float kstScore = v2.get(kst);
				// The previous ties including kst CCE
				while (kst >= 0 && v2.get(kst).equals(kstScore)) {
					randomSet.add(v1.get(kst));
					kst--;
				}
				// Here we will start applying our random choice (+1)
				startOfTies = kst + 1;
				kst = req.getK();
				// The next ties
				while (kst < v1.size() && v2.get(kst).equals(kstScore)) {
					randomSet.add(v1.get(kst));
					kst++;
				}
				// Clean the ties temporary
				while (v1.size() != startOfTies || v2.size() != startOfTies) {
					v1.remove(v1.lastElement());
					v2.remove(v2.lastElement());
				}
				// How many to print from the randomSet?
				int answer = req.getK() - startOfTies;
				int choice;
				// Pick radomly a CCE and put it in the printed
				Random random = new Random();
				for (int l = 0; l < answer; l++) {
					choice = random.nextInt(randomSet.size());
					v1.add(randomSet.get(choice));
					randomSet.remove(choice);
					// Put the corresponding score values
					v2.add(kstScore);
				}
			}

			ScoreCatalog topK = new ScoreCatalog(req.getCatalog().getTID(), v1,
					v2);
			req.getContinuation().receiveResult(
					new ResponsePDU(req.getMessagesCounter(), topK));

			// ************** USER SEARCHING PART **************
		} else if ((req.getType() == QueryPDU.USERQUERY || req.getType() == QueryPDU.USER_ENHANCEDQUERY)
				&& catalog.getUserCatalogEntries() != null
				&& !catalog.getUserCatalogEntries().isEmpty()) {

			// Compute Global Set of terms and feed in the CosineSimilarity
			Iterator<UserCatalogEntry> it = catalog.getUserCatalogEntries()
					.iterator();
			UserCatalogEntry entry;
			ContentProfile cprof;// The check "instanceof" is important
			Iterator<ContentField> itcf;
			ContentField cfield;
			// They must be reused
			TreeSet<String> docTerms = new TreeSet<String>();
			CosineSimilarity cossim = null, cossimUserProfiles = null;

			// HashMap which contains the CatalogEntries with the corresponding
			// score
			HashMap<UserCatalogEntry, Float> scoreBoard = new HashMap<UserCatalogEntry, Float>();

			// TODO : Fix the query part!
			// 1. QUERY WEIGHTS
			BinaryWeight[] queryWeights = new BinaryWeight[query.length];
			int z = 0;
			for (z = 0; z < query.length; z++) {
				queryWeights[z] = new BinaryWeight(query[z]);
			}

			// Computation of source and destination users' global term set!
			// 2. SOURCE USER PROFILE WEIGHTS (ENHANCED QUERY)
			if (req.getType() == QueryPDU.CONTENT_ENHANCEDQUERY
					|| req.getType() == QueryPDU.USER_ENHANCEDQUERY
					|| req.getType() == QueryPDU.HYBRID_ENHANCEDQUERY) {

				// *** Source User ***
				// Important to clear in order to be reused!
				docTerms.clear();

				// get Source User Profile and compute global term set
				cprof = req.getSourceUserProfile();
				if (cprof != null) {
					itcf = cprof.getFields().iterator();
					// Iterate through every ContentField
					while (itcf.hasNext()) {
						cfield = itcf.next();
						if (cfield instanceof TokenizedField) {
							TokenizedField tkzf = (TokenizedField) cfield;
							String[] fieldterms = tkzf.getTerms();
							for (int q = 0; q < fieldterms.length; q++) {
								docTerms.add(fieldterms[q]);
							}
						} else if (cfield instanceof TermField) {
							docTerms.add(((TermField) cfield).getFieldData());
						}
					}

					// Create the Weights!
					Iterator<String> it3 = docTerms.iterator();
					BinaryWeight[] profileWeights1 = new BinaryWeight[docTerms
							.size()];
					z = 0;
					while (it3.hasNext()) {
						profileWeights1[z] = new BinaryWeight(it3.next());
						z++;
					}

					// This should be reused with every entry's user profile
					cossimUserProfiles = new CosineSimilarity(null,
							profileWeights1);
				}

			}

			// For every entry of Catalog compute 1)global term set and 2) the
			// CosineSimilarity
			while (it.hasNext()) {
				// Important to clear in order to be reused!
				docTerms.clear();
				entry = it.next();

				// User Profile
				cprof = entry.getUserProfile();
				itcf = cprof.getFields().iterator();
				// Iterate through every ContentField
				while (itcf.hasNext()) {
					cfield = itcf.next();
					if (cfield instanceof TokenizedField) {
						TokenizedField tkzf = (TokenizedField) cfield;
						String[] fieldterms = tkzf.getTerms();
						for (int q = 0; q < fieldterms.length; q++) {
							docTerms.add(fieldterms[q]);
						}
					} else if (cfield instanceof TermField) {
						docTerms.add(((TermField) cfield).getFieldData());
					}
				}
				// Create the Weights!
				// 3. ENTRY's USER PROFILE WEIGHTS
				Iterator<String> it3 = docTerms.iterator();
				BinaryWeight[] docWeights = new BinaryWeight[docTerms.size()];
				z = 0;
				while (it3.hasNext()) {
					docWeights[z] = new BinaryWeight(it3.next());
					z++;
				}
				if (cossim != null)
					cossim.setDocWeights(docWeights); // reuse!
				else
					cossim = new CosineSimilarity(docWeights, queryWeights);

				// *** User Profile of this entry - global term computation ***
				// Important to clear in order to be reused!
				// NO NEED TO CLEAR THE docTerms because we want the same
				// weights!!!
				if (cossimUserProfiles != null) {
					// Create the Weights!
					// 4. ENTRY's USER PROFILE WEIGHTS (ENHANCED QUERY)
					Iterator<String> it4 = docTerms.iterator();
					BinaryWeight[] profileWeights2 = new BinaryWeight[docTerms
							.size()];
					z = 0;
					while (it4.hasNext()) {
						profileWeights2[z] = new BinaryWeight(it4.next());
						z++;
					}

					cossimUserProfiles.setDocWeights(profileWeights2); // reuse!

					// TODO : Sorting and adding to the new Catalog-Response
					scoreBoard.put(entry, new Float(0.5 * cossim.getScore()
							+ 0.5 * cossimUserProfiles.getScore()));
				} else {
					scoreBoard.put(entry, new Float(cossim.getScore()));
				}

			} // End of Entry Similarity

			// Sort by float score value
			LinkedHashMap<UserCatalogEntry, Float> sortedScoreBoard = this
					.sortHashMapByValuesU(scoreBoard);

			// TODO: Fix this part!!!!
			Vector<UserCatalogEntry> v1 = new Vector<UserCatalogEntry>();
			v1.addAll(sortedScoreBoard.keySet());
			Vector<Float> v2 = new Vector<Float>();
			v2.addAll(sortedScoreBoard.values());

			// Select the k results that will be returned
			int kst = req.getK(), startOfTies = 0;
			if (kst != QueryPDU.RETURN_ALL && v1.size() > kst) {
				Vector<UserCatalogEntry> randomSet = new Vector<UserCatalogEntry>();
				// As we count from 0 and not from 1
				kst--;
				Float kstScore = v2.get(kst);
				// The previous ties including kst UCE
				while (kst >= 0 && v2.get(kst).equals(kstScore)) {
					randomSet.add(v1.get(kst));
					kst--;
				}
				// Here we will start applying our random choice (+1)
				startOfTies = kst + 1;
				kst = req.getK();
				// The next ties
				while (kst < v1.size() && v2.get(kst).equals(kstScore)) {
					randomSet.add(v1.get(kst));
					kst++;
				}
				// Clean the ties temporary
				while (v1.size() != startOfTies || v2.size() != startOfTies) {
					v1.remove(v1.lastElement());
					v2.remove(v2.lastElement());
				}
				// How many to print from the randomSet?
				int answer = req.getK() - startOfTies;
				int choice;
				// Pick radomly a CCE and put it in the printed
				Random random = new Random();
				for (int l = 0; l < answer; l++) {
					choice = random.nextInt(randomSet.size());
					v1.add(randomSet.get(choice));
					randomSet.remove(choice);
					// Put the corresponding score values
					v2.add(kstScore);
				}
			}

			ScoreCatalog topK = new ScoreCatalog(req.getCatalog().getTID(), v1,
					v2);
			req.getContinuation().receiveResult(
					new ResponsePDU(req.getMessagesCounter(), topK));
		} else { // Raw Catalog Without Scores :-)
			req.getContinuation().receiveResult(
					new ResponsePDU(req.getMessagesCounter(), catalog));
		}
	}

	public void startScorer() {
		// Run each test
		while (main_running) {
			try {
				while (!similarityRequests.isEmpty()) {
					serveRequest(similarityRequests.remove(0));
				}
				// Wait until the Selector Thread notify!
				while (!wasSignalled) {
					synchronized (this) {
						try {
							wait();
						} catch (InterruptedException ex) {
							System.out.println("Scorer woke up!");
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
	 * This function will return the scored CatalogEntries sorted by the value
	 * of score!!!
	 * 
	 * @param passedMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<ContentCatalogEntry, Float> sortHashMapByValuesD(
			HashMap<ContentCatalogEntry, Float> passedMap) {
		List<ContentCatalogEntry> mapKeys = new ArrayList<ContentCatalogEntry>(
				passedMap.keySet());
		List<Float> mapValues = new ArrayList<Float>(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		// Descending Order
		Collections.reverse(mapKeys);
		Collections.reverse(mapValues);

		LinkedHashMap<ContentCatalogEntry, Float> sortedMap = new LinkedHashMap<ContentCatalogEntry, Float>();

		Iterator<Float> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator<ContentCatalogEntry> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				Float comp1 = passedMap.get(key);
				Float comp2 = (Float) val;

				if (comp1.equals(comp2)) {
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((ContentCatalogEntry) key, (Float) val);
					break;
				}

			}

		}
		return sortedMap;
	}

	/**
	 * This function will return the scored UserCatalogEntries sorted by the
	 * value of score!!!
	 * 
	 * @param passedMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<UserCatalogEntry, Float> sortHashMapByValuesU(
			HashMap<UserCatalogEntry, Float> passedMap) {
		List<UserCatalogEntry> mapKeys = new ArrayList<UserCatalogEntry>(
				passedMap.keySet());
		List<Float> mapValues = new ArrayList<Float>(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		// Descending Order
		Collections.reverse(mapKeys);
		Collections.reverse(mapValues);

		LinkedHashMap<UserCatalogEntry, Float> sortedMap = new LinkedHashMap<UserCatalogEntry, Float>();

		Iterator<Float> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator<UserCatalogEntry> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				Float comp1 = passedMap.get(key);
				Float comp2 = (Float) val;

				if (comp1.equals(comp2)) {
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((UserCatalogEntry) key, (Float) val);
					break;
				}

			}

		}
		return sortedMap;
	}

	public Vector<SimilarityRequest> getSimilarityRequests() {
		return similarityRequests;
	}

	public void cleanup() {
		this.main_running = false;
	}

	public void addRequest(SimilarityRequest req) {
		this.similarityRequests.add(req);
	}

}
