package nl.boxlab.remote;

import java.util.Arrays;
import java.util.List;

import nl.boxlab.model.Message;

public class MessageProvider extends AbstractProvider {

	public static final String PATH_MESSAGES = "/%s/messages";

	public List<Message> getMessages(String identification) {
		String path = String.format(PATH_MESSAGES, identification);
		String response = getClient().get(path);
		Message[] messages = getSerializer().deserializeArray(Message[].class, response);

		return Arrays.asList(messages);
	}

	public void saveMessage(Message message) {
		String path = String.format(PATH_MESSAGES, message.getIdentity());
		String body = getSerializer().serialize(message);
		getClient().post(path, body);
	}
}
