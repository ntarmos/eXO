/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.similarity;

/**
 * Associates a weight to a specific term
 *
 * @author andy
 */
public abstract class TermWeight implements Weight{
    
    // The term of the corresponding weight
    private String term;
    
    public TermWeight(String term){
        this.term = term;
    }

    public abstract float getWeight();

    public String getWeightedObject() {
        return term;
    }

    
}
