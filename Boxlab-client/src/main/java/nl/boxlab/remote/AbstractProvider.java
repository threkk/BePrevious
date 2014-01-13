package nl.boxlab.remote;

import nl.boxlab.model.serializer.EntitySerializer;

public class AbstractProvider {

	private BoxlabClient client;
	private EntitySerializer serializer;

	public AbstractProvider() {

	}

	public BoxlabClient getClient() {
		return client;
	}

	public void setClient(BoxlabClient client) {
		this.client = client;
	}

	public EntitySerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(EntitySerializer serializer) {
		this.serializer = serializer;
	}
}
