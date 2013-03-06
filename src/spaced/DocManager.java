package spaced;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import ui.SyntaxHighlighter;
import ui.WhitespaceDocument;

public class DocManager {

	public static final int UNDEFINED_ID = -1;

	private SyntaxHighlighter highlighter;
	
	private int idCounter;
	private Map<Integer, WhitespaceDocument> docMap;

	public DocManager(SyntaxHighlighter highlighter) {
		this.highlighter = highlighter;
		idCounter = 0;
		docMap = new HashMap<Integer, WhitespaceDocument>();
	}

	private int getNextID() {
		return idCounter++;
	}

	public int createDocument() {
		WhitespaceDocument doc = new WhitespaceDocument();
		doc.addDocumentListener(highlighter);
		int docID = getNextID();
		docMap.put(docID, doc);
		return docID;
	}

	public int openDocument(File file) throws IOException {
		if (!(file.exists() && file.canRead())) {
			throw new IOException("Couldn't load file " + file.getPath());
		}
		StringBuilder textBuilder = new StringBuilder();
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			textBuilder.append(scanner.nextLine()).append('\n');
		}
		scanner.close();
		int docID = createDocument();
		Document doc = getDocument(docID);
		try {
			doc.insertString(0, textBuilder.toString(), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return docID;
	}

	public WhitespaceDocument getDocument(int docID) {
		return docMap.get(docID);
	}

	public void saveDocument(int docID, File file) throws IOException,
			BadLocationException {
		StyledDocument doc = getDocument(docID);
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
		// String newName = file.getName();
		// renameDocument(docID, newName);
	}

	// public void renameDocument(String oldName, String newName) {
	// if (docMap.containsKey(oldName)) {
	// StyledDocument doc = docMap.get(oldName);
	// docMap.remove(oldName);
	// docMap.put(newName, doc);
	// }
	// }

	public void closeDocument(String name) {
		docMap.remove(name);
	}

}
