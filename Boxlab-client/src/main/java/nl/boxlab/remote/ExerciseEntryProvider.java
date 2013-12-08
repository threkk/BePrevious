package nl.boxlab.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.boxlab.model.ExerciseEntry;

public class ExerciseEntryProvider extends AbstractProvider {

	public static final String PATH_MESSAGES = "/%s/entries";

	public List<ExerciseEntry> getEntries(String identification, Date start, Date end) {
		String path = String.format(PATH_MESSAGES, identification);
		Map<String, Long> queryParams = new HashMap<>();
		queryParams.put("from", start.getTime());
		queryParams.put("to", end.getTime());

		String response = getClient().get(path, queryParams);
		ExerciseEntry[] messages = getSerializer().deserializeArray(ExerciseEntry[].class, response);

		return new ArrayList<>(Arrays.asList(messages));
	}

	public void save(ExerciseEntry entry) {
		String path = String.format(PATH_MESSAGES, entry.getIdentification());
		String body = getSerializer().serialize(entry);
		getClient().post(path, body);
	}

	public void delete(ExerciseEntry entry) {
		String path = String.format(PATH_MESSAGES, entry.getIdentification());
		String id = entry.getId();
		getClient().delete(path, id);
	}
}
