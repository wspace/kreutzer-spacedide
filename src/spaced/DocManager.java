package spaced;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

public class DocManager {

	private int numDocs;
	private Map<String, StyledDocument> docMap;

	public DocManager() {
		numDocs = 0;
		docMap = new HashMap<String, StyledDocument>();
	}

	public StyledDocument createDocument() {
		return createDocument("new" + numDocs++);
	}

	public StyledDocument createDocument(String name) {
		StyledDocument doc = new DefaultStyledDocument();
		docMap.put(name, doc);
		return doc;
	}

	public StyledDocument openDocument(File file) throws IOException {
		if (!(file.exists() && file.canRead())) {
			throw new IOException("Couldn't load file " + file.getPath());
		}
		StringBuilder textBuilder = new StringBuilder();
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			textBuilder.append(scanner.nextLine()).append('\n');
		}
		scanner.close();
		StyledDocument doc = createDocument(file.getName());
		try {
			doc.insertString(0, textBuilder.toString(), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public StyledDocument getDocument(String name) {
		return docMap.get(name);
	}

	public void saveDocument(String name, File file) throws IOException,
			BadLocationException {
		StyledDocument doc = getDocument(name);
		if (file == null || doc == null)
			return;
		if (!file.exists())
			file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(fos);
		String text = doc.getText(0, doc.getLength());
		Scanner scanner = new Scanner(text);
		while (scanner.hasNextLine()) {
			pw.println(scanner.nextLine());
		}
		scanner.close();
		pw.close();
	}

}
