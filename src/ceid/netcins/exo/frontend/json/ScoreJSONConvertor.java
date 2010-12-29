package ceid.netcins.exo.frontend.json;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.exo.catalog.ScoreBoard.Score;

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
public class ScoreJSONConvertor extends ContentFieldJSONConvertor {
	private static final String ScoreEntryTag = "eXO::Entry";
	private static final String ScoreValueTag = "eXO::Score";

	public ScoreJSONConvertor() {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map arg0) {
		throw new RuntimeException("The thing that shouldn't be!");
	}

	@Override
	public void toJSON(Object arg0, Output arg1) {
		if (arg0 == null) {
			arg1.add(null);
			return;
		}
		Score score = (Score)arg0;
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put(ScoreEntryTag, score.getEntry());
		ret.put(ScoreValueTag, score.getScore());
		arg1.add(ret);
	}
}
