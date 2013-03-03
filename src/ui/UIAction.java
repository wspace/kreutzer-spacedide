package ui;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public abstract class UIAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public UIAction(String text, String iconName, String description,
			KeyStroke keyStroke) {
		super(text, new ImageIcon("res/icons/" + iconName));
		putValue(SHORT_DESCRIPTION, description);
		putValue(ACCELERATOR_KEY, keyStroke);
	}

	public UIAction(String text, String iconName, String description) {
		super(text, new ImageIcon("res/icons/" + iconName));
		putValue(SHORT_DESCRIPTION, description);
	}

	public UIAction(String text, String description, KeyStroke keyStroke) {
		super(text);
		putValue(SHORT_DESCRIPTION, description);
		putValue(ACCELERATOR_KEY, keyStroke);
	}

	public UIAction(String text, String description) {
		super(text);
		putValue(SHORT_DESCRIPTION, description);
	}

}
