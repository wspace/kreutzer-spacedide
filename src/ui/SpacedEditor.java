package ui;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import spaced.DocManager;

public class SpacedEditor extends JTextPane {

	private static final long serialVersionUID = 1L;

	private int docID = DocManager.UNDEFINED_ID;

	private UndoManager undoManager;

	public SpacedEditor() {

		undoManager = new UndoManager();
	}

	public void loadDocument(StyledDocument doc, int docID) {
		setDocument(doc);
		doc.addUndoableEditListener(undoManager);
		this.docID = docID;
	}

	public int getDocID() {
		return docID;
	}

	public void undo() {
		undoManager.undo();
	}

	public void redo() {
		undoManager.redo();
	}

}
