package ceid.netcins.exo.user;

import java.io.File;

import ceid.netcins.exo.content.ContentProfile;

public class SharedContentInfo {
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