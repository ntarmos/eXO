package ceid.netcins.exo.content;

import java.io.Serializable;

/**
 * The main difference of this Class in contrast with TermField is that the
 * fieldData is not going to be hashed and indexed in the corresponding Catalog
 * node. This StoredField's fieldData is just stored in a Catalog entry.
 * Examples of such ContentField are "filesize", "SHA-1", "modified" etc.
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class StoredField extends ContentField implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1163562213103678794L;
    String fieldData;

    public StoredField(String name, String fieldData, boolean isPublic) {
        super(name, isPublic);
        this.fieldData = fieldData;
    }

    public StoredField(String name, String fieldData) {
        super(name);
        this.fieldData = fieldData;
    }

    public String getFieldData() {
        return this.fieldData;
    }

    /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
    public String toString() {
        return "SF{ \"" + name + "\" : { \"" + fieldData + "\" , " + (isPublic ? "public" : "private") + " }}";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof StoredField &&
                super.equals((ContentField) o) &&
                fieldData.equals(((StoredField) o).fieldData));
    }

    @Override
    public int hashCode() {
        return super.hashCode() + fieldData.hashCode();
    }
}
