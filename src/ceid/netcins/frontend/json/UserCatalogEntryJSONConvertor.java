package ceid.netcins.frontend.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
	private static final String UCETag = "eXO::UCE";
	ContentProfileJSONConvertor cpjc = new ContentProfileJSONConvertor();

	public UserCatalogEntryJSONConvertor() {
	}

	@Override
	public void toJSON(Object obj, Output out) {
		if (obj == null) {
			out.add(null);
			return;
		}
		UserCatalogEntry uce = (UserCatalogEntry)obj;
		Map<Id, ContentProfile> ret = new HashMap<Id, ContentProfile>();
		ret.put(uce.getUID(), uce.getUserProfile());
		out.add(UCETag, ret);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map object) {
		if (object == null || !object.containsKey(UCETag))
			return null;
			Map map = (Map) object.get(UCETag);
			if (map.size() > 1)
				return null;
			Id id = rice.pastry.Id.build((String)map.keySet().iterator().next());
			Set set = (Set)map.values().iterator().next();
			Iterator it = set.iterator();
			ContentProfile up = (ContentProfile)cpjc.fromJSON(new Object[] { it.next() });
			if (id == null || up == null)
				return null;
			return new UserCatalogEntry(id, up);
	}
}
