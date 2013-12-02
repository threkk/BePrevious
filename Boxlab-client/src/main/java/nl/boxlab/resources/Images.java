package nl.boxlab.resources;

import java.net.URL;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Images {

	private static final Logger logger = LoggerFactory.getLogger(Images.class);

	// 32x32 icons
	public static final ImageIcon ICONS_APPROVE = safeLoad("icons/32x32/Approve.png");
	public static final ImageIcon ICONS_CANCEL = safeLoad("icons/32x32/Cancel.png");
	public static final ImageIcon ICONS_SAVE = safeLoad("icons/32x32/Save.png");

	// 16x16 icons
	public static final ImageIcon ICONS_ADD_SMAL = safeLoad("icons/16x16/Add.png");
	public static final ImageIcon ICONS_APPROVE_SMALL = safeLoad("icons/16x16/Approve.png");
	public static final ImageIcon ICONS_CANCEL_SMALL = safeLoad("icons/16x16/Cancel.png");
	public static final ImageIcon ICONS_DELETE_SMALL = safeLoad("icons/16x16/Cross.png");
	public static final ImageIcon ICONS_EDIT_SMALL = safeLoad("icons/16x16/Edit.png");
	public static final ImageIcon ICONS_FORWARD_2_SMALL = safeLoad("icons/16x16/Forward-2.png");
	public static final ImageIcon ICONS_FORWARD_SMALL = safeLoad("icons/16x16/Forward.png");
	public static final ImageIcon ICONS_GRAPH_SMALL = safeLoad("icons/16x16/Graph.png");
	public static final ImageIcon ICONS_MESSAGE_SMALL = safeLoad("icons/16x16/Message.png");
	public static final ImageIcon ICONS_PREVIOUS_2_SMALL = safeLoad("icons/16x16/Previous-2.png");
	public static final ImageIcon ICONS_PREVIOUS_SMALL = safeLoad("icons/16x16/Previous.png");

	// larger images
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
