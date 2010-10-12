/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.similarity;

/**
 * Represents a simple binary weight!      
 * 1 = term is present, 0 = term not present in document
 * 
 * @author andy
 * @version 1.0
 */
public class BinaryWeight extends TermWeight{
    
    public BinaryWeight(String term){
        super(term);
    }

    /**
     * Always returns 1 (as the rem is presented)
     * when this method is called with a term in our set!
     * 
     * @return
     */
    @Override
    public float getWeight() {
        return 1;
    }
    
}
