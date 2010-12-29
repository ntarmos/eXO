package ceid.netcins.exo.frontend.json;

import java.util.Map;
import java.util.Vector;

import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.exo.catalog.ScoreBoard;
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
public class ScoreBoardJSONConvertor extends ContentFieldJSONConvertor {
	private static final String ScoreDataTag = "eXO::ScoreBoard";

	public ScoreBoardJSONConvertor() {
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
		Vector<Score> scores = ((ScoreBoard)arg0).getAllEntries();
		arg1.add(ScoreDataTag, scores);
	}
}
