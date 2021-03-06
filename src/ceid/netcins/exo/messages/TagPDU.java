package ceid.netcins.exo.messages;

import ceid.netcins.exo.content.ContentField;
import ceid.netcins.exo.content.ContentProfile;
import rice.p2p.commonapi.Id;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class contains the data (tags,contentId,UserId) for the social tagging
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class TagPDU implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2339647551267755148L;

    // The Id of the tagged entity
    private Id taggedId;

    private ContentProfile tags;

    public TagPDU(Id taggedId, ContentProfile tags) {
        this.taggedId = taggedId;
        this.tags = tags;
    }

    public TagPDU(Id taggedId, ContentField[] tags) {

        this.taggedId = taggedId;
        this.tags = new ContentProfile(Arrays.asList(tags));
    }

    /**
     * Getter for tags
     *
     * @return
     */
    public ContentProfile getTags() {
        return tags;
    }

    /**
     * Getter for the contentId
     *
     * @return
     */
    public Id getTaggedId() {
        return taggedId;
    }
}
