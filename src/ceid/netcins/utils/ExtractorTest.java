package ceid.netcins.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gnu.libextractor.Extractor;
import org.gnu.libextractor.MetaData;

public class ExtractorTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		File f = new File(args[0]);
			if (!f.exists() || f.isDirectory() || !f.canRead()) {
				System.err.println("Bailing out");
				return;
			}
			Extractor ex = Extractor.getDefault();
			Map<String, String> tempcontainer = new HashMap<String, String>();
			ArrayList<MetaData> keywords = ex.extract(f.getAbsolutePath());
			for (MetaData md : keywords) {
				tempcontainer.put(md.getTypeAsString(), md.getMetaDataAsString());
			}

			System.out.println("Keys:");
			for (String k : tempcontainer.keySet())
				System.out.println("\t" + k);
			System.out.println("Values:");
			for (String v : tempcontainer.values())
				System.out.println("\t" + v);
	}
}