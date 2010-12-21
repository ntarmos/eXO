package ceid.netcins.json;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.social.TagCloud;

public class TagCloudJSONConvertor extends ContentFieldJSONConvertor {

	public TagCloudJSONConvertor() {
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object fromJSON(Map arg0) {
		if (arg0 == null)
			return null;
		TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
		Iterator<Object> key = arg0.keySet().iterator();
		Iterator<Object> value = arg0.values().iterator();
		while (key.hasNext()) {
			String k = (String)key.next();
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
		Map<String, Integer> tm = tc.getTagTFMap();
		arg1.add(tm);
	}
}
