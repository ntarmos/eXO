package ceid.netcins.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * A set of common utility methods. These can be used everywhere in the project. 
 * 
 * @author Andreas Loupasakis
 *
 */
public class CommonUtils {

	/**
	 * Static method used to represent a Collection to String, joined in a single 
	 * String object.
	 * 
	 * @param s The collection object.
	 * @param delimiter The delimiter which is used to join the Collection items.
	 * @return The joined String representation of the Collection.
	 */
	@SuppressWarnings("rawtypes")
    public static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    /**
     * Return the joined string representation of the strings array.
     * 
     * @param strings
     * @param delimiter
     * @return
     */
    public static String join(String[] strings, String delimiter) {
	    StringBuffer sbuffer = new StringBuffer();
	    for (int i=0; i < strings.length; i++) {
	        if (i != 0) sbuffer.append(delimiter);
	  	    sbuffer.append(strings[i]);
	  	}
	  	return sbuffer.toString();
    }

}
