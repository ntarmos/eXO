/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.catalog;

import java.io.Serializable;
import java.util.Vector;
import rice.p2p.commonapi.Id;

/**
 * This class represents a Catalog enhanced with some scoring of the CatalogEntries
 *
 * @author andy
 * @version 1.0
 */
public class ScoreCatalog extends Catalog implements Serializable{
    
    private Vector<Float> scoreValues;

    /**
     * rows and scoreValues must be sorted appropriately in Scorer!
     * 
     * @param tid
     * @param rows
     * @param scoreValues
     */
    public ScoreCatalog(Id tid, Vector catalogEntries, Vector<Float> scoreValues){
        super(tid,catalogEntries);
        this.scoreValues = scoreValues;
    }
    
    public Vector<Float> getScores(){
        return scoreValues;
    }
    
    public double computeBytes(){
        double counter = 0;
        counter += super.computeBytes();
        counter += Float.SIZE * scoreValues.size();
        
        return counter;
    }
}
