package nl.boxlab.view.exercise;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.controller.details.PatientDetailController;
import nl.boxlab.controller.exercise.ExerciseController;
import nl.boxlab.model.ExerciseEntry;
import nl.boxlab.resources.Exercise;

@SuppressWarnings("serial")
public class ExerciseView extends JPanel {

	private JComboBox<Exercise> cboxExercises;
	private HTMLEditorKit htmlKit;
	private JEditorPane htmlPane;

	private JButton btnSave;
	private JButton btnCancel;

	private ExerciseEntry entry;

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

		JPanel panelCenter = new JPanel(new MigLayout("", "[][grow]",
				"[][grow]"));
		panelCenter.add(new JLabel("Exercise: "), "cell 0 0,alignx trailing");
		panelCenter.add(cboxExercises, "cell 1 0");
		panelCenter.add(new JScrollPane(htmlPane), "cell 0 1 2 1,grow");

		JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		panelButton.add(btnSave);
		panelButton.add(btnCancel);

		setLayout(new BorderLayout());
		add(panelCenter, BorderLayout.CENTER);
		add(panelButton, BorderLayout.SOUTH);
	}

	public void setListener(ExerciseController listener) {
		this.cboxExercises.addItemListener(listener);
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
		if (entry!=null) {
			Exercise exercise = Exercise.valueOf(entry.getExerciseId());
			this.cboxExercises.setSelectedItem(exercise);
		}
	}
}
