package nl.boxlab.view.messages;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.miginfocom.swing.MigLayout;
import nl.boxlab.model.Message;

@SuppressWarnings("serial")
public class TextBubblePanel extends JPanel {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-YYYY HH:mm");

	public static final int POINTER_OFFSET = 10;
	public static final int POINTER_HEIGHT = 10;
	public static final int STROKE_WIDTH = 1;
	public static final int RADIUS = 10;
	public static final int OFFSET = 10;

	private boolean fromPatient;
	private JLabel lblDate;
	private JTextPane txtMessage;

	private RenderingHints hints;

	public TextBubblePanel() {
		this.hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		initComponents();
	}

	private void initComponents() {
		this.lblDate = new JLabel();
		this.txtMessage = new JTextPane();
		this.txtMessage.setOpaque(true);

		setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
		setLayout(new MigLayout("insets 0 5 0 5", "[grow]", "[][]"));
		add(lblDate, "cell 0 0, growx");
		add(txtMessage, "cell 0 1, growx");
	}

	public void updateMessage(Message message) {
		this.fromPatient = message.isFromPatient();

		if (fromPatient) {
			this.lblDate.setHorizontalAlignment(SwingConstants.RIGHT);
		} else {
			this.lblDate.setHorizontalAlignment(SwingConstants.LEFT);
		}
		this.lblDate.setText(DATE_FORMAT.format(message.getDate()));

		updateText(message.getMessage());
	}

	private void updateText(String text) {
		this.txtMessage.setText(text);

		// align the text in the texpane
		StyledDocument doc = this.txtMessage.getStyledDocument();
		SimpleAttributeSet attr = new SimpleAttributeSet();
		if (fromPatient) {
			StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
		} else {
			StyleConstants.setAlignment(attr, StyleConstants.ALIGN_LEFT);
		}
		doc.setParagraphAttributes(0, doc.getLength(), attr, false);
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (this.txtMessage != null) {
			this.txtMessage.setBackground(bg);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		int x = STROKE_WIDTH / 2;
		int y = STROKE_WIDTH / 2;
		int width = getWidth() - RADIUS / 2 - RADIUS;
		int height = getHeight() - RADIUS / 2;

		Polygon pointer = new Polygon();

		if (!fromPatient) {
			x += OFFSET;
			pointer.addPoint(x, y + POINTER_OFFSET);
			pointer.addPoint(x - OFFSET, y + POINTER_OFFSET + POINTER_HEIGHT / 2);
			pointer.addPoint(x, y + POINTER_OFFSET + POINTER_HEIGHT);
		} else {
			pointer.addPoint(width, y + POINTER_OFFSET);
			pointer.addPoint(width + OFFSET, y + POINTER_OFFSET + POINTER_HEIGHT / 2);
			pointer.addPoint(width, y + POINTER_OFFSET + POINTER_HEIGHT);
		}

		RoundRectangle2D.Double rectangle =
		        new RoundRectangle2D.Double(x, y, width, height, RADIUS, RADIUS);

		Area area = new Area(rectangle);
		area.add(new Area(pointer));

		paintBackground(g2d, area);
		g2d.setColor(getBackground().darker());
		g2d.setRenderingHints(hints);
		g2d.setStroke(new BasicStroke(STROKE_WIDTH));
		g2d.draw(area);
	}

	protected void paintBackground(Graphics2D g2d, Area area) {
		Component parent = getParent();
		if (parent != null) {
			Color bg = parent.getBackground();
			Rectangle rect = new Rectangle(0, 0, getWidth(), getHeight());
			Area borderRegion = new Area(rect);
			borderRegion.subtract(area);
			g2d.setClip(borderRegion);
			g2d.setColor(bg);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setClip(null);
		}
	}
}
