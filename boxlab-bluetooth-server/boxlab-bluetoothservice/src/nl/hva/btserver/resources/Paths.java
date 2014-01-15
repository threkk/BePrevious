package nl.hva.btserver.resources;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class Paths {

	private Paths() {

	}

	private static final String BASE_DIRECTORY_NAME = "BePrevious";

	public static final File FILE_DATA_IN = resolvePath("/boxlab-server/data/data_in");
	public static final File FILE_DATA_OUT = resolvePath("/boxlab-server/data/data_out");

	public static final File FILE_MESSAGES_IN = resolvePath("/boxlab-server/data/data_in/messages.json");
	public static final File FILE_ENTRIES_IN = resolvePath("/boxlab-server/data/data_in/entries.json");

	public static final File FILE_MESSAGES_OUT = resolvePath("/boxlab-server/data/data_out/messages.json");
	public static final File FILE_ENTRIES_OUT = resolvePath("/boxlab-server/data/data_out/entries.json");
	public static final File FILE_FEEDBACK_OUT = resolvePath("/boxlab-server/data/data_out/feedback.json");

	private static final File resolvePath(String path) {
		ProtectionDomain domain = Paths.class.getProtectionDomain();
		CodeSource codeSource = domain.getCodeSource();
		File base = null;
		try {
			base = new File(codeSource.getLocation().toURI());
			while (base != null && !base.getName().equals(BASE_DIRECTORY_NAME)) {
				base = base.getParentFile();
			}
		} catch (URISyntaxException e) {
			System.err.println("failed to retrieve base path");
		}

		return new File(base, path);
	}
}
