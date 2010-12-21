package ceid.netcins.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import rice.p2p.commonapi.Id;
import ceid.netcins.catalog.UserCatalogEntry;
import ceid.netcins.content.ContentProfile;

public class UserCatalogEntryJSONConvertor implements Convertor {
	protected static final CatalogEntryJSONConvertor cejc = new CatalogEntryJSONConvertor();
	protected static final ContentProfileJSONConvertor cpjc = new ContentProfileJSONConvertor();

	public UserCatalogEntryJSONConvertor() {
	}

	@Override
	public void toJSON(Object obj, Output out) {
		if (obj == null) {
			out.add(null);
			return;
		}
		UserCatalogEntry uce = (UserCatalogEntry)obj;
		cejc.toJSON(uce, out);
		cpjc.toJSON(uce.getUserProfile(), out);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map object) {
		Id id = (Id)cejc.fromJSON(object);
		ContentProfile cp = (ContentProfile)cpjc.fromJSON(object);
		return new UserCatalogEntry(id, cp);
	}
}
