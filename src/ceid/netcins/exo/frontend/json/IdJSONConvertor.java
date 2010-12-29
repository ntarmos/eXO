package ceid.netcins.exo.frontend.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import rice.pastry.Id;

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
public class IdJSONConvertor implements Convertor {
	public static final String IdTag = "eXO::ID";

	public IdJSONConvertor() {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map arg0) {
		return null;
	}

	@Override
	public void toJSON(Object arg0, Output arg1) {
		arg1.add(((Id)arg0).toStringFull());
	}
}
