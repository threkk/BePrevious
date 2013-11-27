package nl.boxlab.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.controller.LoginController;
import nl.boxlab.resources.Images;

@SuppressWarnings("serial")
public class LoginView extends JPanel {

	private JTextField txtUsername;
	private JPasswordField txtPassword;

	private JButton btnLogin;
	private JButton btnCancel;
	private JLabel lblErrorMessage;

	/**
	 * Create the panel.
	 */
	public LoginView() {
		initComponents();
	}

	private void initComponents() {
		FocusListener focusListener = new ClearErrorMessageListener();
		this.txtUsername = new JTextField(10);
		this.txtUsername.addFocusListener(focusListener);
		this.txtPassword = new JPasswordField(10);
		this.txtPassword.addFocusListener(focusListener);

		this.btnLogin = new JButton("Login");
		this.btnLogin.setActionCommand(LoginController.ACTION_AUTHENTICATE);
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setActionCommand(LoginController.ACTION_DISPOSE);

		this.lblErrorMessage = new JLabel(" ");
		this.lblErrorMessage.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.lblErrorMessage.setForeground(Color.RED);

		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		panel.setBackground(Color.WHITE);
		panel.add(new JLabel(Images.IMAGE_LOGO_MEDIUM, JLabel.CENTER),
				BorderLayout.CENTER);

		JPanel panelLogin = new JPanel();
		panelLogin.setBorder(BorderFactory.createTitledBorder("Login"));
		panelLogin.setLayout(new MigLayout("", "[][grow]", "[][]"));
		panelLogin.add(new JLabel("Username:"), "cell 0 0");
		panelLogin.add(txtUsername, "cell 1 0,growx");
		panelLogin.add(new JLabel("Password"), "cell 0 1");
		panelLogin.add(txtPassword, "cell 1 1,growx");

		setLayout(new MigLayout("", "[grow][grow]", "[][][][]"));
		add(panel, "cell 0 0 2 1,grow");
		add(lblErrorMessage, "cell 0 1 2 1, alignx center");
		add(panelLogin, "cell 0 2 2 1, grow");
		add(btnLogin, "flowx,cell 1 3, alignx trailing");
		add(btnCancel, "cell 1 3, alignx trailing");
	}

	public void setListener(LoginController controller) {
		this.btnLogin.addActionListener(controller);
		this.btnCancel.addActionListener(controller);
	}

	public String getUsername() {
		return this.txtUsername.getText();
	}

	public char[] getPassword() {
		return this.txtPassword.getPassword();
	}

	public void clearPasswordField() {
		this.txtPassword.setText("");
	}
	
	public void clearErrorMessage() {
		this.lblErrorMessage.setText(" ");
	}

	public void setErrorMessage(String errorMessage) {
		this.lblErrorMessage.setText(errorMessage);
	}

	protected class ClearErrorMessageListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			clearErrorMessage();
		}
	}
}
