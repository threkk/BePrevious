package nl.boxlab.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalProperties {

	private static final Logger logger = LoggerFactory.getLogger(LocalProperties.class);

	private Properties properties;

	private LocalProperties() {
		try {
			this.properties = new Properties();
			InputStream in = LocalProperties.class.getResourceAsStream("/properties.properties");
			if (in == null) {
				throw new FileNotFoundException("could not find properties file");
			}
			this.properties.load(in);
		} catch (IOException e) {
			logger.error("failed to load local properties", e);
		}
	}

	private static LocalProperties instance = new LocalProperties();

	public static String getProperty(String key) {
		return instance.properties.getProperty(key);
	}

	public static void main(String[] args) {
		System.out.println(LocalProperties.getProperty("host"));
	}
}
