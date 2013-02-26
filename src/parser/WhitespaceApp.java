package parser;

import java.util.List;


public class WhitespaceApp {

	private ParameterizedWhitespaceOperation[] operations;
	private List<Integer> breakPoints;
	
	public WhitespaceApp(ParameterizedWhitespaceOperation[] operations, List<Integer> breakPoints) {
		this.operations = operations;
		this.breakPoints = breakPoints;
	}

	public ParameterizedWhitespaceOperation getOperation(int iPtr) {
		if (iPtr < 0 || iPtr >= operations.length)
			throw new RuntimeException("Instruction pointer out of bounds");
		return operations[iPtr];
	}
	
	public boolean isBreakPoint(int iPtr) {
		return breakPoints.contains(iPtr);
	}

//	public static WhitespaceApp load(String bytecode) {
//		Scanner scanner = new Scanner(bytecode);
//		List<ParameterizedWhitespaceOperation> opList = new ArrayList<ParameterizedWhitespaceOperation>();
//		Scanner lineScanner;
//		int[] opData = new int[4];
//		List<Integer> args = new ArrayList<Integer>();
//		int[] argArray;
//		int radix = 16; // Hex
//		while (scanner.hasNextLine()) {
//			lineScanner = new Scanner(scanner.nextLine());
//			for (int i = 0; i < opData.length; i++) {
//				if (!lineScanner.hasNextInt(radix))
//					throw new WhitespaceRuntimeException("Corrupt bytecode");
//				opData[i] = lineScanner.nextInt(radix);
//			}
//			args.clear();
//			while (lineScanner.hasNextInt(radix)) {
//				args.add(lineScanner.nextInt(radix));
//			}
//			argArray = new int[args.size()];
//			int i = 0;
//			for (int arg : args) {
//				argArray[i++] = arg;
//			}
//			opList.add(opData[0], new ParameterizedWhitespaceOperation(opData[1],
//					opData[2], opData[3], argArray));
//		}
//		ParameterizedWhitespaceOperation[] operations = new ParameterizedWhitespaceOperation[opList.size()];
//		return new WhitespaceApp((ParameterizedWhitespaceOperation[]) (opList.toArray(operations)));
//	}
//
//	public String toBytecode() {
//		StringBuilder bytecode = new StringBuilder();
//		int opCounter = 0;
//		for (ParameterizedWhitespaceOperation op : operations) {
//			if (op == null)
//				throw new WhitespaceRuntimeException("Operation is null");
//			bytecode.append(Integer.toHexString(opCounter));
//			bytecode.append(" ");
//			bytecode.append(Integer.toHexString(op.textPos));
//			bytecode.append(" ");
//			bytecode.append(Integer.toHexString(op.debugFlag));
//			bytecode.append(" ");
//			bytecode.append(Integer.toHexString(op.opcode));
//			for (int arg : op.args) {
//				// Integer.toHexString() does some weird things with negative
//				// integers
//				String argHex = Integer.toHexString(Math.abs(arg));
//				if (arg < 0)
//					argHex = "-" + argHex;
//				bytecode.append(" ");
//				bytecode.append(argHex);
//			}
//			bytecode.append("\n");
//			opCounter++;
//		}
//		return bytecode.toString();
//	}

	public int getOperationCount() {
		return operations.length;
	}

}
