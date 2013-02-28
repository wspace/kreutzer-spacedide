package spaced;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CharHighlighter implements DocumentListener, KeyListener {

	private Map<Character, Color> charMap;
	private StyledDocument doc;
	
	public CharHighlighter(Map<Character, Color> charMap, StyledDocument doc) {
		this.charMap = charMap;
		this.doc = doc;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
//			StyledDocument doc = (StyledDocument) e.getDocument();
			int offset = e.getOffset();
			char c = e.getDocument().getText(offset, 1).charAt(0);
			if (!charMap.containsKey(c)) {
				return;
			}
			Color color = charMap.get(c);
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setBackground(sas, color);
			doc.setCharacterAttributes(offset, 1, sas, false);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		try {
//			StyledDocument doc = (StyledDocument) e.getDocument();
			int offset = doc.getEndPosition().getOffset() - 1;
			char c = doc.getText(offset, 1).charAt(0);
			if (!charMap.containsKey(c)) {
				return;
			}
			Color color = charMap.get(c);
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setBackground(sas, color);
			doc.setCharacterAttributes(offset, 1, sas, false);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

}
