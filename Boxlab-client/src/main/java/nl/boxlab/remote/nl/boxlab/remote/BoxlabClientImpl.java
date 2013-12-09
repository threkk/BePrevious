package nl.boxlab.remote;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class BoxlabClientImpl implements BoxlabClient {

	private String host;
	private int port;

	private WebResource apiResource;

	public BoxlabClientImpl(String host, int port) {
		this.host = host;
		this.port = port;

		Client client = Client.create();
		String api = String.format("http://%s:%d/boxlab/api", host, port);

		this.apiResource = client.resource(api);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String get(String path) {
		ClientResponse response = this.apiResource
		        .path(path).accept("application/json")
		        .get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			        + response.getStatus());
		}

		return response.getEntity(String.class);
	}

	@Override
	public void post(String path, String body) {
		ClientResponse response = this.apiResource
		        .path(path).type("application/json").post(ClientResponse.class, body);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			        + response.getStatus());
		}
	}

}
