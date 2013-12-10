package nl.boxlab.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JDialog;

public class DialogBuilder {

	private String title = "";
	private boolean modal = true;
	private Dimension minimumSize;
	private Component owner;
	private Container view;
	private boolean alwaysOnTop = false;
	private boolean resizable = true;

	public DialogBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public DialogBuilder setModal(boolean modal) {
		this.modal = modal;
		return this;
	}

	public DialogBuilder setMinimumSize(Dimension minimumSize) {
		this.minimumSize = minimumSize;
		return this;
	}

	public DialogBuilder setOwner(Component owner) {
		this.owner = owner;
		return this;
	}

	public DialogBuilder setView(Container view) {
		this.view = view;
		return this;
	}

	public DialogBuilder setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
		return this;
	}

	public DialogBuilder setResizable(boolean resizable) {
		this.resizable = resizable;
		return this;
	}

	public JDialog build() {
		JDialog dialog = new JDialog();
		if (this.view != null) {
			dialog.setContentPane(view);
		}
		if (this.minimumSize != null) {
			dialog.setMinimumSize(minimumSize);
		} else {
			dialog.pack();
		}

		dialog.setTitle(title);
		dialog.setModal(modal);
		dialog.setLocationRelativeTo(owner);
		dialog.setAlwaysOnTop(alwaysOnTop);
		dialog.setResizable(resizable);

		return dialog;
	}
}
