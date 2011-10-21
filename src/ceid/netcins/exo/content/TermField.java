package ceid.netcins.exo.content;

import java.io.Serializable;

/**
 * This Content Field is also "indexed" in the overlay. The main difference of
 * this Class with the StoredField is that the fieldData member is going to be
 * hashed and indexed to the corresponding Catalog node.
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class TermField extends ContentField implements Serializable {

    private static final long serialVersionUID = 5325607261905749524L;
    String fieldData;

    public TermField(String name, String fieldData, Boolean isPublic) {
        super(name, isPublic);
        this.fieldData = fieldData;
    }

    public TermField(String name, String fieldData) {
        super(name);
        this.fieldData = fieldData;
    }

    public String getFieldData() {
        return this.fieldData;
    }

    /* (non-Javadoc)
      * @see ceid.netcins.content.ContentField#size()
      */
    public int size() {
        return super.size() + fieldData.getBytes().length;
    }

    /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
    public String toString() {
        return "TF{ \"" + name + "\" : { \"" + fieldData + "\" , " + (isPublic ? "public" : "private") + " }}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof TermField &&
                super.equals((ContentField) o) &&
                fieldData.equals(((TermField) o).fieldData));
    }

    @Override
    public int hashCode() {
        return super.hashCode() + fieldData.hashCode();
    }
}
