package nl.hva.btserver.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import nl.boxlab.model.Entity;
import nl.boxlab.model.serializer.JSONEntitySerializer;

public class FileWriter<T extends Entity> {

	private final JSONEntitySerializer serializer;
	private final File file;

	public FileWriter(File file) {
		this.serializer = new JSONEntitySerializer();
		this.file = file;
	}

	public void appendToFile(T entity) throws IOException {
		appendToFile(Collections.singleton(entity));
	}

	public void appendToFile(Collection<T> entities) throws IOException {
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
