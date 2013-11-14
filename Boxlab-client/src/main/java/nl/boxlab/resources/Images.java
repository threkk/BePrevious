package nl.boxlab.resources;

import java.net.URL;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Images {

	private static final Logger logger = LoggerFactory.getLogger(Images.class);

	public static final ImageIcon IMAGE_LOGO_LARGE = safeLoad("images/boxlab_logo_large.png");
	public static final ImageIcon IMAGE_LOGO_MEDIUM = safeLoad("images/boxlab_logo_medium.png");

	public static ImageIcon safeLoad(String resourcename) {
		try {
			ClassLoader loader = Images.class.getClassLoader();
			URL resource = loader.getResource(resourcename);
			return new ImageIcon(resource);
		} catch (Throwable ball) {
			logger.warn("Failed to load image {}", resourcename);
			return null;
		}
	}
}
