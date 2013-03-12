package diagram;

import javax.swing.JLabel;

import parser.ParameterizedWhitespaceOperation;
import parser.WhitespaceOperationType;
import vm.VMListener;
import vm.WhitespaceMachine.VMState;

public class StatusBar extends JLabel implements VMListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VMState state = VMState.UNINITIALIZED;
	private ParameterizedWhitespaceOperation pwo;
	
	public StatusBar() {
		update();
	}

	@Override
	public void vmStarted() {
		state = VMState.RUNNING;
		update();
	}

	@Override
	public void vmPaused() {
		state = VMState.PAUSED;
		update();
	}

	@Override
	public void vmResumed() {
		state = VMState.RUNNING;
		update();
	}

	@Override
	public void vmStopped() {
		state = VMState.TERMINATED;
		pwo = null;
		update();
	}


	public void update() {
		StringBuilder builder = new StringBuilder();
		builder.append("<html><body>");
		builder.append("VM state: " + state + "<br>");
		if (pwo != null) {
			builder.append("Current operation: ");
			builder.append("type=" + WhitespaceOperationType.getOperationByOpcode(pwo.opcode) + "; ");
			builder.append("index=" + pwo.index + "; ");
			builder.append("pos=" + pwo.textPos + ";");
		}
		builder.append("</body></html>");
		System.out.println("update: " + builder);
		setText(builder.toString());
	}

	@Override
	public void newOperation(ParameterizedWhitespaceOperation pwo) {
		this.pwo = pwo;
//		update();
	}

}
