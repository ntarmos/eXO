package ceid.netcins.frontend;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.jetty.server.handler.AbstractHandler;
import ceid.netcins.CatalogService;

public abstract class CatalogFrontendAbstractHandler extends AbstractHandler {
	public static final String ReqIDTag = "eXO::reqID";
	public static final String UIDTag = "eXO::UID";
	public static final String PostParamTag = "eXO_data";
	public static final String RequestStatusTag = "eXO::Status";
	public static final String RequestStatusSuccessTag = "eXO::Success";
	public static final String RequestStatusFailureTag = "eXO::Failure";
	public static final String RequestStatusProcessingTag = "eXO::Processing";

	protected CatalogService catalogService = null;
	protected Hashtable<String, Vector<String>> queue = null;

	public CatalogFrontendAbstractHandler(CatalogService catalogService, Hashtable<String, Vector<String>> queue) {
		this.catalogService = catalogService;
		this.queue = queue;
	}
	
	public static String streamToString(java.io.InputStream is)
	{
		java.io.InputStreamReader ir = new java.io.InputStreamReader(is);
		java.io.BufferedReader    in = new java.io.BufferedReader(ir);

		StringBuffer buffer = new StringBuffer();
		try {
			String line = null;
			while((line=in.readLine()) != null) {
				buffer.append( line );
				buffer.append( '\n' );
			}
		} catch (IOException e) {
			return null;
		}
		return buffer.toString();
	}
}
