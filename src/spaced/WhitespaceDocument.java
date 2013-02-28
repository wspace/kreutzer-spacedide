package spaced;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class WhitespaceDocument extends DefaultStyledDocument {
	
	private static final long serialVersionUID = 1L;

	@Override
	public void insertString(int offset, String text, AttributeSet a)
			throws BadLocationException {
		super.insertString(offset, text, a);
	}

	@Override
	public void remove(int arg0, int arg1) throws BadLocationException {
		super.remove(arg0, arg1);
	}

	@Override
	public void replace(int arg0, int arg1, String arg2, AttributeSet arg3)
			throws BadLocationException {
		super.replace(arg0, arg1, arg2, arg3);
	}
	
	
	
}