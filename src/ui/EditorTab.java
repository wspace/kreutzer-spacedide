package ui;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.undo.UndoManager;

public class EditorTab extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private JTextPane editor;
	
	private UndoManager undoManager;

	public EditorTab(JTextPane editor) {
		super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.editor = editor;
		setViewportView(editor);
		undoManager = new UndoManager();
		editor.getDocument().addUndoableEditListener(undoManager);
	}

	public JTextPane getEditor() {
		return editor;
	}
	
	public UndoManager getUndoManager() {
		return undoManager;
	}

}
