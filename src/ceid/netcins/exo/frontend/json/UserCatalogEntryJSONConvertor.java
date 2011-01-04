package ceid.netcins.exo.frontend.json;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import rice.p2p.commonapi.Id;
import ceid.netcins.exo.catalog.UserCatalogEntry;
import ceid.netcins.exo.content.ContentProfile;

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
	private static final String UIDTag = "eXO::UID";
	private static final String ProfilesTag = "eXO::Profiles";
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
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(UIDTag, uce.getUID());
		ret.put(ProfilesTag, uce.getUserProfile());
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
			Id id = rice.pastry.Id.build((String)map.get(UIDTag));
			ContentProfile up = (ContentProfile)cpjc.fromJSON(new Object[] { map.get(ProfilesTag)});
			if (id == null || up == null)
				return null;
			return new UserCatalogEntry(id, up);
	}
}
