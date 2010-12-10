package ceid.netcins.catalog;

import java.io.Serializable;
import java.util.Vector;

/**
 * This class represents a Catalog enhanced with some scoring of the
 * CatalogEntries
 * 
 * @author Andreas Loupasakis
 * @version 1.0
 */
public class ScoreBoard  implements Serializable {

	private static final long serialVersionUID = -7733473333205719161L;
	private Vector<Float> scoreValues;
	private Vector<CatalogEntry> catalogEntries;

	/**
	 * rows and scoreValues must be sorted appropriately in Scorer!
	 * 
	 * @param tid
	 * @param rows
	 * @param scoreValues
	 */
	public ScoreBoard(Vector<CatalogEntry> catalogEntries,
			Vector<Float> scoreValues) {
		this.catalogEntries = catalogEntries;
		this.scoreValues = scoreValues;
	}

	public Vector<Float> getScores() {
		return this.scoreValues;
	}
	
	public Vector<CatalogEntry> getCatalogEntries(){
		return this.catalogEntries;
	}
	
	public double computeBytes() {
		double counter = 0;
		counter += Float.SIZE * scoreValues.size();

		if (catalogEntries != null) {
			for (CatalogEntry ce : catalogEntries) {
				counter += ce.computeTotalBytes();
			}
		}

		return counter;
	}
}
