package nl.boxlab.controller.message;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;

import nl.boxlab.ClientContext;
import nl.boxlab.DateUtilities;
import nl.boxlab.MessageUtilities;
import nl.boxlab.model.Message;
import nl.boxlab.model.Patient;
import nl.boxlab.remote.MessageProvider;
import nl.boxlab.view.DialogBuilder;
import nl.boxlab.view.messages.MessageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageController implements ActionListener {

	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

	public static final int MIN_CHARACTERS = 10;

	public static final String ACTION_SEND = "send";

	private ClientContext context;
	private MessageView view;
	private JDialog dialog;

	private Patient patient;

	public MessageController(ClientContext context) {
		this.context = context;
		this.view = new MessageView();
	}

	public void showView(Component owner, Patient patient) {
		Date start = DateUtilities.addDay(new Date(), -30);
		Date end = new Date();
		MessageProvider messageProvider = this.context.getMessageProvider();
		List<Message> messages = messageProvider.getMessages(patient.getIdentification(), start, end);

		this.patient = patient;

		this.view.setMessages(messages);
		this.view.setListener(this);

		this.dialog = new DialogBuilder()
		        .setTitle("Showing patient messages")
		        .setMinimumSize(new Dimension(400, 450))
		        .setView(view)
		        .setOwner(owner).build();
		this.dialog.setVisible(true);
	}

	public void hideView() {
		this.dialog.dispose();
		this.dialog = null;
		this.view.setMessages(null);
		this.view.removeListener(this);
	}

	private void sendMessage(String text) {
		text = text.trim();
		if (text.length() < MIN_CHARACTERS) {
			MessageUtilities.showWarningMessage(this.view,
			        "A message must be at least " + MIN_CHARACTERS + " characters long");
			return;
		}

		MessageProvider messageProvider = this.context.getMessageProvider();
		Message message = new Message(text);
		message.setIdentity(this.patient.getIdentification());

		try {
			messageProvider.saveMessage(message);
			this.view.getMessages().add(message);
			this.view.clearInput();
			this.view.updateView();
		} catch (Exception e) {
			logger.error("Failed to send message", e);
			MessageUtilities.showErrorMessage(this.view, "Failed to send message", e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_SEND.equals(e.getActionCommand())) {
			try {
				sendMessage(this.view.getInput());
			} catch (Exception ex) {
				logger.error("Failed to send message", ex);
				MessageUtilities.showErrorMessage(view, "Failed to send message", ex);
			}
		}
	}
}
