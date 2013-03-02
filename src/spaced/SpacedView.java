package spaced;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import parser.LanguageDefinition;
import spaced.Spaced.Actions;

public class SpacedView implements ITabManager {

	public static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

	private final String title;
	// private IAppController appController;

	private ActionMap actions;

	private JFrame frame;
	private JSplitPane mainSplitPane;
	private JSplitPane centerSplitPane;
	private JTabbedPane tabbedPane;
	private JToolBar toolBar;
	private JPanel ioPanel;
	private JScrollPane consoleScrollPane;
	private SpacedConsole console;
//	private SpacedPrompt prompt;
	private JScrollPane memoryTableScrollPane;
	private MemoryTable2 memoryTable;
	private JPanel rightPanel;
	private StatusBar statusBar;

	private JMenuBar menuBar;
	private CommandDialog dialog;
	public SpacedView(String title, ActionMap actions) {
		this.title = title;
		this.actions = actions;
		// this.appController = docManager;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					init();
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public JFrame getFrame() {
		if (frame == null) {
			frame = new JFrame(title);
			frame.setSize(DEFAULT_SIZE);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					int state = JOptionPane.showConfirmDialog(frame,
							"Are you sure you want to exit?", "Exit?",
							JOptionPane.YES_NO_OPTION);
					if (state == JOptionPane.YES_OPTION) {
						frame.setVisible(false);
						frame.dispose();
					}
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

	private void init() {
		getFrame();
	}

	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			JMenu menu;
			JMenuItem item;
			// File menu
			menu = new JMenu("File");
			item = new JMenuItem("New Document");
			item.setAction(actions.get(Actions.NEW_DOC));
			menu.add(item);
			item = new JMenuItem("Open Document");
			item.setAction(actions.get(Actions.OPEN));
			menu.add(item);
			item = new JMenuItem("Save Document");
			item.setAction(actions.get(Actions.SAVE));
			menu.add(item);
			item = new JMenuItem("Close Document");
			item.setAction(actions.get(Actions.CLOSE_DOC));
			menu.add(item);
			item = new JMenuItem("Merge");
			item.setAction(actions.get(Actions.MERGE));
			menu.add(item);
			item = new JMenuItem("Exit");
			item.setAction(actions.get(Actions.QUIT));
			menu.add(item);
			menuBar.add(menu);
			// Edit menu
			menu = new JMenu("Edit");
			item = new JMenuItem("Undo");
			item.setAction(actions.get(Actions.UNDO));
			menu.add(item);
			item = new JMenuItem("Redo");
			item.setAction(actions.get(Actions.REDO));
			menu.add(item);
			item = new JMenuItem("Add Breakpoint");
			item.setAction(actions.get(Actions.ADD_BREAKPOINT));
			menu.add(item);
			item = new JMenuItem("Remove Comments");
			item.setAction(actions.get(Actions.REMOVE_COMMENTS));
			menu.add(item);
			item = new JMenuItem("Generate Output Code");
			item.setAction(actions.get(Actions.GENERATE_OUTPUT_CODE));
			menu.add(item);
			menuBar.add(menu);
			// View menu
			menu = new JMenu("View");
			item = new JMenuItem("Show Command Dialog");
			item.setAction(actions.get(Actions.SHOW_COMMAND_DIALOG));
			menu.add(item);
			menuBar.add(menu);
			// Info menu
			menu = new JMenu("Info");
			item = new JMenuItem("Credits");
			item.setAction(actions.get(Actions.SHOW_CREDITS));
			menu.add(item);
			menuBar.add(menu);
		}
		return menuBar;
	}

	// public JDialog getMergeDialog() {
	// if (mergeDialog == null) {
	// mergeDialog = new JDialog();
	// GridLayout layout = new GridLayout(0, 3);
	// layout.setHgap(20);
	// layout.setVgap(20);
	// JPanel dialogPanel = new JPanel(layout);
	// JLabel textLabel = new JLabel("Text File:");
	// dialogPanel.add(textLabel);
	// JTextField textField = new JTextField();
	// dialogPanel.add(textField);
	// mergeDialog.setLayout(layout);
	// }
	// return mergeDialog;
	// }

	public CommandDialog getCommandDialog() {
		if (dialog == null) {
			dialog = new CommandDialog(getFrame(), this,
					LanguageDefinition.getWhitespaceLanguageDefinition());
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
			centerSplitPane.setRightComponent(getRightPanel());
			centerSplitPane.setLeftComponent(getTabbedPane());
			centerSplitPane.setPreferredSize(new Dimension(-1, 400));
		}
		return centerSplitPane;
	}

	public JPanel getRightPanel() {
		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setLayout(new BorderLayout());
			rightPanel.add(getMemoryTableScrollPane(), BorderLayout.CENTER);
//			rightPanel.add(getStatusBar(), BorderLayout.SOUTH);
		}
		return rightPanel;
	}

	// public JSplitPane getRightSplitPane() {
	// if (rightSplitPane == null) {
	// rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	// rightSplitPane.setContinuousLayout(true);
	// rightSplitPane.setLeftComponent(getTabbedPane());
	// rightSplitPane.setRightComponent(getMemoryTableScrollPane());
	// }
	// return rightSplitPane;
	// }

	// public JPanel getHelperPanel() {
	// if (helperPanel == null) {
	// helperPanel = new JPanel();
	// helperPanel.setPreferredSize(new Dimension(100, -1));
	// }
	// return helperPanel;
	// }

	public EditorTab getActiveTab() {
		if (getTabbedPane().getTabCount() == 0)
			return null;
		return (EditorTab) getTabbedPane().getSelectedComponent();
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

	// public JScrollPane getEditorScrollPane() {
	// if (editorScrollPane == null) {
	// editorScrollPane = new JScrollPane(
	// JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	// JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	// editorScrollPane.setViewportView(getEditor());
	// editorScrollPane.setPreferredSize(new Dimension(500, 400));
	// }
	// return editorScrollPane;
	// }

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

//	public SpacedPrompt getPrompt() {
//		if (prompt == null) {
//			prompt = new SpacedPrompt(getConsole().getPrintStream());
//		}
//		return prompt;
//	}

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
			button.setAction(actions.get(Actions.NEW_DOC));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(Actions.OPEN));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(Actions.SAVE));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(Actions.CLOSE_DOC));
			button.setText("");
			toolBar.add(button);

			toolBar.addSeparator();

			button = new JButton();
			button.setAction(actions.get(Actions.UNDO));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(Actions.REDO));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(Actions.ADD_BREAKPOINT));
			button.setText("");
			toolBar.add(button);

			toolBar.addSeparator();

			button = new JButton();
			button.setAction(actions.get(Actions.RUN));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(Actions.STOP));
			button.setText("");
			toolBar.add(button);

			toolBar.addSeparator();

			button = new JButton();
			button.setAction(actions.get(Actions.DEBUG));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(Actions.STEP));
			button.setText("");
			toolBar.add(button);

			button = new JButton();
			button.setAction(actions.get(Actions.RESUME));
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

	public void clearMemoryTable() {
		getMemoryTable().clear();
	}

	public void clearConsole() {
		getConsole().setText("");
	}

	public String getText() {
		EditorTab tab = getActiveTab();
		if (tab != null)
			return tab.getEditor().getText();
		return "";
	}

	public void setText(String text) {
		EditorTab tab = getActiveTab();
		if (tab != null)
			tab.getEditor().setText(text);
	}

	public int openTab(StyledDocument doc, String title) {
		JTextPane editor = new JTextPane();
		editor.setDocument(doc);
		int i = getTabbedPane().getTabCount();
		EditorTab tab = new EditorTab(editor);
		getTabbedPane().addTab(title, tab);
		getTabbedPane().setSelectedIndex(i);
		return i;
	}

	public void closeActiveTab() {
		if (getTabbedPane().getTabCount() != 0) {
			int i = getTabbedPane().getSelectedIndex();
			getTabbedPane().remove(i);
		}
	}

	public void setSelectedTabMark(boolean edited) {
		int i = getTabbedPane().getSelectedIndex();
		if (i < 0 || i >= getTabbedPane().getTabCount())
			return;
		String title = getTabbedPane().getTitleAt(i);
		if (title.endsWith("*")) {
			if (!edited) {
				title = title.substring(0, title.length() - 1);
			}
		} else {
			if (edited) {
				title = title.concat("*");
			}
		}
		getTabbedPane().setTitleAt(i, title);
	}

	public InputStream getInputStream() {
//		return getPrompt().getInputStream();
		return getConsole().getInputStream();
	}

	public PrintStream getPrintStream() {
		return getConsole().getPrintStream();
	}

	public void setSelectedTabTitle(String name) {
		if (getTabbedPane().getTabCount() > 0) {
			int i = getTabbedPane().getSelectedIndex();
			getTabbedPane().setTitleAt(i, name);
		}
	}

	public void showCredits() {
		JOptionPane
				.showMessageDialog(
						getFrame(),
						Spaced.TITLE
								+ " "
								+ Spaced.VERSION
								+ "\nDeveloped by Sebastian Kreutzer 2012/2013\nSee https://sourceforge.net/projects/spacedide/ for further information.",
						"Credits", JOptionPane.INFORMATION_MESSAGE);
	}

	public String showCodeGenDialog() {
		String result = JOptionPane.showInputDialog(getFrame(), "Insert text:",
				"Code generation", JOptionPane.OK_CANCEL_OPTION);
		return result;
	}

	public File[] showMergeDialog(File defaultDir) {
		final File defDir = defaultDir;
		final File[] files = new File[2];
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout(0, 3, 10, 10);
		panel.setLayout(layout);
		JLabel label = new JLabel("Input file: ");
		panel.add(label);
		final JTextField text = new JTextField();
		text.setEditable(false);
		panel.add(text);
		JButton button = new JButton("Select");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File f = showOpenDialog(defDir);
				if (f != null) {
					files[0] = f;
					text.setText(f.getPath());
				}
			}
		});
		panel.add(button);
		JLabel label2 = new JLabel("Ouput file: ");
		panel.add(label2);
		final JTextField text2 = new JTextField();
		text2.setEditable(false);
		panel.add(text2);
		JButton button2 = new JButton("Select");
		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File f = showOpenDialog(defDir);
				if (f != null) {
					files[1] = f;
					text2.setText(f.getPath());
				}
			}
		});
		panel.add(button2);
		JOptionPane.showOptionDialog(getFrame(), "Select file", "Merge",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new Object[] { panel, "Merge" }, 0);
		return files;
	}

	// public static void main(String[] args) {
	// SpacinatorView view = new SpacinatorView("Test", null);
	// }

}
