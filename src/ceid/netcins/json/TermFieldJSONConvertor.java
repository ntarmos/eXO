package ceid.netcins.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.content.TermField;

public class TermFieldJSONConvertor extends ContentFieldJSONConvertor {
	static final String FieldDataTag = "eXO::FieldData";

	public TermFieldJSONConvertor() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object fromJSON(Map arg0) {
		return new TermField(
				(String)arg0.get(FieldNameTag),
				(String)arg0.get(FieldDataTag),
				(Boolean)arg0.get(FieldIsPublicTag)
		);
	}

	@Override
	public void toJSON(Object arg0, Output arg1) {
		if (arg0 == null) {
			arg1.add(arg0);
			return;
		}
		TermField tf = (TermField)arg0;
		arg1.add(FieldNameTag, tf.getFieldName());
		arg1.add(FieldDataTag, tf.getFieldData());
		arg1.add(FieldIsPublicTag, tf.isPublic());
	}
}
