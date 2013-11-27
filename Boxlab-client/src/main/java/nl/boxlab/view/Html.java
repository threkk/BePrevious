package nl.boxlab.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.resources.Exercise;

public class Html extends JFrame {

	public static final URL STYLESHEET = Exercise.class
			.getResource("/html/style.css");

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Html frame = new Html();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 * @throws BadLocationException
	 */
	public Html() throws IOException, BadLocationException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel(new MigLayout("", "[grow]", "[grow]"));

		// create a JEditorPane
		URL url = Html.class.getResource("/html/exercise01.html");
		System.out.println("url: " + url.toExternalForm());
		JEditorPane jEditorPane = new JEditorPane();

		// make it read-only
		jEditorPane.setEditable(false);

		// add a HTMLEditorKit to the editor pane
		HTMLEditorKit kit = new HTMLEditorKit();
		HTMLDocument htmlDocument = (HTMLDocument) kit.createDefaultDocument();
		try {
			URL resource = Html.class.getResource("/html/");
			System.out.println("resource: " + resource.toExternalForm());
			htmlDocument.setBase(Html.class.getResource("/html/"));
		} catch (Exception e) {

		}

//		kit.insertHTML(htmlDocument, 0, Exercises.LEG_EXTENSION.getHtml(), 0,
//				0, null);
//
//		kit.getStyleSheet().importStyleSheet(Exercises.STYLESHEET);
		jEditorPane.setEditorKit(kit);
		jEditorPane.setDocument(htmlDocument);
		// jEditorPane.setText(Exercises.LEG_EXTENSION.getHtml());

		// now add it to a scroll pane
		contentPane.add(new JScrollPane(jEditorPane), "cell 0 0,grow");

		setContentPane(contentPane);
	}

	private String safeLoadText(String resourceName) {
		ClassLoader cl = Exercise.class.getClassLoader();
		StringBuffer buffer = new StringBuffer();

		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = cl.getResourceAsStream(resourceName);
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
			LoggerFactory.getLogger(Exercise.class).error(
					"failed to load html for exercize", e);
		}

		return buffer.toString();
	}
}
