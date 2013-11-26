package nl.boxlab.remote;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Date;

import nl.boxlab.model.Entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JSONEntitySerializer implements EntitySerializer {

	private Gson gson;

	public JSONEntitySerializer() {
		this.gson = new GsonBuilder()
		        .excludeFieldsWithModifiers(Modifier.FINAL)
		        .registerTypeAdapter(Date.class, new DateAdapter())
		        .create();
	}

	@Override
	public <T extends Entity> T deserialize(Class<T> clazz, String input) {
		return this.gson.fromJson(input, clazz);
	}

	@Override
	public <T extends Entity> T[] deserializeArray(Class<T[]> clazz, String input) {
		return this.gson.fromJson(input, clazz);
	}

	@Override
	public <T extends Entity> String serialize(T input) {
		return this.gson.toJson(input);
	}

	@Override
	public <T extends Entity> String serializeArray(T[] input) {
		return this.gson.toJson(input);
	}

	protected class DateAdapter extends TypeAdapter<Date> {

		@Override
		public void write(JsonWriter out, Date value) throws IOException {
			if (value == null) {
				out.nullValue();
			} else {
				out.value(value.getTime());
			}
		}

		@Override
		public Date read(JsonReader in) throws IOException {
			return new Date(in.nextLong());
		}

	}
}
