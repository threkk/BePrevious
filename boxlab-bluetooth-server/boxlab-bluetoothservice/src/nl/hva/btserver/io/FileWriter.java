package nl.hva.btserver.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import nl.boxlab.model.Entity;
import nl.boxlab.model.serializer.JSONEntitySerializer;

public class FileWriter {

	private final JSONEntitySerializer serializer;

	public FileWriter() {
		this.serializer = new JSONEntitySerializer();
	}

	public <T extends Entity> void appendToFile(File file, T entity) throws IOException {
		appendToFile(file, Collections.singleton(entity));
	}

	public <T extends Entity> void appendToFile(File file, Collection<T> entities) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new java.io.FileWriter(file, true));
			Iterator<T> it = entities.iterator();
			while (it.hasNext()) {
				writer.write(serializer.serialize(it.next()));
				if (it.hasNext()) {
					writer.newLine();
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
