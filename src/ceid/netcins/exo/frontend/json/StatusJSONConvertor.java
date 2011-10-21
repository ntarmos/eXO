package ceid.netcins.exo.frontend.json;

import ceid.netcins.exo.content.Status;
import org.eclipse.jetty.util.ajax.JSON;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/20/11
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusJSONConvertor extends ContentFieldJSONConvertor {
    public static final String StatusTag = "eXO::UserStatus";

    public StatusJSONConvertor() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object fromJSON(final Map arg0) {

        return new Status((String) arg0.get(StatusTag), ((Long) arg0.get(StatusDateTag)), (Boolean) arg0.get(FieldIsPublicTag));
    }

    @Override
    public void toJSON(final Object arg0, final JSON.Output arg1) {
        if (arg0 == null) {
            arg1.add(null);
            return;
        }
        final Status status = (Status) arg0;
        arg1.add(StatusDateTag, status.getDate().getTime());
        arg1.add(StatusTag, status.getStatusText());
        arg1.add(FieldIsPublicTag, status.isPublic());
    }
}
