package ceid.netcins.json;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.social.TagCloud;

public class TagCloudJSONConvertor extends ContentFieldJSONConvertor {

	public TagCloudJSONConvertor() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object fromJSON(Map arg0) {
		TreeMap<String, Integer> tm = new TreeMap<String, Integer>();
		for (Object key : arg0.keySet())
			tm.put((String)key, (Integer)arg0.get(key));
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
