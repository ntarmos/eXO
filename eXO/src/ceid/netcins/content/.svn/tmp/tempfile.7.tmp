/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.content;

import java.io.Serializable;

/**This Content Field is also an "indexed term"
 * The only addition is just like the StoredField a String field value.
 * The main difference of this Class in contrast with StoredField is
 * that this fieldData is going to be hashed and indexed in a corresponding
 * Catalog node.
 *
 * @author andy
 */
public class TermField extends ContentField implements Serializable{
    
    String fieldData;

    public TermField(String name, String fieldData){
        super(name);
        this.fieldData = fieldData;
    }
    
    public String getFieldData(){
        return this.fieldData;
    }
}
