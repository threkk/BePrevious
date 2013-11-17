package nl.boxlab.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class TitlePanel extends JPanel {

	private JLabel lblTitle;
	private JLabel lblDescription;

	public TitlePanel() {
		this(null, null);
	}

	public TitlePanel(String title, String description) {
		initComponents();

		if (title != null) {
			setTitle(title);
		}

		if (description != null) {
			setDescription(description);
		}
	}

	private void initComponents() {
		this.lblTitle = new JLabel();
		this.lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 24));
		this.lblDescription = new JLabel();

		setLayout(new MigLayout("", "[]", "[][]"));
		setBackground(Color.WHITE);
		add(lblTitle, "cell 0 0");
		add(lblDescription, "cell 0 1");
	}

	public String getTitle() {
		return this.lblTitle.getText();
	}

	public void setTitle(String title) {
		this.lblTitle.setText(title);
	}

	public String getDescription() {
		return this.lblDescription.getText();
	}

	public void setDescription(String description) {
		this.lblDescription.setText(description);
	}
}
