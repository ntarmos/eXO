package ceid.netcins.exo.frontend.json;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

import ceid.netcins.exo.content.TermField;

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
public class TermFieldJSONConvertor extends ContentFieldJSONConvertor {
	static final String FieldDataTag = "eXO::FieldData";

	public TermFieldJSONConvertor() {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object fromJSON(Map arg0) {
		return new TermField(
				(String)arg0.get(FieldNameTag),
				(String)arg0.get(FieldDataTag),
				(Boolean)arg0.get(FieldIsPublicTag)
		);
	}

	@Override
	public void toJSON(Object arg0, Output arg1) {
		if (arg0 == null) {
			arg1.add(null);
			return;
		}
		TermField tf = (TermField)arg0;
		arg1.add(FieldNameTag, tf.getFieldName());
		arg1.add(FieldDataTag, tf.getFieldData());
		arg1.add(FieldIsPublicTag, tf.isPublic());
	}
}
