package vm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.swing.event.EventListenerList;

import parser.ParameterizedWhitespaceOperation;
import parser.WhitespaceApp;
import parser.WhitespaceOperationType;

public class WhitespaceMachine implements Runnable {

	public static final int DEFAULT_HEAP_SIZE = 1024;
	public static final int DEFAULT_STACK_SIZE = 1024;

	public enum VMState {
		UNINITIALIZED, READY, RUNNING, PAUSED, TERMINATION_REQUESTED, TERMINATED
	}

	public enum ExecutionMode {
		RUN_CONTINUOUSLY, RUN_STEP, RUN_TO_BREAKPOINT
	}

	public static final int INTEGER_TERMINATION_CHAR = 10;

	private EventListenerList listeners = new EventListenerList();

	private VMState state;
	private ExecutionMode executionMode;

	private InputStream in;
	private PrintStream out;

	private Heap heap;
	private Stack stack;
	private Stack subroutines;

	private WhitespaceApp program;
	private int iPtr;

	private Thread executionThread;

	public WhitespaceMachine(int heapSize, int stackSize, InputStream in,
			PrintStream out) {
		this.in = in;
		this.out = out;
		heap = new Heap(heapSize);
		stack = new Stack(stackSize, out);
		subroutines = new Stack(128, out);
		state = VMState.UNINITIALIZED;
	}

	public WhitespaceMachine(InputStream in, PrintStream out) {
		this(DEFAULT_HEAP_SIZE, DEFAULT_STACK_SIZE, in, out);
	}

	public void init(WhitespaceApp program) {
		if (program == null)
			throw new WhitespaceRuntimeException("Program is null");
		this.program = program;
		reset();
		state = VMState.READY;
	}

	// public void init(String bytecode) {
	// init(WhitespaceApp.load(bytecode));
	// }

	public void addStackListener(StackListener l) {
		stack.addStackListener(l);
	}

	public void removeStackListener(StackListener l) {
		stack.removeStackListener(l);
	}

	public void addHeapAccessListener(HeapAccessListener l) {
		heap.addHeapAccessListener(l);
	}

	public void removeHeapAccessListener(HeapAccessListener l) {
		heap.removeHeapAccessListener(l);
	}

	public void addVMListener(VMListener l) {
		listeners.add(VMListener.class, l);
	}

	public void run() {
		if (state == VMState.UNINITIALIZED)
			throw new RuntimeException("Virtual machine is not initialized");
		state = VMState.RUNNING;
		boolean runStep = false;
		int lastBreakPoint = -1;
		for (VMListener l : listeners.getListeners(VMListener.class)) {
			l.vmStarted();
		}
		while (state != VMState.TERMINATION_REQUESTED) {
			switch (state) {
			case PAUSED:
				Thread.yield();
				break;
			case RUNNING:
				runStep = false;
				switch (executionMode) {
				case RUN_TO_BREAKPOINT:
					runStep = true;
					if (program.isBreakPoint(iPtr) && iPtr != lastBreakPoint) {
						state = VMState.PAUSED;
						runStep = false;
						lastBreakPoint = iPtr;
					}
					break;
				case RUN_STEP:
					runStep = true;
					state = VMState.PAUSED;
					for (VMListener l : listeners
							.getListeners(VMListener.class)) {
						l.vmPaused();
					}
					break;
				case RUN_CONTINUOUSLY:
					runStep = true;
					// long time = System.nanoTime();
					// System.out.println("Delta[ms]:"
					// + ((System.nanoTime() - time) / 1000000.0f));
					// System.out.println(stack);
					break;
				default:
					break;
				}
				if (runStep) {
					executeNext();
				}
				break;
			default:
				throw new WhitespaceRuntimeException(
						"Virtual machine is in bad state");
			}
		}
		state = VMState.TERMINATED;
		for (VMListener l : listeners.getListeners(VMListener.class)) {
			l.vmStopped();
		}
		
		executionThread = null;
	}

	public void start(ExecutionMode mode) {
		if (state == VMState.UNINITIALIZED)
			throw new WhitespaceRuntimeException(
					"Virtual machine is not initialized");
		if (state != VMState.READY
				|| (executionThread != null && executionThread.isAlive()))
			throw new WhitespaceRuntimeException(
					"Virtual machine is already running");
		this.executionMode = mode;
		executionThread = new Thread(this);
		executionThread.start();
	}

	private void executeNext() {
		ParameterizedWhitespaceOperation pwo = program.getOperation(iPtr);// ops[iPtr];
		iPtr++;
		int opcode = pwo.opcode;
		WhitespaceOperationType type = WhitespaceOperationType
				.getOperationByOpcode(opcode);
		// System.out.println(type);
		for (VMListener l : listeners.getListeners(VMListener.class)) {
			l.newOperation(pwo);
		}
		int temp;
		switch (type) {
		case PUSH:
			stack.push(pwo.args[0]);
			break;
		case POP:
			stack.pop();
			break;
		case DUPLICATE:
			stack.copyToTop(0);
			break;
		case SWAP:
			stack.swapTopElements();
			break;
		case COPY_N:
			stack.copyToTop(pwo.args[0]);
			break;
		case DISCARD_N:
			stack.discardElements(pwo.args[0]);
			break;
		case STORE:
			int value = stack.pop();// stack.topElement();
			int address = stack.pop();// stack.getElement(1);
			heap.store(address, value);
			break;
		case RETRIEVE:
			stack.push(heap.load(stack.pop()));
			break;
		case ADD:
			stack.push(stack.pop() + stack.pop());
			break;
		case SUBTRACT:
			temp = stack.pop();
			stack.push(stack.pop() - temp);
			break;
		case MULTIPLY:
			stack.push(stack.pop() * stack.pop());
			break;
		case DIVIDE:
			temp = stack.pop();
			stack.push(stack.pop() / temp);
			break;
		case MODULO:
			temp = stack.pop();
			stack.push(stack.pop() % temp);
			break;
		case MARK_LABEL:
			System.err.println("You shouldn't get here");
			break;
		case GOTO:
			jumpToLabel(pwo.args[0]);
			break;
		case GOTO_IF_ZERO:
			temp = stack.pop();
			jumpToLabel(pwo.args[0], temp == 0);
			break;
		case GOTO_IF_NEGATIVE:
			temp = stack.pop();
			jumpToLabel(pwo.args[0], temp < 0);
			break;
		case CALL_SUBROUTINE:
			subroutines.push(iPtr);
			jumpToLabel(pwo.args[0]);
			break;
		case RETURN:
			temp = subroutines.pop();
			iPtr = temp;
			break;
		case EXIT:
			stop();
			break;
		case PRINT_INT:
			out.print(stack.pop());
			break;
		case PRINT_CHAR:
			out.print((char) stack.pop());
			break;
		case READ_INT:
			temp = readInt();
			heap.store(stack.pop(), temp);
			break;
		case READ_CHAR:
			temp = readChar();
			heap.store(stack.pop(), temp);
			System.out.println("Char read: " + temp);
			break;
		case UNDEFINED:
			reportError("Reached undefined operation!");
			// TODO Print error message
			break;
		default:
			break;
		}
	}

	private void reportError(String msg) {
		ParameterizedWhitespaceOperation pwo = program.getOperation(iPtr - 1);
		int pos = pwo.textPos;
		WhitespaceOperationType op = WhitespaceOperationType
				.getOperationByOpcode(pwo.opcode);
		String err = String.format("Error at %d (%s): %s", pos, op, msg);
		out.println(err);
	}

	private int readChar() {
		try {
			int c = in.read();
			return c;
		} catch (IOException e) {
			e.printStackTrace();
			reportError("Could not read input stream!");
		}
		return 0;
	}

	private int readInt() {
		StringBuilder builder = new StringBuilder();
		int c;
		while ((c = readChar()) != INTEGER_TERMINATION_CHAR) {
			builder.append((char) c);
		}
		try {
			int i = Integer.parseInt(builder.toString());
			return i;
		} catch (NumberFormatException e) {
			reportError("Excpected an integer as input!");
			stop();
		}
		return 0;
	}

	private void jumpToLabel(int position) {
		if (isValidPosition(position)) {
			iPtr = position;
		} else {
			String msg = "Error: Unable to jump to label at " + position;
			out.println(msg);
			throw new WhitespaceRuntimeException(msg);
		}
	}

	private void jumpToLabel(int position, boolean condition) {
		if (condition)
			jumpToLabel(position);
	}

	public boolean isValidPosition(int n) {
		return n >= 0 && n < program.getOperationCount();
	}

	public void stop() {
		if (isRunning()) {
			state = VMState.TERMINATION_REQUESTED;
		}
	}

	private void reset() {
		heap.reset();
		stack.reset();
		subroutines.reset();
		iPtr = 0;
	}

	public boolean isRunning() {
		return state == VMState.RUNNING || state == VMState.PAUSED;
	}

	public boolean isPaused() {
		return state == VMState.PAUSED;
	}

	public void resume() {
		if (isPaused()) {
			state = VMState.RUNNING;
			for (VMListener l : listeners.getListeners(VMListener.class)) {
				l.vmResumed();
			}
		}
	}

	public VMState getState() {
		return state;
	}

	public void setExecutionMode(ExecutionMode mode) {
		this.executionMode = mode;
	}

	public Thread getExecutionThread() {
		return executionThread;
	}

	public ParameterizedWhitespaceOperation getCurrentPWO() {
		if (isRunning()) {
			return program.getOperation(iPtr);
		}
		return null;
	}

	public ParameterizedWhitespaceOperation getNextPwo() {
		if (isRunning() && isValidPosition(iPtr + 1)) {
			return program.getOperation(iPtr + 1);
		}
		return null;
	}

	public Stack getStack() {
		return stack;
	}

	public Heap getHeap() {
		return heap;
	}

}
