package diagram;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import parser.WhitespaceLang;
import parser.WhitespaceOperationType;
import spaced.WhitespaceUtilities;

public class CommandDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel panel;

	private WhitespaceLang lang;

	private ITabManager tabManager;

	public CommandDialog(JFrame owner, ITabManager tabManager,
			WhitespaceLang lang) {
		super(owner);
		this.lang = lang;
		this.tabManager = tabManager;
		init();
	}

	private void init() {
		setTitle("Command Dialog");
		setContentPane(getPanel());
		pack();
	}

	public JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			GridLayout layout = new GridLayout(0, 3);
			panel.setLayout(layout);
			JButton button;
			for (final String op : lang.keyWords()) {
				button = new JButton();
				final WhitespaceOperationType type = lang.getOperation(op);
				button.setText(type.toString());
				button.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						JTextPane editor = tabManager.getActiveEditor();
						if (editor == null)
							return;
						int i = editor.getCaretPosition();
						try {
							editor.getDocument().insertString(i,
									type.toString() + op, null);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
				});
				panel.add(button);
			}
			JLabel label = new JLabel("Insert Number:");
			panel.add(label);
			final JTextField numberField = new JTextField();
			panel.add(numberField);
			button = new JButton("OK");
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JTextPane editor = tabManager.getActiveEditor();
					if (editor == null)
						return;
					String text = numberField.getText();
					if (text == null || text.isEmpty())
						return;
					int n = Integer.valueOf(text);
					String numberString = WhitespaceUtilities
							.toWhitespaceNumber(n);
					int i = editor.getCaretPosition();
					try {
						editor.getDocument().insertString(i,
								text + numberString, null);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			});
			panel.add(button);
			label = new JLabel("Insert Character:");
			panel.add(label);
			final JTextField charField = new JTextField();
			charField.setColumns(1);
			panel.add(charField);
			button = new JButton("OK");
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JTextPane editor = tabManager.getActiveEditor();
					if (editor == null)
						return;
					String text = charField.getText();
					if (text == null || text.isEmpty())
						return;
					char c = text.charAt(0);
					String numberString = WhitespaceUtilities
							.toWhitespaceNumber(c);
					int i = editor.getCaretPosition();
					try {
						editor.getDocument().insertString(i, c + numberString,
								null);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}

			});
			panel.add(button);
			panel.setPreferredSize(new Dimension(400, 200));
		}
		return panel;
	}

}
