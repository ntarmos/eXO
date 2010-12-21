package ceid.netcins.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.content.ContentField;
import ceid.netcins.content.ContentProfile;

public class ContentProfileJSONConvertor implements Convertor {
	public static final String ProfileTag = "eXO::Profile";

	public ContentProfileJSONConvertor() {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map arg0) {
		Object[] content = (Object[])arg0.get(ProfileTag);
		ContentProfile ret = new ContentProfile();
		if (content != null)
			for (Object cf : content) {
				if (cf instanceof Map) {
					ContentFieldJSONConvertor tc = null;
					if (((Map)cf).containsKey(TermFieldJSONConvertor.FieldDataTag))
						tc = new TermFieldJSONConvertor();
					else
						tc = new TokenizedFieldJSONConvertor();
					ret.add((ContentField)tc.fromJSON((Map)cf));
				}
			}
		return ret;
	}

	@SuppressWarnings("rawtypes")
	public Object fromJSON(Object[] arg0) {
		ContentProfile ret = new ContentProfile();
		if (arg0 != null)
			for (Object cf : arg0) {
				if (cf instanceof Map) {
					ContentFieldJSONConvertor tc = null;
					if (((Map)cf).containsKey(TermFieldJSONConvertor.FieldDataTag))
						tc = new TermFieldJSONConvertor();
					else
						tc = new TokenizedFieldJSONConvertor();
					ret.add((ContentField)tc.fromJSON((Map)cf));
				}
			}
		return ret;
	}

	
	@Override
	public void toJSON(Object arg0, Output arg1) {
		if (arg0 == null)
			arg1.add(null);
		else
			arg1.add(ProfileTag, ((ContentProfile)arg0).getAllFields());
	}
}
