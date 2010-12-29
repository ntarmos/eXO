package ceid.netcins.exo.frontend.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import rice.p2p.commonapi.Id;
import ceid.netcins.exo.catalog.CatalogEntry;

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
public class CatalogEntryJSONConvertor implements Convertor {
	private static final String CETag = "eXO::CE";
	protected static final IdJSONConvertor idjc = new IdJSONConvertor();
	
	public CatalogEntryJSONConvertor() {
	}

	@Override
	public void toJSON(Object obj, Output out) {
		if (obj == null) {
			out.add(null);
			return;
		}
		out.add(CETag, ((CatalogEntry)obj).getUID());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map object) {
		if (object == null || !object.containsKey(CETag))
			return null;
		Id id = rice.pastry.Id.build((String)object.get(CETag));
		return id;
	}
}
