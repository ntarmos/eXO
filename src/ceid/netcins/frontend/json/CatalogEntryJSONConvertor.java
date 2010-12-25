package ceid.netcins.frontend.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import rice.p2p.commonapi.Id;

import ceid.netcins.catalog.CatalogEntry;

public class CatalogEntryJSONConvertor implements Convertor {
	protected static final IdJSONConvertor idjc = new IdJSONConvertor();

	
	public CatalogEntryJSONConvertor() {
	}

	@Override
	public void toJSON(Object obj, Output out) {
		if (obj == null) {
			out.add(null);
			return;
		}
		idjc.toJSON(((CatalogEntry)obj).getUID(), out);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map object) {
		Id id = (Id)idjc.fromJSON(object);
		return id;
	}

}
