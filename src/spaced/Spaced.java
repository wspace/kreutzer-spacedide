package spaced;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import parser.LanguageDefinition;
import parser.ParameterizedWhitespaceOperation;
import parser.WhitespaceApp;
import parser.WhitespaceParser;
import vm.VMListener;
import vm.WhitespaceMachine;
import vm.WhitespaceMachine.ExecutionMode;

public class Spaced implements VMListener {

	public enum Actions {
		NEW_DOC, OPEN, SAVE, CLOSE_DOC, QUIT, UNDO, REDO, MERGE, INSERT_COMMAND, RUN, STOP, DEBUG, RESUME, STEP, SHOW_COMMAND_DIALOG, SHOW_CREDITS, REMOVE_COMMENTS, GENERATE_OUTPUT_CODE, ADD_BREAKPOINT
	}

	public static final String TITLE = "Spaced IDE";
	public static final String VERSION = "1.1.0";

	private Properties properties;

	private SpacedView view;
	private WhitespaceMachine virtualMachine;

	private ActionMap actions;

	private File lastDir = new File(System.getProperty("user.home"));

	private boolean documentChanged;
	private int numNewDocs;

	public Spaced() {
		actions = generateActionMap();
		view = new SpacedView(TITLE + " " + VERSION, actions);
		loadSettings();
		initVM();
	}

	private ActionMap generateActionMap() {
		ActionMap actions = new ActionMap();
		// New document
		AbstractAction action = new SpacedAction("New document", "newdoc.png",
				"Creates a new document", KeyStroke.getKeyStroke(KeyEvent.VK_N,
						KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				newDocument();
			}
		};
		actions.put(Actions.NEW_DOC, action);
		// Open document
		action = new SpacedAction("Open", "open.png", "Opens a document",
				KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				openDocument();
			}
		};
		actions.put(Actions.OPEN, action);
		action = new SpacedAction("Save", "save.png",
				"Saves the selected document", KeyStroke.getKeyStroke(
						KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				saveDocument();
			}
		};
		actions.put(Actions.SAVE, action);
		action = new SpacedAction("Close Document", "close.png",
				"Closes the selected document", KeyStroke.getKeyStroke(
						KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				closeDocument();
			}
		};
		actions.put(Actions.CLOSE_DOC, action);
		action = new SpacedAction("Exit", "exit.png", "Quits the application",
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		};
		actions.put(Actions.QUIT, action);
		action = new SpacedAction("Undo", "undo.png", "Undo",
				KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		};
		actions.put(Actions.UNDO, action);
		action = new SpacedAction("Redo", "redo.png", "Redo",
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				redo();
			}
		};
		actions.put(Actions.REDO, action);
		action = new SpacedAction("Run", "run.png", "Executes the program",
				KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		};
		actions.put(Actions.RUN, action);

		action = new SpacedAction("Add Breakpoint", "breakpoint.png",
				"Adds a debug breakpoint", KeyStroke.getKeyStroke(
						KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextPane editor = view.getActiveTab().getEditor();
				if (editor == null)
					return;
				int i = editor.getCaretPosition();
				try {
					editor.getDocument().insertString(i, "#DBG_BREAK", null);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		};
		actions.put(Actions.ADD_BREAKPOINT, action);

		action = new SpacedAction("Stop", "stop.png", "Stops the execution",
				KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		};
		actions.put(Actions.STOP, action);
		action = new SpacedAction("Debug", "debug.png",
				"Executes the program in debug mode", KeyStroke.getKeyStroke(
						KeyEvent.VK_D, KeyEvent.ALT_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				debug();
			}
		};
		actions.put(Actions.DEBUG, action);
		action = new SpacedAction("Step", "step.png", "Executes one step",
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				runStep();
			}
		};
		actions.put(Actions.STEP, action);
		action = new SpacedAction("Resume", "resume.png",
				"Executes untill the next break point is reached",
				KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.ALT_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				resume();
			}
		};
		actions.put(Actions.RESUME, action);
		action = new AbstractAction("Show Command Dialog") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				view.getCommandDialog().setVisible(true);
			}
		};
		actions.put(Actions.SHOW_COMMAND_DIALOG, action);

		action = new AbstractAction("Credits") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				view.showCredits();
			}
		};
		actions.put(Actions.SHOW_CREDITS, action);

		action = new AbstractAction("Generate Output Code") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = view.showCodeGenDialog();
				if (text != null && !text.isEmpty()) {
					StringBuilder builder = new StringBuilder();
					builder.append("GENERATED_OUTPUT_START");
					for (int i = 0; i < text.length(); i++) {
						char c = text.charAt(i);
						int n = (int) c;
						String numberString = (n >= 0 ? ' ' : '\t')
								+ Integer.toBinaryString(Math.abs(n))
										.replace('1', '\t').replace('0', ' ')
								+ '\n';
						builder.append("  ");
						builder.append(numberString);
						builder.append("\t\n  ");
					}
					builder.append("END");
					JTextPane editor = view.getActiveTab().getEditor();
					if (editor == null)
						return;
					int i = editor.getCaretPosition();
					try {
						editor.getDocument().insertString(i,
								builder.toString(), null);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		actions.put(Actions.GENERATE_OUTPUT_CODE, action);

		action = new AbstractAction("Remove Comments") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				removeComments();
			}
		};
		actions.put(Actions.REMOVE_COMMENTS, action);

		action = new SpacedAction("Merge", "merge.png",
				"Opens the merge dialog", KeyStroke.getKeyStroke(KeyEvent.VK_M,
						KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				File[] files = view.showMergeDialog(lastDir);
				ArrayList<Character> list = new ArrayList<Character>();
				list.add(' ');
				list.add('\t');
				list.add('\n');
				if (files.length == 2 && files[0] != null && files[1] != null) {
					Scanner scanner;
					try {
						scanner = new Scanner(files[0]);

						StringBuilder input = new StringBuilder();
						while (scanner.hasNextLine()) {
							input.append(scanner.nextLine() + "\n");
						}
						SpacedMerger merger = new SpacedMerger(list);
						String text = view.getText();
						if (text == null || text.isEmpty()) {
							view.getPrintStream().println(
									"Error: Source code is empty");
						}
						String merged = merger.merge(text, input.toString());
						PrintWriter out = new PrintWriter(files[1]);
						out.print(merged);
						out.flush();
						out.close();
						System.out.println(merged);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		actions.put(Actions.MERGE, action);

		return actions;
	}

	private void loadSettings() {
		String path = System.getenv("APPDATA");
		if (path == null) {
			path = System.getProperty("user.home");
		}
		File file = new File(path, "/.spaced/settings");
		if (!file.exists()) {
			try {
				file.getParentFile().mkdir();
				file.createNewFile();
				properties = new Properties();
				properties.put("default_dir", System.getProperty("user.home"));
				properties.store(new FileOutputStream(file),
						"Whitespace IDE property file");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
				if (properties.containsKey("default_dir")) {
					lastDir = new File((String) properties.get("default_dir"));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void initVM() {
		virtualMachine = new WhitespaceMachine(view.getInputStream(),
				view.getPrintStream());
		// virtualMachine.addHeapAccessListener(view.getMemoryTable());
		// virtualMachine.addStackListener(view.getMemoryTable());
		virtualMachine.addVMListener(this);
		virtualMachine.addVMListener(view.getStatusBar());
	}

	public static void main(String[] args) {
		new Spaced();
	}

	public void newDocument() {
		final DefaultStyledDocument doc = new DefaultStyledDocument();
		doc.addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				documentChanged = true;
				view.setSelectedTabMark(true);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// System.out.println("fsdjfksl");
				documentChanged = true;
				view.setSelectedTabMark(true);
				try {
					final int offset = e.getOffset();
					final int length = e.getLength();
					final char c = e.getDocument().getText(offset, 1).charAt(0);
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							SimpleAttributeSet set = new SimpleAttributeSet();
							StyleConstants.setBackground(set,
									c == ' ' ? Color.blue : Color.red);
							doc.setCharacterAttributes(offset, length, set,
									false);
						}
					});
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// documentChanged = true;
				// view.setSelectedTabMark(true);
			}
		});
		// doc.addDocumentListener(createSyntaxHighlighter(doc));
		doc.setDocumentFilter(new DocumentFilter() {

			@Override
			public void insertString(FilterBypass fb, int offset,
					String string, AttributeSet attr)
					throws BadLocationException {
				System.out.println("fdjskl");
				super.insertString(fb, offset, string, attr);
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setBackground(sas, Color.blue);
				((StyledDocument) fb.getDocument()).setCharacterAttributes(
						offset, 1, sas, false);
			}

		});
		view.openTab(doc, "New_" + numNewDocs);
		numNewDocs++;
	}

	// @Override
	// public StyledDocument openDocument(File file) {
	// try {
	// Scanner scanner = new Scanner(file);
	// StringBuilder text = new StringBuilder();
	// while (scanner.hasNextLine()) {
	// String line = scanner.nextLine();
	// text.append(line).append('\n');
	// }
	// scanner.close();
	// StyledDocument doc = new DefaultStyledDocument();
	// doc.insertString(0, text.toString(), null);
	// doc.addDocumentListener(new DocumentListener() {
	//
	// @Override
	// public void removeUpdate(DocumentEvent e) {
	// documentChanged = true;
	// view.setSelectedTabMark(true);
	// }
	//
	// @Override
	// public void insertUpdate(DocumentEvent e) {
	// documentChanged = true;
	// view.setSelectedTabMark(true);
	// }
	//
	// @Override
	// public void changedUpdate(DocumentEvent e) {
	// // documentChanged = true;
	// // view.setSelectedTabMark(true);
	// }
	// });
	// lastDir = file.getParentFile();
	// return doc;
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } catch (BadLocationException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	public void openDocument() {
		try {
			File file = view.showOpenDialog(lastDir);
			if (file == null || !file.exists())
				return;
			Scanner scanner = new Scanner(file);
			StringBuilder text = new StringBuilder();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				text.append(line).append('\n');
			}
			scanner.close();
			StyledDocument doc = new DefaultStyledDocument();
			doc.insertString(0, text.toString(), null);
			doc.addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					documentChanged = true;
					view.setSelectedTabMark(true);
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					documentChanged = true;
					view.setSelectedTabMark(true);
				}

				@Override
				public void changedUpdate(DocumentEvent e) {

				}
			});
			doc.addDocumentListener(createSyntaxHighlighter(doc));
			view.openTab(doc, file.getName());
			lastDir = file.getParentFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public DocumentListener createSyntaxHighlighter(StyledDocument doc) {
		HashMap<Character, Color> charMap = new HashMap<Character, Color>();
		charMap.put(' ', Color.green);
		charMap.put('\t', Color.blue);
		charMap.put('\n', Color.red);
		return new CharHighlighter(charMap, doc);
	}

	public void saveDocument() {
		try {
			File file = view.showSaveDialog(lastDir);
			if (file == null)
				return;
			if (!file.exists())
				file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			PrintWriter pw = new PrintWriter(fos);
			String text = view.getText();
			Scanner scanner = new Scanner(text);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				pw.println(line);
			}
			scanner.close();
			pw.close();
			documentChanged = false;
			view.setSelectedTabMark(false);
			view.setSelectedTabTitle(file.getName());
			lastDir = file.getParentFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void closeDocument() {
		view.closeActiveTab();
	}

	public void exit() {
		System.out.println("Quitting now...");
		System.exit(0); // FIXME
	}

	public void undo() {
		EditorTab tab = view.getActiveTab();
		if (tab != null) {
			UndoManager um = tab.getUndoManager();
			if (um.canUndo()) {
				um.undo();
			}
		}
	}

	public void redo() {
		EditorTab tab = view.getActiveTab();
		if (tab != null) {
			UndoManager um = tab.getUndoManager();
			if (um.canRedo()) {
				um.redo();
			}
		}
	}

	public void execute() {
		String text = view.getText();
		if (text == null || text.isEmpty()) {
			view.getPrintStream().println("Error: Source code is empty");
		}
		WhitespaceApp app = new WhitespaceParser(text).parse();
		stop(); // Quits current program
		virtualMachine.init(app);
		view.clearMemoryTable();
		view.clearConsole();
		virtualMachine.start(WhitespaceMachine.ExecutionMode.RUN_CONTINUOUSLY);
	}

	public void stop() {
		if (virtualMachine != null && virtualMachine.isRunning()) {
			virtualMachine.quit();
			Thread executionThread = virtualMachine.getExecutionThread();
			if (executionThread != null) {
				try {
					executionThread.join(1000); // This is really important! If
												// not included, the VM may not
												// be terminated before restart
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(virtualMachine.isRunning());
				if (executionThread.isAlive()) {
					forceStop();
				}
			}
			System.out.println("abort");
		}
	}

	public void forceStop() {
		System.out.println("Force stop");
		virtualMachine.getExecutionThread().stop();
	}

	public boolean isDocumentSaved() {
		return !documentChanged;
	}

	public File getDefaultDir() {
		return lastDir;
	}

	public void debug() {
		String text = view.getText();
		if (text == null || text.isEmpty()) {
			view.getPrintStream().println("Error: Source code is empty");
		}
		WhitespaceApp app = new WhitespaceParser(text).parse();
		stop(); // Quit current program
		virtualMachine.init(app);
		view.clearMemoryTable();
		view.clearConsole();
		virtualMachine.start(WhitespaceMachine.ExecutionMode.RUN_TO_BREAKPOINT);
	}

	public void runStep() {
		if (virtualMachine.isPaused()) {
			System.out.println("Step");
			virtualMachine.setExecutionMode(ExecutionMode.RUN_STEP);
			virtualMachine.resume();
		}
	}

	public void resume() {
		if (virtualMachine.isPaused()) {
			System.out.println("Resume");
			virtualMachine.setExecutionMode(ExecutionMode.RUN_TO_BREAKPOINT);
			virtualMachine.resume();
		}
	}

	public void removeComments() {
		EditorTab tab = view.getActiveTab();
		if (tab != null) {
			JTextPane editor = tab.getEditor();
			int x0 = editor.getSelectionStart();
			int x1 = editor.getSelectionEnd();
			int cPos = editor.getCaretPosition();
			String text = editor.getText();
			if (text == null)
				return;
			if (x0 == x1) {
				x0 = 0;
				x1 = text.length();
			}
			StringBuilder newText = new StringBuilder();
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (i < x0 || i > x1 || c == '\n' || c == '\t' || c == ' ') {
					newText.append(c);
				}
			}
			editor.setText(newText.toString());
			editor.setCaretPosition(Math.min(x0, newText.length()));
		}
	}

	@Override
	public void vmStarted() {
		view.getMemoryTable().clear();
	}

	@Override
	public void vmPaused() {
		ParameterizedWhitespaceOperation pwo = virtualMachine.getCurrentPWO();
		if (pwo != null) {
			int pos = pwo.textPos;
			view.getActiveTab().getEditor().setSelectionStart(pos);
		}
		view.getMemoryTable().update(virtualMachine.getStack().getValueMap(),
				virtualMachine.getHeap().getValueMap());
	}

	@Override
	public void vmResumed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void vmStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public void newOperation(ParameterizedWhitespaceOperation pwo) {
		// TODO Auto-generated method stub

	}
}
