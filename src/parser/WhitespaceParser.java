package parser;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import parser.WhitespaceOperationType.ArgType;

public class WhitespaceParser {

	public static final String BREAKPOINT_EXPR = "#DBG_BREAK";

	// private final String sourceCode;

	public WhitespaceParser() {
		// this.sourceCode = sourceCode;
	}

	public WhitespaceApp parse(String sourceCode, int docID) throws WhitespaceSyntaxError {
		SyntaxScanner scanner = new SyntaxScanner(sourceCode,
				WhitespaceLang.getLatestVersion());
		HashMap<String, Integer> labels = new HashMap<String, Integer>();

		// Scanning for label declarations
		int i = 0;
		while (scanner.hasNext()) {
			WhitespaceOperationType op = scanner.nextOperation();
			if (op == WhitespaceOperationType.MARK_LABEL) {
				labels.put(scanner.nextLabel(), i);
			} else {
				// Label marks are not passed to the VM
				if (op.argType == ArgType.INTEGER)
					scanner.nextInteger();
				else if (op.argType == ArgType.LABEL)
					scanner.nextLabel();
				i++;
			}
		}

		scanner.reset();

		// Scanning all operations and replacing label identifiers
		ParameterizedWhitespaceOperation[] operations = new ParameterizedWhitespaceOperation[i];
		i = 0;
		while (scanner.hasNext()) {
			WhitespaceOperationType op = scanner.nextOperation();

			if (op == WhitespaceOperationType.UNDEFINED) {
				throw new WhitespaceSyntaxError("Undefined operation",
						scanner.getPosition());
			}

			// Label marks are skipped
			if (op == WhitespaceOperationType.MARK_LABEL) {
				scanner.nextLabel(); // Not doing this will confuse the scanner
				continue;
			}
			boolean hasArg = false;
			int arg = 0;
			switch (op.argType) {
			case NONE:
				hasArg = false;
				break;
			case INTEGER:
				hasArg = true;
				arg = scanner.nextInteger();
				break;
			case LABEL:
				hasArg = true;
				String label = scanner.nextLabel();
				if (!labels.containsKey(label)) {
					throw new WhitespaceSyntaxError("Undefined label",
							scanner.getPosition());
				}
				arg = labels.get(label);
				break;
			}
			ParameterizedWhitespaceOperation pwo;
			if (hasArg)
				pwo = new ParameterizedWhitespaceOperation(
						scanner.getPosition(), WhitespaceLang
								.getLatestVersion().getMaxKeywordLength(), i, 
						op.opcode, arg); // FIXME Length is not accurate
			else {
				pwo = new ParameterizedWhitespaceOperation(
						scanner.getPosition(), WhitespaceLang
								.getLatestVersion().getMaxKeywordLength(), i,
						op.opcode);
			}
			operations[i] = pwo;
			i++;
		}

		List<Integer> breakPoints = new ArrayList<Integer>();
		int pos = 0;
		for (int startIndex = 0; (pos = sourceCode.indexOf(BREAKPOINT_EXPR,
				startIndex)) != -1; startIndex = pos + 1) {
			for (ParameterizedWhitespaceOperation op : operations) {
				if (op.textPos >= pos) {
					breakPoints.add(op.index);
					System.out.println("Breakpoint found: index=" + op.index);
					break;
				}
			}
		}

		return new WhitespaceApp(operations, breakPoints, docID);
	}
}
