package nl.boxlab;

import javax.swing.UIManager;

import nl.boxlab.controller.library.PatientLibraryController;
import nl.boxlab.controller.login.LoginController;
import nl.boxlab.resources.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

	private static final Logger logger = LoggerFactory
			.getLogger(Launcher.class);

	private static final void initLaF() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.error("failed to set system look and feel", e);
		}
	}

	private static void authenticate(ClientContext context) {
		LoginController loginController = new LoginController(context);
		loginController.showView();

		if (!context.isAuthenticated()) {
			logger.debug("user is not authenticated, shutting down");
			System.exit(0);
		}
	}

	private static void startApplication(ClientContext context) {
		PatientLibraryController library = new PatientLibraryController(context);
		library.showView();
	}

	public static void main(String[] args) {
		ClientContext context = new ClientContext();
		context.setHost(Constants.HOST);
		context.setPort(Constants.PORT);

		initLaF();
		authenticate(context);
		startApplication(context);
	}

}
