package diagram;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import parser.WhitespaceLang;

public class SyntaxHighlighter implements DocumentListener {

	private MutableAttributeSet defaultStyle = new SimpleAttributeSet();
	private MutableAttributeSet spaceStyle;
	private MutableAttributeSet tabStyle;

	public SyntaxHighlighter(MutableAttributeSet spaceStyle,
			MutableAttributeSet tabStyle, MutableAttributeSet defaultStyle) {
		this.spaceStyle = spaceStyle;
		this.tabStyle = tabStyle;
		this.defaultStyle = defaultStyle;
	}

	public static final SyntaxHighlighter getDefaultHighlighter() {
		SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
		StyleConstants.setBackground(defaultStyle, new Color(190, 190, 190));
		SimpleAttributeSet spaceStyle = new SimpleAttributeSet();
		StyleConstants.setBackground(spaceStyle, new Color(255, 66, 66));
		SimpleAttributeSet tabStyle = new SimpleAttributeSet();
		StyleConstants.setBackground(tabStyle, new Color(0, 128, 255));
		SyntaxHighlighter defaultHighlighter = new SyntaxHighlighter(
				spaceStyle, tabStyle, defaultStyle);
		return defaultHighlighter;
	}

	public void addDefaultAttribute(Object name, Object value) {
		defaultStyle.addAttribute(name, value);
	}

	public void addSpaceAttribute(Object name, Object value) {
		spaceStyle.addAttribute(name, value);
	}

	public void addTabAttribute(Object name, Object value) {
		spaceStyle.addAttribute(name, value);
	}

	public Object getSpaceAttribute(Object name) {
		return spaceStyle.getAttribute(name);
	}

	public Object getDefaultAttribute(Object name) {
		return defaultStyle.getAttribute(name);
	}

	public Object getTabAttribute(Object name) {
		return tabStyle.getAttribute(name);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			highlight((StyledDocument) e.getDocument(), e.getOffset(),
					e.getLength());
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {

	}

	public void highlight(StyledDocument doc, int offset, int length)
			throws BadLocationException {
		String text = doc.getText(offset, length);
		AttributeSet style = null;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
			case WhitespaceLang.SPACE:
				style = spaceStyle;
				break;
			case WhitespaceLang.TAB:
				style = tabStyle;
				break;
			default:
				style = defaultStyle;
				break;
			}
			final int charOffset = offset + i;
			final StyledDocument finalDoc = doc;
			final AttributeSet finalStyle = style;
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					finalDoc.setCharacterAttributes(charOffset, 1, finalStyle,
							false);
				}

			});
		}
	}

}
