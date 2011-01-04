package ceid.netcins.exo.frontend.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import rice.p2p.commonapi.Id;
import ceid.netcins.exo.catalog.ContentCatalogEntry;
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
public class ContentCatalogEntryJSONConvertor implements Convertor {
	private static final String CCETag = "eXO::CCE";
	private static final String UIDTag = "eXO::UID";
	private static final String ProfilesTag = "eXO::Profiles";
	protected ContentProfileJSONConvertor cpjc = new ContentProfileJSONConvertor();

	public ContentCatalogEntryJSONConvertor() {
	}

	@Override
	public void toJSON(Object obj, Output out) {
		if (obj == null) {
			out.add(null);
			return;
		}
		ContentCatalogEntry uce = (ContentCatalogEntry)obj;
		Vector<ContentProfile> set = new Vector<ContentProfile>();
		set.add(uce.getUserProfile());
		set.add(uce.getContentProfile());
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(UIDTag, uce.getUID());
		ret.put(ProfilesTag, set);
		out.add(CCETag, ret);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map object) {
		if (object == null || !object.containsKey(CCETag))
		return null;
		Map map = (Map)object.get(CCETag);
		if (map.size() > 1)
			return null;
		Id id = rice.pastry.Id.build((String)map.get(UIDTag));
		Vector set = (Vector)map.get(ProfilesTag);
		Iterator it = set.iterator();
		ContentProfile up = (ContentProfile)cpjc.fromJSON(new Object[] { it.next() });
		ContentProfile cp = (ContentProfile)cpjc.fromJSON(new Object[] { it.next() });
		if (id == null || up == null || cp == null)
			return null;
		return new ContentCatalogEntry(id, up, cp);
	}
}
