package nl.boxlab.view.messages;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import nl.boxlab.model.Message;

public class MessageListCellRenderer implements ListCellRenderer<Message> {

	public static final Color COLOR_MESSAGE_PATIENT = new Color(175, 255, 255);
	public static final Color COLOR_MESSAGE_PROFFESIONAL = Color.LIGHT_GRAY;

	private TextBubblePanel panel;

	public MessageListCellRenderer() {
		this.panel = new TextBubblePanel();
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Message> list, Message message, int index,
	        boolean isSelected, boolean cellHasFocus) {
		this.panel.updateMessage(message);

		if (message.isFromPatient()) {
			this.panel.setBackground(COLOR_MESSAGE_PATIENT);
		} else {
			this.panel.setBackground(COLOR_MESSAGE_PROFFESIONAL);
		}

		if (isSelected) {
			this.panel.setBackground(this.panel.getBackground().brighter());
		}

		return panel;
	}
}
