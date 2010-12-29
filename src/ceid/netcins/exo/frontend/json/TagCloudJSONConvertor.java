package ceid.netcins.exo.frontend.json;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.exo.content.ContentField;
import ceid.netcins.exo.social.TagCloud;

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
public class TagCloudJSONConvertor extends ContentFieldJSONConvertor {

	public TagCloudJSONConvertor() {
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object fromJSON(Map arg0) {
		if (arg0 == null)
			return null;
		Hashtable<ContentField, Integer> tm = new Hashtable<ContentField, Integer>();
		Iterator<Object> key = arg0.keySet().iterator();
		Iterator<Object> value = arg0.values().iterator();
		while (key.hasNext()) {
			ContentField k = (ContentField)key.next();
			Integer v = (Integer)value.next();
			tm.put(k, v);
		}
		return new TagCloud(tm);
	}

	@Override
	public void toJSON(Object arg0, Output arg1) {
		if (arg0 == null) {
			arg1.add(null);
			return;
		}
		TagCloud tc = (TagCloud)arg0;
		Map<ContentField, Integer> tm = tc.getTagTFMap();
		arg1.add(tm);
	}
}
