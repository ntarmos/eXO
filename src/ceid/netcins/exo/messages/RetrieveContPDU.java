package ceid.netcins.exo.messages;

import rice.p2p.commonapi.Id;

import java.io.Serializable;

/**
 * Holds the Content Identifier.
 *
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public class RetrieveContPDU implements Serializable {

    private static final long serialVersionUID = -3861889468881922351L;

    // The content ID
    private Id contentId;

    // Flag for Tagclouds
    private boolean cloudflag;

    public RetrieveContPDU(Id contentId) {
        this.contentId = contentId;
        this.cloudflag = true;
    }

    public Id getContentId() {
        return contentId;
    }

    public boolean getCloudFlag() {
        return cloudflag;
    }
}
