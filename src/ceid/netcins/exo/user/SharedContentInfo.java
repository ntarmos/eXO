package ceid.netcins.exo.user;

import java.io.File;
import java.io.Serializable;

import ceid.netcins.exo.content.ContentProfile;

public class SharedContentInfo implements Serializable {
	private static final long serialVersionUID = -7895565013129977341L;
	private File file;
	private String filename;
	private ContentProfile profile;

	SharedContentInfo(File file, String filename, ContentProfile profile) {
		this.file = file;
		this.filename = filename;
		this.profile = profile;
	}

	public String getFilename() {
		if (filename != null)
			return filename;
		if (file != null)
			return file.getName();
		return null;
	}

	public void setFilename(String fname) {
		this.filename = fname;
	}

	public ContentProfile getProfile() {
		return profile;
	}

	public void setProfile(ContentProfile cp) {
		this.profile = cp;
	}
}