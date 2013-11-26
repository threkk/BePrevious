package nl.boxlab.remote;

import nl.boxlab.resources.LocalProperties;

public class ClientFactory {

	public BoxlabClient createClient() {
		String host = LocalProperties.getProperty("host");
		int port = Integer.parseInt(LocalProperties.getProperty("port"));
		return new BoxlabClientImpl(host, port);
	}

}
