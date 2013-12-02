package nl.boxlab.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.boxlab.model.ExerciseEntry;

public class ExerciseEntryProvider extends AbstractProvider {

	public List<ExerciseEntry> getEntries(String identification) {
		List<ExerciseEntry> entries = new ArrayList<ExerciseEntry>();

		ExerciseEntry entryA = new ExerciseEntry();
		entryA.setDate(new Date());
		entryA.setRepetitions(Arrays.asList(10, 10, 10));

		ExerciseEntry entryB = new ExerciseEntry();
		entryB.setDone(true);
		entryB.setDate(new Date(new Date().getTime() + 86400000L * 4));
		entryB.setRepetitions(Arrays.asList(10, 10, 10));

		ExerciseEntry entryC = new ExerciseEntry();
		entryC.setDate(new Date(new Date().getTime() + 86400000L * 7));
		entryC.setRepetitions(Arrays.asList(10, 10, 10));

		entries.add(entryA);
		entries.add(entryB);
		entries.add(entryC);

		return entries;
	}

	public void save(ExerciseEntry entry) {
		// TODO Auto-generated method stub
		
	}
}
