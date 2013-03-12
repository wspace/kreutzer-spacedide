package diagram;

import java.util.HashMap;

public enum WhitespaceOperationType {
	// Stack
	PUSH(0x00, ArgType.INTEGER), POP(0x01, ArgType.NONE), DUPLICATE(0x02, ArgType.NONE), SWAP(0x03, ArgType.NONE), COPY_N(0x04, ArgType.INTEGER), DISCARD_N(
			0x05, ArgType.INTEGER),
	// Arithmetic
	ADD(0x10, ArgType.NONE), SUBTRACT(0x11, ArgType.NONE), MULTIPLY(0x12, ArgType.NONE), DIVIDE(0x13, ArgType.NONE), MODULO(0x14, ArgType.NONE),
	// Heap
	STORE(0x20, ArgType.NONE), RETRIEVE(0x21, ArgType.NONE),
	// Flow control
	MARK_LABEL(0x30, ArgType.LABEL), GOTO(0x31, ArgType.LABEL), CALL_SUBROUTINE(0x32, ArgType.LABEL), RETURN(0x33, ArgType.NONE), GOTO_IF_ZERO(
			0x34, ArgType.LABEL), GOTO_IF_NEGATIVE(0x35, ArgType.LABEL), EXIT(0x36, ArgType.NONE),
	// I/O
	PRINT_CHAR(0x40, ArgType.NONE), PRINT_INT(0x41, ArgType.NONE), READ_CHAR(0x42, ArgType.NONE), READ_INT(0x43, ArgType.NONE),
	// Undefined
	UNDEFINED(0xFF, ArgType.NONE);

	/**
	 * Stores all opcodes
	 */
	private static HashMap<Integer, WhitespaceOperationType> opRegistry;
	
	/**
	 * Unique operation code
	 */
	public final int opcode;
	
	/**
	 * Indicates if the operation needs an argument
	 */
	public final ArgType argType;

	public enum ArgType {
		INTEGER, LABEL, NONE
	}
	
	private WhitespaceOperationType(int opcode, ArgType argType) {
		this.opcode = opcode;
		this.argType = argType;;
		register();
	}

	private void register() {
		if (opRegistry == null) {
			opRegistry = new HashMap<Integer, WhitespaceOperationType>();
		}
		opRegistry.put(opcode, this);
	}

	public static WhitespaceOperationType getOperationByOpcode(int opcode) {
		if (opRegistry.containsKey(opcode))
			return opRegistry.get(opcode);
		return UNDEFINED;
	}
}
