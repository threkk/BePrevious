package nl.boxlab.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nl.boxlab.ClientContext;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.model.ExerciseNote;

public class ExerciseEntryProvider extends AbstractProvider {

	public ExerciseEntryProvider(ClientContext context) {
		super(context);
	}

	public List<ExerciseEntry> getEntries(String identification) {

		List<ExerciseEntry> entries = new ArrayList<ExerciseEntry>();

		ExerciseEntry entryA = new ExerciseEntry();
		entryA.setDate(new Date());
		entryA.setRepetitions(Arrays.asList(10, 10, 10));
		entryA.setWeight(82.40);

		ExerciseEntry entryB = new ExerciseEntry();
		entryB.setDone(true);
		entryB.getNotes().add(new ExerciseNote("Ik snap de opdracht niet."));
		entryB.setDate(new Date(new Date().getTime() + 86400000L * 4));
		entryB.setRepetitions(Arrays.asList(10, 10, 10));
		entryB.setWeight(78.20);

		ExerciseEntry entryC = new ExerciseEntry();
		entryC.getNotes().add(new ExerciseNote("Ik snap de opdracht niet."));
		entryC.getNotes().add(new ExerciseNote("asdf"));
		entryC.setDate(new Date(new Date().getTime() + 86400000L * 7));
		entryC.setRepetitions(Arrays.asList(10, 10, 10));
		entryC.setWeight(78.20);

		entries.add(entryA);
		entries.add(entryB);
		entries.add(entryC);

		return entries;
	}
}
