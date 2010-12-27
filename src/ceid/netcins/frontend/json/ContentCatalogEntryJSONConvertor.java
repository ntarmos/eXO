package ceid.netcins.frontend.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.catalog.ContentCatalogEntry;
import ceid.netcins.catalog.UserCatalogEntry;
import ceid.netcins.content.ContentProfile;

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
	protected static final UserCatalogEntryJSONConvertor ucjc = new UserCatalogEntryJSONConvertor();
	protected static final ContentProfileJSONConvertor cpjc = new ContentProfileJSONConvertor();

	public ContentCatalogEntryJSONConvertor() {
	}

	@Override
	public void toJSON(Object obj, Output out) {
		if (obj == null) {
			out.add(null);
			return;
		}
		ContentCatalogEntry uce = (ContentCatalogEntry)obj;
		ucjc.toJSON(uce, out);
		cpjc.toJSON(uce.getContentProfile(), out);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map object) {
		UserCatalogEntry uce = (UserCatalogEntry)ucjc.fromJSON(object);
		ContentProfile cp = (ContentProfile)cpjc.fromJSON(object);
		return new ContentCatalogEntry(uce.getUID(), cp, uce.getUserProfile());
	}
}
