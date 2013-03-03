package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import parser.WhitespaceLang;
import spaced.DocManager;
import spaced.SpacedNew.ActionType;

public class SpacedViewNew implements ITabManager {

	public static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

	private JFrame frame;

	private JSplitPane mainSplitPane;
	private JSplitPane centerSplitPane;

	private JTabbedPane tabbedPane;

	private JMenuBar menuBar;
	private JToolBar toolBar;

	private CommandDialog dialog;

	private JPanel ioPanel;

	private JScrollPane consoleScrollPane;
	private JScrollPane memoryTableScrollPane;

	private SpacedConsole console;

	private MemoryTable2 memoryTable;
	private StatusBar statusBar;

	private String title;
	private ActionMap actions;

	/**
	 * Links an editor to a given document ID
	 */
	private HashMap<Integer, SpacedEditor> editorMap;

	/**
	 * Links a document ID to a tab index
	 */
	private HashMap<Integer, Integer> indexMap;

	public SpacedViewNew(String title, ActionMap actions) {
		this.title = title;
		this.actions = actions;
		editorMap = new HashMap<Integer, SpacedEditor>();
		indexMap = new HashMap<Integer, Integer>();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				init();
			}
		});

	}

	public void init() {
		getFrame();
	}

	public JFrame getFrame() {
		if (frame == null) {
			frame = new JFrame(title);
			frame.setSize(DEFAULT_SIZE);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					actions.get(ActionType.QUIT).actionPerformed(null);
				}

			});
			frame.setLocationRelativeTo(null);
			frame.getContentPane().add(getMainSplitPane(), BorderLayout.CENTER);
			frame.getContentPane().add(getToolBar(), BorderLayout.PAGE_START);
			frame.setJMenuBar(getMenuBar());
			frame.setVisible(true);
		}
		return frame;
	}

	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			JMenu menu;
			JMenuItem item;
			// File menu
			menu = new JMenu("File");
			item = new JMenuItem("New Document");
			item.setAction(actions.get(ActionType.NEW_DOC));
			menu.add(item);
			item = new JMenuItem("Open Document");
			item.setAction(actions.get(ActionType.OPEN));
			menu.add(item);
			item = new JMenuItem("Save Document");
			item.setAction(actions.get(ActionType.SAVE));
			menu.add(item);
			item = new JMenuItem("Close Document");
			item.setAction(actions.get(ActionType.CLOSE_DOC));
			menu.add(item);
			item = new JMenuItem("Merge");
			item.setAction(actions.get(ActionType.MERGE));
			menu.add(item);
			item = new JMenuItem("Exit");
			item.setAction(actions.get(ActionType.QUIT));
			menu.add(item);
			menuBar.add(menu);
			// Edit menu
			menu = new JMenu("Edit");
			item = new JMenuItem("Undo");
			item.setAction(actions.get(ActionType.UNDO));
			menu.add(item);
			item = new JMenuItem("Redo");
			item.setAction(actions.get(ActionType.REDO));
			menu.add(item);
			item = new JMenuItem("Add Breakpoint");
			item.setAction(actions.get(ActionType.ADD_BREAKPOINT));
			menu.add(item);
			item = new JMenuItem("Remove Comments");
			item.setAction(actions.get(ActionType.REMOVE_COMMENTS));
			menu.add(item);
			item = new JMenuItem("Generate Output Code");
			item.setAction(actions.get(ActionType.GENERATE_OUTPUT_CODE));
			menu.add(item);
			menuBar.add(menu);
			// Run menu
			menu = new JMenu("Run");
			item = new JMenuItem();
			item.setAction(actions.get(ActionType.RUN));
			menu.add(item);
			item = new JMenuItem();
			item.setAction(actions.get(ActionType.STOP));
			menu.add(item);
			item = new JMenuItem();
			item.setAction(actions.get(ActionType.DEBUG));
			menu.add(item);
			item = new JMenuItem();
			item.setAction(actions.get(ActionType.RESUME));
			menu.add(item);
			item = new JMenuItem();
			item.setAction(actions.get(ActionType.STEP));
			menu.add(item);
			menuBar.add(menu);
			// View menu
			menu = new JMenu("View");
			item = new JMenuItem("Show Command Dialog");
			item.setAction(actions.get(ActionType.SHOW_COMMAND_DIALOG));
			menu.add(item);
			menuBar.add(menu);
			// Info menu
			menu = new JMenu("Info");
			item = new JMenuItem("Credits");
			item.setAction(actions.get(ActionType.SHOW_CREDITS));
			menu.add(item);
			menuBar.add(menu);
		}
		return menuBar;
	}

	public CommandDialog getCommandDialog() {
		if (dialog == null) {
			dialog = new CommandDialog(getFrame(), this,
					WhitespaceLang.getLatestVersion());
			dialog.setLocationRelativeTo(getFrame());
		}
		return dialog;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setPreferredSize(new Dimension(600, 400));
		}
		return tabbedPane;
	}

	public JSplitPane getMainSplitPane() {
		if (mainSplitPane == null) {
			mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			mainSplitPane.setContinuousLayout(true);
			mainSplitPane.setDividerSize(4);
			mainSplitPane.setTopComponent(getCenterSplitPane());
			mainSplitPane.setBottomComponent(getIOPanel());
		}
		return mainSplitPane;
	}

	public JSplitPane getCenterSplitPane() {
		if (centerSplitPane == null) {
			centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			centerSplitPane.setContinuousLayout(true);
			centerSplitPane.setDividerSize(4);
			centerSplitPane.setRightComponent(getMemoryTableScrollPane());
			centerSplitPane.setLeftComponent(getTabbedPane());
			centerSplitPane.setPreferredSize(new Dimension(-1, 400));
		}
		return centerSplitPane;
	}

	public SpacedConsole getConsole() {
		if (console == null) {
			console = new SpacedConsole();
		}
		return console;
	}

	public MemoryTable2 getMemoryTable() {
		if (memoryTable == null) {
			memoryTable = new MemoryTable2();
		}
		return memoryTable;
	}

	public StatusBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new StatusBar();
		}
		return statusBar;
	}

	public JPanel getIOPanel() {
		if (ioPanel == null) {
			ioPanel = new JPanel();
			ioPanel.setLayout(new BorderLayout());
			ioPanel.add(getConsoleScrollPane(), BorderLayout.CENTER);
			ioPanel.add(getStatusBar(), BorderLayout.SOUTH);
			ioPanel.setPreferredSize(new Dimension(-1, 100));
		}
		return ioPanel;
	}

	public JScrollPane getConsoleScrollPane() {
		if (consoleScrollPane == null) {
			consoleScrollPane = new JScrollPane(
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			consoleScrollPane.setViewportView(getConsole());
		}
		return consoleScrollPane;
	}

	public JScrollPane getMemoryTableScrollPane() {
		if (memoryTableScrollPane == null) {
			memoryTableScrollPane = new JScrollPane();
			memoryTableScrollPane.setViewportView(getMemoryTable());
			memoryTableScrollPane.setPreferredSize(new Dimension(-1, -1));
		}
		return memoryTableScrollPane;
	}

	public JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();

			JButton button = new JButton();
			button.setAction(actions.get(ActionType.NEW_DOC));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(ActionType.OPEN));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(ActionType.SAVE));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(ActionType.CLOSE_DOC));
			button.setText("");
			toolBar.add(button);

			toolBar.addSeparator();

			button = new JButton();
			button.setAction(actions.get(ActionType.UNDO));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(ActionType.REDO));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(ActionType.ADD_BREAKPOINT));
			button.setText("");
			toolBar.add(button);

			toolBar.addSeparator();

			button = new JButton();
			button.setAction(actions.get(ActionType.RUN));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(ActionType.STOP));
			button.setText("");
			toolBar.add(button);

			toolBar.addSeparator();

			button = new JButton();
			button.setAction(actions.get(ActionType.DEBUG));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(ActionType.STEP));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(ActionType.RESUME));
			button.setText("");
			toolBar.add(button);
		}
		return toolBar;
	}

	public File showOpenDialog(File defaultDir) {
		JFileChooser fileChooser = new JFileChooser(defaultDir);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int state = fileChooser.showOpenDialog(getFrame());
		if (state == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	public File showSaveDialog(File defaultDir) {
		JFileChooser fileChooser = new JFileChooser(defaultDir);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int state = fileChooser.showSaveDialog(getFrame());
		if (state == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	public void openTab(String title, int docID, StyledDocument doc) {
		SpacedEditor editor = new SpacedEditor();
		editor.loadDocument(doc, docID);
		editorMap.put(docID, editor);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(editor);
		getTabbedPane().addTab(title, scrollPane);
		getTabbedPane().setSelectedComponent(scrollPane);
		int index = getTabbedPane().getSelectedIndex();
		indexMap.put(index, docID);
	}

	public SpacedEditor getEditor(int docID) {
		return editorMap.get(docID);
	}

	public int getActiveDocumentID() {
		int index = getTabbedPane().getSelectedIndex();
		if (indexMap.containsKey(index)) {
			return indexMap.get(index);
		}
		return DocManager.UNDEFINED_ID;
	}

	public SpacedEditor getActiveEditor() {
		return getEditor(getActiveDocumentID());
	}

	public InputStream getInputStream() {
		return getConsole().getInputStream();
	}

	public PrintStream getPrintStream() {
		return getConsole().getPrintStream();
	}

	public void showErrorDialog(String msg) {
		JOptionPane.showMessageDialog(getFrame(), msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void exit() {
		getFrame().dispose();
	}

}
