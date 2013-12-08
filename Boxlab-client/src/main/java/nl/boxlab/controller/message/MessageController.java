package nl.boxlab.controller.message;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;

import nl.boxlab.ClientContext;
import nl.boxlab.ClientContextImpl;
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

	public static final String ACTION_SEND = "send";

	private ClientContext context;
	private MessageView view;
	private JDialog dialog;

	private List<Message> messages;


	public MessageController(ClientContext context) {
		this.context = context;
		this.view = new MessageView();
	}

	public void showView(Component owner, Patient patient) {
		MessageProvider messageProvider = this.context.getMessageProvider();

		this.messages = messageProvider.getMessages(patient.getIdentification());
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
		MessageProvider messageProvider = this.context.getMessageProvider();
		Message message = new Message(new Date(), text, false);
		messageProvider.saveMessage(message);

		this.messages.add(message);
		this.view.updateView();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_SEND.equals(e.getActionCommand())) {
			try {
				sendMessage(this.view.getInput());
				this.view.clearInput();
			} catch (Exception ex) {
				logger.error("Failed to send message", ex);
				MessageUtilities.showErrorMessage(view, "Failed to send message", ex);
			}
		}
	}

	public static void main(String[] args) {
		ClientContextImpl context = new ClientContextImpl();
		context.setMessageProvider(new MessageProvider());

		Patient patient = new Patient();
		patient.setIdentification("1234");

		MessageController controller = new MessageController(context);
		controller.showView(null, patient);
	}
}
