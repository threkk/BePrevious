package nl.hva.btserver.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import nl.boxlab.model.Entity;
import nl.boxlab.model.serializer.JSONEntitySerializer;

public class FileReader<T extends Entity> {

	private final JSONEntitySerializer serializer;
	private final File file;
	private final Class<T> clazz;

	public FileReader(File file, Class<T> clazz) {
		this.serializer = new JSONEntitySerializer();
		this.file = file;
		this.clazz = clazz;
	}

	public List<T> readFromFile() throws IOException {
		List<T> result = new LinkedList<T>();

		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while ((line = reader.readLine()) != null) {
				result.add(serializer.deserialize(clazz, line));
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return result;
	}
}
