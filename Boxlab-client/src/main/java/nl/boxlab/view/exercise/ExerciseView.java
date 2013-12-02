package nl.boxlab.view.exercise;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.controller.exercise.ExerciseController;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.resources.Exercise;

@SuppressWarnings("serial")
public class ExerciseView extends JPanel {

	private JComboBox<Exercise> cboxExercises;
	private HTMLEditorKit htmlKit;
	private JEditorPane htmlPane;

	private ExerciseController listener;

	private JButton btnSave;
	private JButton btnCancel;

	private ExerciseEntry entry;

	private JLabel lblDate;
	private JPanel panelRepetitions;
	private JSpinner[] spinnerRepitions;
	private JButton btnAddSet;
	private JButton btnRemoveSet;

	public ExerciseView() {
		initComponents();
	}

	private void initComponents() {
		this.cboxExercises = new JComboBox<>();
		this.cboxExercises.setModel(new DefaultComboBoxModel<>(Exercise
				.values()));
		this.cboxExercises.setSelectedIndex(-1);

		this.htmlKit = new HTMLEditorKit();
		this.htmlPane = new JEditorPane();
		this.htmlPane.setEditorKit(htmlKit);
		this.htmlPane.setEditable(false);

		this.btnSave = new JButton("Save");
		this.btnSave.setActionCommand(ExerciseController.ACTION_SAVE);
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setActionCommand(ExerciseController.ACTION_CANCEL);

		this.lblDate = new JLabel("-");
		this.btnAddSet = new JButton("add set");
		this.btnAddSet.setActionCommand(ExerciseController.ACTION_ADD_SET);
		this.btnRemoveSet = new JButton("remove set");
		this.btnRemoveSet
				.setActionCommand(ExerciseController.ACTION_REMOVE_SET);

		this.spinnerRepitions = new JSpinner[0];
		this.panelRepetitions = new JPanel(new FlowLayout(FlowLayout.LEADING,
				5, 0));

		JPanel panelCenter = new JPanel(new MigLayout("", "[][grow]",
				"[][][][grow]"));
		panelCenter.add(new JLabel("Date:"), "cell 0 0");
		panelCenter.add(lblDate, "cell 1 0");
		panelCenter.add(new JLabel("Exercise: "), "cell 0 1");
		panelCenter.add(cboxExercises, "cell 1 1");
		panelCenter.add(new JLabel("Repetitions:"), "cell 0 2");
		panelCenter.add(new JScrollPane(htmlPane), "cell 0 3 2 1,grow");
		panelCenter.add(panelRepetitions, "flowx,cell 1 2");
		panelCenter.add(btnAddSet, "cell 1 2");
		panelCenter.add(btnRemoveSet, "cell 1 2");

		JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		panelButton.add(btnSave);
		panelButton.add(btnCancel);

		setLayout(new BorderLayout());
		add(panelCenter, BorderLayout.CENTER);
		add(panelButton, BorderLayout.SOUTH);
	}

	public void setListener(ExerciseController listener) {
		this.listener = listener;
		this.cboxExercises.addItemListener(listener);

		this.btnAddSet.addActionListener(listener);
		this.btnRemoveSet.addActionListener(listener);
		this.btnSave.addActionListener(listener);
		this.btnCancel.addActionListener(listener);
	}

	public void updateSets() {
		List<Integer> repetitions = getEntry().getRepetitions();
		if (repetitions.isEmpty()) {
			repetitions.addAll(Arrays.asList(10, 10, 10));
		}

		this.panelRepetitions.removeAll();
		this.panelRepetitions.invalidate();

		for (JSpinner spinner : this.spinnerRepitions) {
			spinner.removeChangeListener(listener);
		}

		this.spinnerRepitions = new JSpinner[repetitions.size()];
		for (int i = 0; i < repetitions.size(); i++) {
			int value = repetitions.get(i);

			SpinnerNumberModel model = new SpinnerNumberModel(value, 0, 100, 1);
			JSpinner spinner = new JSpinner(model);
			spinner.setName("spinner-" + i);
			spinner.addChangeListener(listener);

			this.spinnerRepitions[i] = spinner;
			this.panelRepetitions.add(spinner);
		}
		
		//validate();
		//repaint();
		this.panelRepetitions.updateUI();
		updateUI();
	}

	public void updateView() {
		updateSets();

		Integer exerciseID = this.entry.getExerciseId();
		if (exerciseID == null) {
			exerciseID = 1;
		}

		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String formattedDate = dateFormat.format(entry.getDate());

		this.cboxExercises.setSelectedItem(Exercise.valueOf(exerciseID));
		this.lblDate.setText(formattedDate);
	}

	public void showExercise(String html) {
		HTMLDocument doc = (HTMLDocument) htmlKit.createDefaultDocument();
		doc.setBase(ExerciseView.class.getResource("/html/"));
		try {
			htmlKit.insertHTML(doc, 0, html, 0, 0, null);
		} catch (BadLocationException | IOException e) {

		}
		this.htmlPane.setDocument(doc);
	}

	public ExerciseEntry getEntry() {
		return entry;
	}

	public void setEntry(ExerciseEntry entry) {
		this.entry = entry;
		if (entry != null) {
			updateView();
		}
	}
}
