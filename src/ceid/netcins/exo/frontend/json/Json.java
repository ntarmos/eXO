package ceid.netcins.exo.frontend.json;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON;

import ceid.netcins.exo.catalog.CatalogEntry;
import ceid.netcins.exo.catalog.ContentCatalogEntry;
import ceid.netcins.exo.catalog.ScoreBoard;
import ceid.netcins.exo.catalog.ScoreBoard.Score;
import ceid.netcins.exo.catalog.UserCatalogEntry;
import ceid.netcins.exo.content.ContentProfile;
import ceid.netcins.exo.content.StoredField;
import ceid.netcins.exo.content.TermField;
import ceid.netcins.exo.content.TokenizedField;
import ceid.netcins.exo.social.TagCloud;
import ceid.netcins.exo.user.Friend;
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
public class Json extends JSON {
	private static final Json instance = new Json();

	private Json() {
		JSON.registerConvertor(TermField.class, new TermFieldJSONConvertor());
		JSON.registerConvertor(StoredField.class, new StoredFieldJSONConvertor());
		JSON.registerConvertor(TokenizedField.class, new TokenizedFieldJSONConvertor());
		JSON.registerConvertor(ContentProfile.class, new ContentProfileJSONConvertor());
		JSON.registerConvertor(CatalogEntry.class, new CatalogEntryJSONConvertor());
		JSON.registerConvertor(UserCatalogEntry.class, new UserCatalogEntryJSONConvertor());
		JSON.registerConvertor(ContentCatalogEntry.class, new ContentCatalogEntryJSONConvertor());
		JSON.registerConvertor(TagCloud.class, new TagCloudJSONConvertor());
		JSON.registerConvertor(rice.p2p.commonapi.Id.class, new IdJSONConvertor());
		JSON.registerConvertor(rice.pastry.Id.class, new IdJSONConvertor());
		JSON.registerConvertor(ScoreBoard.class, new ScoreBoardJSONConvertor());
		JSON.registerConvertor(Score.class, new ScoreJSONConvertor());
		JSON.registerConvertor(SharedContentItem.class, new SharedContentItemJSONConvertor());
		JSON.registerConvertor(Friend.class, new FriendJSONConvertor());
	}

	public static void init() {
		// Force singleton initialization
	}

	public static Json getInstance() {
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jetty.util.ajax.JSON#appendMap(java.lang.StringBuffer, java.util.Map)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void appendMap(StringBuffer buffer, Map object) {
		// Overriding Jetty {@link org.eclipse.jetty.util.ajax.JSON}'s appendMap(...) 
		// method to let it use Convertors registered for key values.
		// XXX: should probably also submit upsteram to Jetty
		if (object==null) {
			appendNull(buffer);
			return;
		}

		buffer.append('{');
		Iterator iter=object.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry=(Map.Entry)iter.next();
			Convertor convertor=getConvertor(entry.getKey().getClass());
			if (convertor != null)
				appendJSON(buffer,convertor,entry.getKey());
			else
				appendString(buffer,entry.getKey().toString());
			buffer.append(':');
			append(buffer,entry.getValue());
			if (iter.hasNext())
				buffer.append(',');
		}
		buffer.append('}');
	}

	/*
	 * Hide JSON.toString(Object)
	 */
    public static String toString(Object object)
    {
        StringBuffer buffer=new StringBuffer(DEFAULT.getStringBufferSize());
        synchronized (buffer)
        {
            DEFAULT.append(buffer,object);
            return buffer.toString();
        }
    }

	/*
	 * Hide JSON.toString(Object[])
	 */
    public static String toString(Object[] array)
    {
        StringBuffer buffer=new StringBuffer(instance.getStringBufferSize());
        synchronized (buffer)
        {
            instance.appendArray(buffer,array);
            return buffer.toString();
        }
    }

    /*
     * Hide JSON.toString(Map)
     */
	@SuppressWarnings("rawtypes")
	public static String toString(Map object)
	{
		StringBuffer buffer=new StringBuffer(instance.getStringBufferSize());
		synchronized (buffer)
		{
			instance.appendMap(buffer,object);
			return buffer.toString();
		}
	}
}
