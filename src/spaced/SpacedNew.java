package spaced;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleContext.NamedStyle;

import parser.ParameterizedWhitespaceOperation;
import parser.WhitespaceApp;
import parser.WhitespaceParser;
import parser.WhitespaceSyntaxError;
import ui.SpacedEditor;
import ui.SpacedViewNew;
import ui.SyntaxHighlighter;
import ui.UIAction;
import vm.VMListener;
import vm.WhitespaceMachine;
import vm.WhitespaceMachine.ExecutionMode;

public class SpacedNew implements VMListener {

	public enum ActionType {
		NEW_DOC, OPEN, SAVE, CLOSE_DOC, QUIT, UNDO, REDO, MERGE, RUN, STOP, DEBUG, RESUME, STEP, SHOW_COMMAND_DIALOG, SHOW_CREDITS, REMOVE_COMMENTS, GENERATE_OUTPUT_CODE, ADD_BREAKPOINT
	}

	public static final String TITLE = "Spaced IDE";
	public static final String VERSION = "1.2";

	private Properties properties;

	private SpacedViewNew view;
	private WhitespaceMachine virtualMachine;

	private DocManager docManager;
	private File lastDir;

	private int numNewDocs = 0;

	private SyntaxHighlighter highlighter = SyntaxHighlighter
			.getDefaultHighlighter();

	public SpacedNew() {
		docManager = new DocManager(highlighter);
		initView();
		initVM();
		try {
			loadSettings();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initView() {
		ActionMap actionMap = new ActionMap();
		actionMap.put(ActionType.NEW_DOC, getNewDocumentAction());
		actionMap.put(ActionType.OPEN, getOpenDocumentAction());
		actionMap.put(ActionType.SAVE, getSaveDocumentAction());
		actionMap.put(ActionType.CLOSE_DOC, getCloseDocumentAction());
		actionMap.put(ActionType.QUIT, getQuitAction());
		actionMap.put(ActionType.ADD_BREAKPOINT, getAddBreakpointAction());
		actionMap.put(ActionType.DEBUG, getDebugAction());
		actionMap.put(ActionType.GENERATE_OUTPUT_CODE,
				getGenerateOutputCodeAction());
		actionMap.put(ActionType.MERGE, getMergeAction());
		actionMap.put(ActionType.UNDO, getUndoAction());
		actionMap.put(ActionType.REDO, getRedoAction());
		actionMap.put(ActionType.REMOVE_COMMENTS, getRemoveCommentsAction());
		actionMap.put(ActionType.RUN, getRunAction());
		actionMap.put(ActionType.STOP, getStopAction());
		actionMap.put(ActionType.STEP, getRunStepAction());
		actionMap.put(ActionType.RESUME, getResumeAction());
		actionMap.put(ActionType.SHOW_COMMAND_DIALOG, getCommandDialogAction());
		actionMap.put(ActionType.SHOW_CREDITS, getCreditsAction());
		view = new SpacedViewNew(TITLE, actionMap);
	}

	public void initVM() {
		virtualMachine = new WhitespaceMachine(view.getInputStream(),
				view.getPrintStream());
		virtualMachine.addVMListener(this);
		virtualMachine.addVMListener(view.getStatusBar());
	}

	public String getPropertiesPath() {
		String path = System.getenv("APPDATA");
		if (path == null) {
			path = System.getProperty("user.home");
		}
		path = path.concat("/.spaced/settings");
		return path;
	}

	public void loadSettings() throws FileNotFoundException, IOException {
		properties = new Properties();
		File file = new File(getPropertiesPath());
		if (file.exists()) {
			properties.load(new FileInputStream(file));
		} else {
			properties.load(getClass().getResourceAsStream("default_settings"));
		}

		if (properties.containsKey("default_dir")) {
			lastDir = new File((String) properties.get("default_dir"));
		}
		highlighter = SyntaxHighlighter.getDefaultHighlighter();
		if (properties.containsKey("space_color")) {
			Color spaceColor = new Color(Integer.parseInt(
					properties.get("space_color").toString(), 16));
			highlighter
					.addSpaceAttribute(StyleConstants.Background, spaceColor);
		}
		if (properties.containsKey("tab_color")) {
			Color tabColor = new Color(Integer.parseInt(properties.get(
					"tab_color").toString()));
			highlighter.addSpaceAttribute(StyleConstants.Background, tabColor);
		}
	}

	public void saveSettings() throws IOException {
		properties.put("default_dir", lastDir.isFile() ? lastDir.getParent()
				: lastDir.getPath());
		Color spaceColor = (Color) highlighter
				.getSpaceAttribute(StyleConstants.Background);
		properties.put("space_color", Integer.toString(spaceColor.getRGB()));
		Color tabColor = (Color) highlighter
				.getTabAttribute(StyleConstants.Background);
		properties.put("tab_color", Integer.toString(tabColor.getRGB()));
		File file = new File(getPropertiesPath());
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			file.createNewFile();
		}
		properties
				.store(new FileOutputStream(file), "Spaced IDE property file");
	}

	@Override
	public void vmStarted() {
		view.getMemoryTable().clear();
		view.getConsole().clear();
	}

	@Override
	public void vmPaused() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				view.getMemoryTable().update(
						virtualMachine.getStack().getValueMap(),
						virtualMachine.getHeap().getValueMap());
			}
		});
//		ParameterizedWhitespaceOperation pwo = virtualMachine.getCurrentPWO();
//		int docID = virtualMachine.getAppDocID();
//		StyledDocument doc = docManager.getDocument(docID);
//		SimpleAttributeSet sas = new SimpleAttributeSet();
//		StyleConstants.setBackground(sas, Color.blue);
//		doc.setCharacterAttributes(pwo.textPos, pwo.length, sas, false);
	}

	@Override
	public void vmResumed() {
//		ParameterizedWhitespaceOperation pwo = virtualMachine.getCurrentPWO();
//		int docID = virtualMachine.getAppDocID();
//		StyledDocument doc = docManager.getDocument(docID);
//		SimpleAttributeSet sas = new SimpleAttributeSet();
//		StyleConstants.setBackground(sas, Color.white);
//		doc.setCharacterAttributes(pwo.textPos, pwo.length, sas, false);
	}

	@Override
	public void vmStopped() {

	}

	@Override
	public void newOperation(ParameterizedWhitespaceOperation pwo) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					view.getMemoryTable().update(
							virtualMachine.getStack().getValueMap(),
							virtualMachine.getHeap().getValueMap());
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public WhitespaceApp parseDocument(int docID) throws WhitespaceSyntaxError {
		if (docID == DocManager.UNDEFINED_ID) {
			view.showErrorDialog("No active document!");
			return null;
		}
		Document doc = docManager.getDocument(docID);
		String sourceCode = null;
		try {
			sourceCode = doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (sourceCode == null || sourceCode.isEmpty()) {
			return null;
		}
		WhitespaceParser parser = new WhitespaceParser();
		WhitespaceApp app = parser.parse(sourceCode, docID);
		return app;
	}

	public void run() {
		int docID = view.getActiveDocumentID();
		try {
			WhitespaceApp app = parseDocument(docID);
			stop();
			virtualMachine.init(app);
			virtualMachine.start(ExecutionMode.RUN_CONTINUOUSLY);
		} catch (WhitespaceSyntaxError e) {
			view.getPrintStream().println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void stop() {
		if (virtualMachine.isRunning()) {
			virtualMachine.stop();
			Thread t = virtualMachine.getExecutionThread();
			if (t.isAlive()) {
				System.out.println("Forcing exit...");
				t.interrupt();
			}
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void debug() {
		int docID = view.getActiveDocumentID();
		try {
			WhitespaceApp app = parseDocument(docID);
			stop();
			virtualMachine.init(app);
			virtualMachine.start(ExecutionMode.RUN_TO_BREAKPOINT);
		} catch (WhitespaceSyntaxError e) {
			view.getPrintStream().println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void runStep() {
		if (virtualMachine.isRunning() && virtualMachine.isPaused()) {
			virtualMachine.setExecutionMode(ExecutionMode.RUN_STEP);
			virtualMachine.resume();
		}
	}

	public void resume() {
		if (virtualMachine.isRunning() && virtualMachine.isPaused()) {
			virtualMachine.setExecutionMode(ExecutionMode.RUN_TO_BREAKPOINT);
			virtualMachine.resume();
		}
	}

	public void quit() {
		int state = JOptionPane.showConfirmDialog(view.getFrame(),
				"Are you sure you want to exit?", "Exit",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (state == JOptionPane.OK_OPTION) {
			try {
				saveSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stop();
			view.exit();
		}
	}

	public Action getNewDocumentAction() {
		UIAction action = new UIAction("New", "newdoc.png",
				"Opens a new document", KeyStroke.getKeyStroke(KeyEvent.VK_N,
						KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int docID = docManager.createDocument();
				StyledDocument doc = docManager.getDocument(docID);
				view.openTab("New" + numNewDocs, docID, doc);
				numNewDocs++;
			}
		};
		return action;
	}

	public Action getOpenDocumentAction() {
		UIAction action = new UIAction("Open", "open.png", "Loads a document",
				KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = view.showOpenDialog(lastDir);
				if (file == null) {
					return;
				}
				lastDir = file;
				try {
					int docID = docManager.openDocument(file);
					StyledDocument doc = docManager.getDocument(docID);
					view.openTab(file.getName(), docID, doc);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		return action;

	}

	public Action getSaveDocumentAction() {
		UIAction action = new UIAction("Save", "save.png",
				"Saves the current document", KeyStroke.getKeyStroke(
						KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = view.showSaveDialog(lastDir);
				if (file == null) {
					return;
				}
				lastDir = file;
				try {
					docManager.saveDocument(view.getActiveDocumentID(), file);
				} catch (Exception e1) {
					view.showErrorDialog("Saving failed!");
					e1.printStackTrace();
				}
			}
		};
		return action;
	}

	public Action getCloseDocumentAction() {
		UIAction action = new UIAction("Close document", "close.png",
				"Closes the current document", KeyStroke.getKeyStroke(
						KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				docManager.closeDocument("");
			}
		};
		return action;
	}

	public Action getQuitAction() {
		UIAction action = new UIAction("Quit", "exit.png",
				"Quits the application", KeyStroke.getKeyStroke(KeyEvent.VK_Q,
						KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		};
		return action;
	}

	public Action getUndoAction() {
		UIAction action = new UIAction("Undo", "undo.png",
				"Undo the last change", KeyStroke.getKeyStroke(KeyEvent.VK_Z,
						KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				view.getActiveEditor().undo();
			}
		};
		return action;
	}

	public Action getRedoAction() {
		UIAction action = new UIAction("Redo", "redo.png",
				"Redo the last change", KeyStroke.getKeyStroke(KeyEvent.VK_Y,
						KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				view.getActiveEditor().redo();
			}
		};
		return action;
	}

	public Action getMergeAction() {
		UIAction action = new UIAction("Merge", "merge.png",
				"Merges the program with another text", KeyStroke.getKeyStroke(
						KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// FIXME
			}
		};
		return action;
	}

	public Action getRunAction() {
		UIAction action = new UIAction("Run", "run.png", "Runs the program",
				KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				run();
			}
		};
		return action;
	}

	public Action getDebugAction() {
		UIAction action = new UIAction("Debug", "debug.png",
				"Runs the program in debug mode", KeyStroke.getKeyStroke(
						KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				debug();
			}
		};
		return action;
	}

	public Action getStopAction() {
		UIAction action = new UIAction("Stop", "stop.png",
				"Aborts the execution", KeyStroke.getKeyStroke(KeyEvent.VK_H,
						KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		};
		return action;
	}

	public Action getRunStepAction() {
		UIAction action = new UIAction("Run step", "step.png",
				"Executes the next step", KeyStroke.getKeyStroke(KeyEvent.VK_U,
						KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				runStep();
			}
		};
		return action;
	}

	public Action getResumeAction() {
		UIAction action = new UIAction("Resume", "resume.png",
				"Continues the execution until the next breakpoint is reached",
				KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				resume();
			}
		};
		return action;
	}

	public Action getCommandDialogAction() {
		UIAction action = new UIAction("Show command dialog",
				"Opens the command dialog") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				view.getCommandDialog().setVisible(true);
			}
		};
		return action;
	}

	public Action getCreditsAction() {
		UIAction action = new UIAction("Credits", "Displays the credits") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = String
						.format("%s %s\nDeveloped by Sebastian Kreutzer 2012/2013\nSee https://sourceforge.net/projects/spacedide/ for further information.",
								TITLE, VERSION);
				JOptionPane.showMessageDialog(view.getFrame(), msg, "Credits",
						JOptionPane.INFORMATION_MESSAGE);
			}
		};
		return action;
	}

	public Action getRemoveCommentsAction() {
		UIAction action = new UIAction("Remove comments",
				"Removes all comments in the selected area") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// FIXME
			}
		};
		return action;
	}

	public Action getGenerateOutputCodeAction() {
		UIAction action = new UIAction("Generate output code",
				"Generate output code") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// FIXME
			}
		};
		return action;
	}

	public Action getAddBreakpointAction() {
		UIAction action = new UIAction("Add breakpoint", "breakpoint.png",
				"Adds a breakpoint at the current position",
				KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				int docID = view.getActiveDocumentID();
				// SimpleAttributeSet sas = new SimpleAttributeSet();
				// StyleConstants.setBackground(sas, Color.green);
				try {
					docManager.getDocument(docID).insertString(
							view.getActiveEditor().getCaretPosition(),
							WhitespaceParser.BREAKPOINT_EXPR, null);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}

				// FIXME
			}
		};
		return action;
	}

}
