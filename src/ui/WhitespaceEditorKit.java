package ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

public class WhitespaceEditorKit extends StyledEditorKit {

	private static final long serialVersionUID = 1L;

	@Override
	public Document createDefaultDocument() {
		return new WhitespaceDocument();
	}

	@Override
	public String getContentType() {
		return "text/whitespace";
	}

	@Override
	public void read(InputStream in, Document doc, int pos)
			throws IOException, BadLocationException {
		super.read(in, doc, pos);
	}

	@Override
	public void read(Reader in, Document doc, int pos) throws IOException,
			BadLocationException {
		super.read(in, doc, pos);
	}

	@Override
	public void write(OutputStream out, Document doc, int pos, int length)
			throws IOException, BadLocationException {
		super.write(out, doc, pos, length);
	}

	@Override
	public void write(Writer out, Document doc, int pos, int length)
			throws IOException, BadLocationException {
		super.write(out, doc, pos, length);
	}

}
