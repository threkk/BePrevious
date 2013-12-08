package nl.boxlab.controller.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import nl.boxlab.ClientContext;
import nl.boxlab.view.DialogBuilder;
import nl.boxlab.view.login.LoginView;

public class LoginController implements ActionListener {

	public static final String ACTION_AUTHENTICATE = "action-authenticate";
	public static final String ACTION_DISPOSE = "action-dispose";

	private ClientContext context;
	private LoginView view;
	private JDialog dialog;

	public LoginController(ClientContext context) {
		this.context = context;
		this.view = new LoginView();
		this.view.setListener(this);
	}

	public void showView() {
		if (this.dialog != null && this.dialog.isVisible()) {
			this.dialog.dispose();
		}

		this.dialog = new DialogBuilder()
		        .setTitle("Please enter your credentials")
		        .setView(view)
		        .setAlwaysOnTop(true)
		        .setResizable(false).build();
		this.dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (ACTION_AUTHENTICATE.equals(command)) {
			String username = this.view.getUsername();
			char[] password = this.view.getPassword();

			this.context.authenticate(username, password);
			if (!this.context.isAuthenticated()) {
				this.view.setErrorMessage("Invalid username "
				        + "or password combination");
				this.view.clearPasswordField();
			} else {
				this.dialog.dispose();
			}
		} else if (ACTION_DISPOSE.equals(command)) {
			if (this.dialog != null) {
				this.dialog.dispose();
				this.dialog = null;
			}
		}
	}
}
