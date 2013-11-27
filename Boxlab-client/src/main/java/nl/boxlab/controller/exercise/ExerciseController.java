package nl.boxlab.controller.exercise;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import nl.boxlab.ClientContext;
import nl.boxlab.ModelUtilities;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.resources.Exercise;
import nl.boxlab.view.exercise.ExerciseView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExerciseController implements ActionListener, ItemListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ExerciseController.class);

	public static final String ACTION_SAVE = "save";
	public static final String ACTION_CANCEL = "cancel";

	private ClientContext context;
	private ExerciseView view;
	private JDialog dialog;

	private ExerciseEntry entry;

	public ExerciseController(ClientContext context) {
		this.context = context;
		this.view = new ExerciseView();
		this.view.setListener(this);
	}

	public void showView(Component owner, ExerciseEntry entry) {
		this.entry = entry;
		this.view.setEntry(ModelUtilities.deepClone(entry));
		this.dialog = new JDialog();
		this.dialog.setTitle("Showing patient details");
		this.dialog.setContentPane(view);
		this.dialog.pack();
		this.dialog.setModal(true);
		this.dialog.setLocationRelativeTo(owner);
		this.dialog.setVisible(true);
	}

	public void hideView() {
		this.dialog.dispose();
		this.dialog = null;
		this.view.setEntry(null);
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
		if (ACTION_SAVE.equals(e.getActionCommand())) {
			try {
				ModelUtilities.merge(this.view.getEntry(), this.entry);
				context.getExerciseEntryProvider().save(this.entry);

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
			hideView();
		}
	}
}
