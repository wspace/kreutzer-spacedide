package spaced;

import javax.swing.ActionMap;

import vm.WhitespaceMachine;

public class SpacedNew {
	
	public enum UIActions {
		NEW_DOC, OPEN, SAVE, CLOSE_DOC, QUIT, UNDO, REDO, MERGE, INSERT_COMMAND, RUN, STOP, DEBUG, RESUME, STEP, SHOW_COMMAND_DIALOG, SHOW_CREDITS, REMOVE_COMMENTS, GENERATE_OUTPUT_CODE, ADD_BREAKPOINT
	}

	public static final String TITLE = "Spaced IDE";
	public static final String VERSION = "1.1.0";
	
	private SpacedView view;
	private WhitespaceMachine virtualMachine;
	
	public SpacedNew() {
		view = new SpacedView(TITLE, null);
	}
	
	
}
