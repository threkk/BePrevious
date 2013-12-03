package nl.boxlab;

import java.awt.Component;

import javax.swing.JOptionPane;

public class MessageUtilities {

	public static void showErrorMessage(String message, Throwable ball) {
		showErrorMessage(null, message, ball);
	}

	public static void showErrorMessage(Component owner, String message, Throwable ball) {
		String title = "An error occured";
		int type = JOptionPane.ERROR_MESSAGE;

		StringBuilder sb = new StringBuilder();
		sb.append(message).append("\n");
		if (ball.getLocalizedMessage() != null) {
			sb.append(ball.getLocalizedMessage());
		}

		JOptionPane.showMessageDialog(owner, sb.toString(), title, type);
	}
}
