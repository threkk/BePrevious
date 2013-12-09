package nl.boxlab.controller.exercise;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.boxlab.ModelUtilities;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.resources.Exercise;
import nl.boxlab.view.DialogBuilder;
import nl.boxlab.view.exercise.ExerciseView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExerciseController implements ActionListener, ItemListener,
        ChangeListener {

	private static final Logger logger = LoggerFactory
	        .getLogger(ExerciseController.class);

	public static final int MIN_SETS = 1;
	public static final int MAX_SETS = 10;
	public static final int DEFAULT_REPITITIONS = 10;

	public static final String ACTION_SAVE = "save";
	public static final String ACTION_CANCEL = "cancel";
	public static final String ACTION_ADD_SET = "add-set";
	public static final String ACTION_REMOVE_SET = "remove-set";

	private ExerciseView view;
	private JDialog dialog;
	private boolean cancelled;

	private ExerciseEntry entry;

	public ExerciseController() {
		this.view = new ExerciseView();
		this.view.setListener(this);
	}

	public void showView(Component owner, ExerciseEntry entry) {
		this.cancelled = false;
		this.entry = entry;
		this.view.setEntry(ModelUtilities.deepClone(entry));
		this.dialog = new DialogBuilder()
		        .setTitle("Showing exercise details")
		        .setView(view)
		        .setMinimumSize(new Dimension(780, 435))
		        .setOwner(owner).build();
		this.dialog.setVisible(true);
	}

	public void hideView() {
		this.dialog.dispose();
		this.dialog = null;
		this.view.setEntry(null);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	private String loadExerciseHtml(Exercise item) {
		ClassLoader cl = ExerciseController.class.getClassLoader();
		StringBuffer buffer = new StringBuffer();

		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = cl.getResourceAsStream(item.getHtmlResource());
			if (in == null) {
				throw new FileNotFoundException();
			}
			reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
		} catch (IOException e) {
			logger.error("failed to load html for exercize", e);
		}

		return buffer.toString();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			return;
		}

		Exercise item = (Exercise) e.getItem();
		String html = loadExerciseHtml(item);

		this.view.showExercise(html);
		this.view.getEntry().setExerciseId(item.getId());
	}

	public void actionPerformed(ActionEvent e) {
		int sets = this.view.getEntry().getRepetitions().size();
		if (ACTION_ADD_SET.equals(e.getActionCommand())) {
			if (sets >= MAX_SETS) {
				return;
			}
			this.view.getEntry().getRepetitions().add(DEFAULT_REPITITIONS);
			this.view.updateView();
		} else if (ACTION_REMOVE_SET.equals(e.getActionCommand())) {
			if (sets <= MIN_SETS) {
				return;
			}
			this.view.getEntry().getRepetitions().remove(sets - 1);
			this.view.updateView();
		} else if (ACTION_SAVE.equals(e.getActionCommand())) {
			try {
				ModelUtilities.merge(this.view.getEntry(), this.entry);
				hideView();
			} catch (Exception ex) {
				String localizedMessage = ex.getLocalizedMessage();
				String message = "Failed to save the entry to the database\n";
				logger.error(message, ex);
				if (localizedMessage != null) {
					message += localizedMessage;
				}
				JOptionPane.showMessageDialog(view, message,
				        "An exception occured", JOptionPane.ERROR_MESSAGE);
			}
		} else if (ACTION_CANCEL.equals(e.getActionCommand())) {
			this.cancelled = true;
			hideView();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSpinner spinner = (JSpinner) e.getSource();
		String name = spinner.getName();
		String[] split = name.split("-");
		int set = Integer.parseInt(split[1]);
		int value = ((Number) spinner.getValue()).intValue();

		List<Integer> repititions = this.view.getEntry().getRepetitions();
		repititions.set(set, value);
	}
}
