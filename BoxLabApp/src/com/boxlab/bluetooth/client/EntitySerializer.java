package com.boxlab.bluetooth.client;

import com.boxlab.bluetooth.client.Entity;

public interface EntitySerializer {

	public <T extends Entity> T deserialize(Class<T> clazz, String input);

	public <T extends Entity> T[] deserializeArray(Class<T[]> clazz, String input);

	public <T extends Entity> String serialize(T input);

	public <T extends Entity> String serializeArray(T[] input);

}
