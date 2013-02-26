package spaced;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public abstract class SpacedAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public SpacedAction(String text, String iconName, String description, KeyStroke keyStroke) {
		super(text, new ImageIcon(Spaced.class.getResource(iconName)));
		putValue(SHORT_DESCRIPTION, description);
		putValue(ACCELERATOR_KEY, keyStroke);
	}
	

}
