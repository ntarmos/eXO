package ceid.netcins.json;

import org.eclipse.jetty.util.ajax.JSON.Convertor;

public abstract class ContentFieldJSONConvertor implements Convertor {
	protected static final String FieldNameTag = "eXO::FieldName";
	protected static final String FieldIsPublicTag = "eXO::FieldIsPublic";

	protected ContentFieldJSONConvertor() {
	}
}
