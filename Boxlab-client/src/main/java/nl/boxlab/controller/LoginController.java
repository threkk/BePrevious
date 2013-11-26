package nl.boxlab.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import nl.boxlab.ClientContextImpl;
import nl.boxlab.view.LoginView;

public class LoginController implements ActionListener {

	public static final String ACTION_AUTHENTICATE = "action-authenticate";
	public static final String ACTION_DISPOSE = "action-dispose";

	private ClientContextImpl context;
	private LoginView view;
	private JDialog dialog;

	public LoginController(ClientContextImpl context) {
		this.context = context;
		this.view = new LoginView();
		this.view.setListener(this);
	}

	public void showView() {
		if (this.dialog != null && this.dialog.isVisible()) {
			this.dialog.dispose();
		}

		this.dialog = new JDialog();
		this.dialog.setModal(true);
		this.dialog.setTitle("Please enter your credentials");
		this.dialog.setContentPane(view);
		this.dialog.setAlwaysOnTop(true);
		this.dialog.pack();
		this.dialog.setResizable(false);
		this.dialog.setLocationRelativeTo(null);
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
