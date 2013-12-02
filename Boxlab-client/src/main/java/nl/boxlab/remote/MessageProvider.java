package nl.boxlab.remote;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.boxlab.model.Message;

public class MessageProvider extends AbstractProvider {

	public List<Message> getMessages() {
		return Arrays.asList(new Message(new Date(),
				"Ik snap er helemaal niks van"));
	}
}
