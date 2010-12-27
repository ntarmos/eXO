package ceid.netcins.frontend.json;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.content.TokenizedField;

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
public class TokenizedFieldJSONConvertor extends ContentFieldJSONConvertor {
	private static final String TermsTag = "eXO::Terms";
	private static final String TFScoresTag = "eXO::TFScores";

	public TokenizedFieldJSONConvertor() {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map arg0) {
		TreeMap<String, Integer> tfm = new TreeMap<String, Integer>();
		
		//name = (String)arg0.get(FieldNameTag);
		Object terms[] = (Object[])arg0.get(TermsTag);
		Object tf[] = (Object[])arg0.get(TFScoresTag);
		//isPublic = (Boolean)arg0.get(FieldIsPublicTag);
		if (terms != null && tf != null && terms.length == tf.length) {
			for (int i = 0; i < terms.length; i++)
				tfm.put((String)terms[i], ((Long)tf[i]).intValue());
		}

		return new TokenizedField(
				(String)arg0.get(FieldNameTag),
				tfm,
				(Boolean)arg0.get(FieldIsPublicTag)
		);
	}

	@Override
	public void toJSON(Object arg0, Output arg1) {
		if (arg0 == null) {
			arg1.add(null);
			return;
		}
		TokenizedField tf = (TokenizedField)arg0;
		arg1.add(FieldNameTag, tf.getFieldName());
		arg1.add(TermsTag, tf.getTerms());
		arg1.add(TFScoresTag, tf.getTF());
		arg1.add(FieldIsPublicTag, tf.isPublic());
	}
}