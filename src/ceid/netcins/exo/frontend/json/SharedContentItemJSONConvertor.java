package ceid.netcins.exo.frontend.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Convertor;
import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.exo.user.User.SharedContentItem;

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
public class SharedContentItemJSONConvertor implements Convertor {
	private static final String FilenameTag = "eXO::Filename";
	protected ContentProfileJSONConvertor cpjc = new ContentProfileJSONConvertor();

	public SharedContentItemJSONConvertor() {
	}

	@Override
	public void toJSON(Object obj, Output out) {
		if (obj == null || !(obj instanceof SharedContentItem)) {
			out.add(null);
			return;
		}
		SharedContentItem sci = (SharedContentItem)obj;
		out.add(FilenameTag, sci.getFilename());
		cpjc.toJSON(sci.getProfile(), out);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map object) {
		return null;
	}
}
