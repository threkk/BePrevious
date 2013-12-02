package nl.boxlab.view.messages;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import nl.boxlab.model.Message;

@SuppressWarnings("serial")
public class MessageView extends JPanel {

	private JList<Message> listMessages;
	private JButton btnSend;
	private JTextPane txtInput;

	public MessageView() {
		initComponents();
	}

	private void initComponents() {
		this.listMessages = new JList<>();
		this.txtInput = new JTextPane();
		this.btnSend = new JButton("Send!");

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(btnSend, BorderLayout.EAST);
		panel.add(new JScrollPane(txtInput), BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(new JScrollPane(listMessages));
		splitPane.setBottomComponent(panel);

		setLayout(new BorderLayout(5, 5));
		add(splitPane, BorderLayout.CENTER);
	}

}
