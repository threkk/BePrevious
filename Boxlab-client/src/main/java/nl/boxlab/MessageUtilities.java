package nl.boxlab;

import java.awt.Component;

import javax.swing.JOptionPane;

public class MessageUtilities {

	public static boolean showConfirmMessage(String message) {
		return showConfirmMessage(null, message);
	}

	public static boolean showConfirmMessage(Component owner, String message) {
		String title = "Please confirm";
		int options = JOptionPane.YES_NO_OPTION;
		int result = JOptionPane.showConfirmDialog(owner, message, title, options);

		return result == JOptionPane.YES_OPTION;
	}

	public static void showWarningMessage(String message) {
		showWarningMessage(null, message);
	}

	public static void showWarningMessage(Component owner, String message) {
		String title = "Warning";
		int type = JOptionPane.WARNING_MESSAGE;

		JOptionPane.showMessageDialog(owner, message, title, type);
	}

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
