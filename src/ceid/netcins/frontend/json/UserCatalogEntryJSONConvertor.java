package ceid.netcins.frontend.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import rice.p2p.commonapi.Id;
import ceid.netcins.catalog.UserCatalogEntry;
import ceid.netcins.content.ContentProfile;

/**
 * 
 * @author <a href="mailto:loupasak@ceid.upatras.gr">Andreas Loupasakis</a>
 * @author <a href="mailto:ntarmos@cs.uoi.gr">Nikos Ntarmos</a>
 * @author <a href="mailto:peter@ceid.upatras.gr">Peter Triantafillou</a>
 * 
 * "eXO: Decentralized Autonomous Scalable Social Networking"
 * Proc. 5th Biennial Conf. on Innovative Data Systems Research (CIDR),
 * January 9-12, 2011, Asilomar, California, USA.
 * 
 */
public class UserCatalogEntryJSONConvertor implements Convertor {
	protected static final CatalogEntryJSONConvertor cejc = new CatalogEntryJSONConvertor();
	protected static final ContentProfileJSONConvertor cpjc = new ContentProfileJSONConvertor();

	public UserCatalogEntryJSONConvertor() {
	}

	@Override
	public void toJSON(Object obj, Output out) {
		if (obj == null) {
			out.add(null);
			return;
		}
		UserCatalogEntry uce = (UserCatalogEntry)obj;
		cejc.toJSON(uce, out);
		cpjc.toJSON(uce.getUserProfile(), out);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map object) {
		Id id = (Id)cejc.fromJSON(object);
		ContentProfile cp = (ContentProfile)cpjc.fromJSON(object);
		return new UserCatalogEntry(id, cp);
	}
}
