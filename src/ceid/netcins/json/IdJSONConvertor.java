package ceid.netcins.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import rice.pastry.Id;

public class IdJSONConvertor implements Convertor {
	public static final String IDTag = "eXO::ID";

	public IdJSONConvertor() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object fromJSON(Map arg0) {
		return Id.build((String)arg0.get(IDTag));
	}

	@Override
	public void toJSON(Object arg0, Output arg1) {
		arg1.add(IDTag, ((Id)arg0).toStringFull());
	}
}
