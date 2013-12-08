package nl.boxlab.view.messages;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import nl.boxlab.controller.message.MessageController;
import nl.boxlab.model.Message;

@SuppressWarnings("serial")
public class MessageView extends JPanel {

	private JList<Message> listMessages;
	private JButton btnSend;
	private JTextPane txtInput;

	private List<Message> messages;

	public MessageView() {
		initComponents();
	}

	private void initComponents() {
		this.listMessages = new JList<>();
		this.listMessages.setCellRenderer(new MessageListCellRenderer());
		this.txtInput = new JTextPane();
		this.btnSend = new JButton("Send!");
		this.btnSend.setActionCommand(MessageController.ACTION_SEND);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(btnSend, BorderLayout.EAST);
		panel.add(new JScrollPane(txtInput), BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setResizeWeight(0.80);
		splitPane.setTopComponent(new JScrollPane(listMessages));
		splitPane.setBottomComponent(panel);

		setLayout(new BorderLayout(5, 5));
		add(splitPane, BorderLayout.CENTER);
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
		updateView();
	}

	public void setListener(MessageController listener) {
		this.btnSend.addActionListener(listener);
	}

	public void removeListener(MessageController listener) {
		this.btnSend.removeActionListener(listener);
	}

	public void clearInput() {
		this.txtInput.setText("");
	}

	public String getInput() {
		return this.txtInput.getText();
	}

	public void updateView() {
		DefaultListModel<Message> model = new DefaultListModel<Message>();
		for (Message message : messages) {
			model.addElement(message);
		}
		this.listMessages.setModel(model);

		this.listMessages.ensureIndexIsVisible(model.getSize() - 1);
	}
}
