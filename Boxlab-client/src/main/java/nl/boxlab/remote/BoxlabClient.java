package nl.boxlab.remote;

public interface BoxlabClient {

	public String getHost();

	public int getPort();

	public String get(String path);

	public void post(String path, String body);
}
