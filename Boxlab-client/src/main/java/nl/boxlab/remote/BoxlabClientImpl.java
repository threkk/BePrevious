package nl.boxlab.remote;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

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

	public String get(String path, Map<String, ? extends Object> query) {
		ClientResponse response = this.apiResource
		        .queryParams(createQueryParams(query))
		        .path(path)
		        .accept("application/json")
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
		        .path(path)
		        .type("application/json")
		        .post(ClientResponse.class, body);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			        + response.getStatus());
		}
	}

	@Override
	public void delete(String path, String id) {
		ClientResponse response = this.apiResource
				.path(path)
				.path(id)
		        .delete(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			        + response.getStatus());
		}
	}

	private MultivaluedMap<String, String> createQueryParams(Map<String, ? extends Object> map) {
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (value == null) {
				continue;
			}
			queryParams.add(key, String.valueOf(value));
		}
		return queryParams;
	}

}
