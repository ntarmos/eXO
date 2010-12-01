

package ceid.netcins.catalog;

import java.io.Serializable;
import java.util.Vector;

import rice.p2p.commonapi.Id;

/**
 * This class represents a Catalog enhanced with some scoring of the
 * CatalogEntries
 * 
 * @author Andreas Loupasakis
 * @version 1.0
 */
public class ScoreCatalog extends Catalog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7733473333205719161L;
	private Vector<Float> scoreValues;

	/**
	 * rows and scoreValues must be sorted appropriately in Scorer!
	 * 
	 * @param tid
	 * @param rows
	 * @param scoreValues
	 */
	public ScoreCatalog(Id tid, Vector<?> catalogEntries,
			Vector<Float> scoreValues) {
		super(tid, catalogEntries);
		this.scoreValues = scoreValues;
	}

	public Vector<Float> getScores() {
		return scoreValues;
	}

	@Override
	public double computeBytes() {
		double counter = 0;
		counter += super.computeBytes();
		counter += Float.SIZE * scoreValues.size();

		return counter;
	}
}
