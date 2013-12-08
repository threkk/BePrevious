package nl.boxlab.remote;

import java.util.Map;

public interface BoxlabClient {

	public String getHost();

	public int getPort();

	public String get(String path);

	public String get(String path, Map<String, ? extends Object> query);

	public void post(String path, String body);

	public void delete(String path, String id);
}
