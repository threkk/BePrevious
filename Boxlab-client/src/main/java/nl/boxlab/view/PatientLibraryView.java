package nl.boxlab.view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.model.Patient;
import nl.boxlab.table.TableController;

@SuppressWarnings("serial")
public class PatientLibraryView extends JPanel {

	private TableController<Patient> tableController;
	private JTable table;

	public PatientLibraryView(TableController<Patient> tableController) {
		this.tableController = tableController;

		initComponents();
	}

	private void initComponents() {
		this.table = new JTable();
		this.tableController.setup(table);

		JPanel panelDetails = new JPanel();

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.75);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(new JScrollPane(table));
		splitPane.setRightComponent(panelDetails);

		setLayout(new MigLayout("", "[grow]", "[grow]"));
		add(splitPane, "cell 0 0,grow");
	}
}
