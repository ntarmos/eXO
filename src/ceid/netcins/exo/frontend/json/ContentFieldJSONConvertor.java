package ceid.netcins.exo.frontend.json;

import org.eclipse.jetty.util.ajax.JSON.Convertor;

/**
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 *         <p/>
 *         "eXO: Decentralized Autonomous Scalable Social Networking"
 *         Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 *         January 9-12, 2011, Asilomar, California, USA.
 */
public abstract class ContentFieldJSONConvertor implements Convertor {
    protected static final String FieldNameTag = "eXO::FieldName";
    protected static final String FieldIsPublicTag = "eXO::FieldIsPublic";
    protected static final String StatusDateTag = "eXO::StatusDate";

    protected ContentFieldJSONConvertor() {
    }
}

