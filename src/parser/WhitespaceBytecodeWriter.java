package parser;

import vm.WhitespaceRuntimeException;

public class WhitespaceBytecodeWriter {

	private boolean closed;

	private StringBuilder bytecode;
	int opCounter = 0;

	public WhitespaceBytecodeWriter() {
		bytecode = new StringBuilder();
		closed = false;
	}

	public void compile(ParameterizedWhitespaceOperation op) {
		if (op == null)
			throw new WhitespaceRuntimeException("Operation is null");
		bytecode.append(Integer.toHexString(opCounter));
		bytecode.append(" ");
		bytecode.append(Integer.toHexString(op.textPos));
		bytecode.append(" ");
		bytecode.append(Integer.toHexString(op.index));
		bytecode.append(" ");
		bytecode.append(Integer.toHexString(op.opcode));
		for (int arg : op.args) {
			//Integer.toHexString() does some weird things with negative integers
			String argHex = Integer.toHexString(Math.abs(arg));
			if (arg < 0)
				argHex = "-" + argHex;
			bytecode.append(" ");
			bytecode.append(argHex);
		}
		bytecode.append("\n");
		opCounter++;
	}

	public void close() {
		closed = true;
	}

	public String getBytecode() {
		if (!closed)
			throw new WhitespaceRuntimeException(
					"Bytecode writer is not closed yet");
		return bytecode.toString();
	}

}
