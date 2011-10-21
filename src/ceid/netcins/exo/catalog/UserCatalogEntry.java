package ceid.netcins.exo.catalog;

import ceid.netcins.exo.content.ContentField;
import ceid.netcins.exo.content.ContentProfile;
import rice.p2p.commonapi.Id;

import java.io.Serializable;
import java.util.Set;

/**
 * An extension of the CatalogEntry which is used to offer user indexing
 * functionality
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
@SuppressWarnings("rawtypes")
public class UserCatalogEntry extends CatalogEntry implements Serializable,
        Comparable {

    private static final long serialVersionUID = 7850151060272447739L;
    // The profile of the user we want to have indexed.
    private ContentProfile userProfile;

    public UserCatalogEntry(Id uid, ContentProfile usrp) {
        super(uid);
        this.userProfile = usrp;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof UserCatalogEntry &&
                super.equals((CatalogEntry) o) &&
                userProfile.equals(((UserCatalogEntry) o).userProfile));
    }

    @Override
    public int hashCode() {
        return super.hashCode() + userProfile.hashCode();
    }

    /**
     * Getter for the userProfile
     *
     * @return userProfile
     */
    public ContentProfile getUserProfile() {
        return this.userProfile;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("UserCatalog Entry : [UID] = ");
        buf.append(getUID().toString());
        buf.append("[userProfile] = ");
        if (userProfile == null)
            buf.append("null");
        else
            buf.append(userProfile.toString());
        return buf.toString();
    }

    /**
     * @return A sum of the UserCatalogEntry data in bytes
     */
    public double computeTotalBytes() {
        double counter = 0;
        counter += getUID().getByteArrayLength();
        if (userProfile != null)
            counter += userProfile.computeTotalBytes();

        return counter;
    }

    @Override
    public CatalogEntry add(CatalogEntry additions) {
        if (additions != null && (
                !(additions instanceof UserCatalogEntry) ||
                        !(getUID().equals(additions.getUID())))
                ) {
            return null;
        }
        Set<ContentField> add = null;
        ContentProfile addcp = null;
        if (additions == null || (addcp = ((UserCatalogEntry) additions).getUserProfile()) == null ||
                (add = addcp.getAllFields()).size() == 0)
            return this;
        if (userProfile == null)
            userProfile = new ContentProfile(add);
        else
            for (ContentField cf : add)
                userProfile.add(cf);
        return this;
    }

    @Override
    public CatalogEntry subtract(CatalogEntry deletions) {
        if (deletions != null && (
                !(deletions instanceof UserCatalogEntry) ||
                        !(getUID().equals(deletions.getUID())))
                ) {
            return null;
        }
        Set<ContentField> del = null;
        ContentProfile delcp = null;
        if (deletions == null || (delcp = ((UserCatalogEntry) deletions).getUserProfile()) == null ||
                (del = delcp.getAllFields()) == null)
            return this;
        if (userProfile == null)
            return this;
        for (ContentField cf : del)
            userProfile.remove(cf);
        return this;
    }
}
