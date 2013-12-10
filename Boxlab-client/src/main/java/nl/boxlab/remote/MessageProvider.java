package nl.boxlab.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.boxlab.model.Message;

public class MessageProvider extends AbstractProvider {

	public static final String PATH_MESSAGES = "/%s/messages";

	public List<Message> getMessages(String identification, Date start, Date end) {
		String path = String.format(PATH_MESSAGES, identification);
		Map<String, Long> queryParams = new HashMap<>();
		queryParams.put("from", start.getTime());
		queryParams.put("to", end.getTime());

		String response = getClient().get(path, queryParams);
		Message[] messages = getSerializer().deserializeArray(Message[].class, response);

		return new ArrayList<>(Arrays.asList(messages));
	}

	public void saveMessage(Message message) {
		String path = String.format(PATH_MESSAGES, message.getIdentity());
		String body = getSerializer().serialize(message);
		getClient().post(path, body);
	}
}
