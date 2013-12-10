package nl.boxlab;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import nl.boxlab.controller.library.PatientLibraryController;
import nl.boxlab.controller.login.LoginController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
	}

	private static void startApplication(ClientContext context) {
		PatientLibraryController library = new PatientLibraryController(context);
		library.showView();
	}

	public static void main(String[] args) {
		AbstractApplicationContext ctx =
		        new ClassPathXmlApplicationContext("application-context.xml");
		ClientContext context = ctx.getBean("context", ClientContext.class);

		initLaF();
		authenticate(context);
		if (context.isAuthenticated()) {
			try {
				startApplication(context);
			} catch (Exception ex) {
				String localizedMessage = ex.getLocalizedMessage();
				String message = "Failed to start the application\n";
				logger.error(message, ex);
				if (localizedMessage != null) {
					message += localizedMessage;
				}
				JOptionPane.showMessageDialog(null, message,
				        "An exception occured", JOptionPane.ERROR_MESSAGE);
				ctx.close();
			}
		} else {
			logger.debug("user is not authenticated, shutting down");
		}
	}

}
