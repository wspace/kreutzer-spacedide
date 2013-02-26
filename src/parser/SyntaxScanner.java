package parser;

import vm.WhitespaceRuntimeException;

public class SyntaxScanner {

	private LanguageDefinition language;
	private final String sourceCode;
	private int pos;
	
	private static int END_REACHED = -1;

	public SyntaxScanner(String sourceCode, LanguageDefinition language) {
		this.sourceCode = sourceCode;
		this.language = language;
		reset();
	}

	public WhitespaceOperationType nextOperation() {
		String prefix = "";
		int max = language.getMaxKeywordLength();
		for (int i = 0; i < max; i++) {
			prefix += next();
			if (language.isKeyWord(prefix)) {
				return language.getOperation(prefix);
			}
		}
		return WhitespaceOperationType.UNDEFINED;
	}

	public boolean hasNext() {
		return nextValidCharPos() != END_REACHED;
	}

	public int nextInteger() {
		char c;
		boolean negative;
		c = next();
		if (c == language.getBinaryOneChar()) {
			negative = true;
		} else if (c == language.getBinaryZeroChar()) {
			negative = false;
		} else {
			throw new WhitespaceSyntaxError("Expected sign definition", pos);
		}
		
		int value = 0;
		boolean nextDigitAvailable = true;
		do {
			c = next();
			if (c == language.getBinaryZeroChar()) {
				value <<= 1;
			} else if (c == language.getBinaryOneChar()) {
				value = (value << 1) | 1;
			} else {
				nextDigitAvailable = false;
			}
		} while (nextDigitAvailable);
		return negative ? -value : value;
	}

	public String nextLabel() {
		char c;
		StringBuilder label = new StringBuilder();
		while ((c = next()) != language.getLabelTerminationChar()) {
			label.append(c);
		}
		return label.toString();
	}

	private int nextValidCharPos() {
		for (int i = pos + 1; i < sourceCode.length(); i++) {
			if (!language.isIgnored(sourceCode.charAt(i)))
				return i;
		}
		return END_REACHED;
	}

	private char next() {
		pos = nextValidCharPos();
		if (pos == END_REACHED)
			throw new WhitespaceRuntimeException("Unexpected end");
		return sourceCode.charAt(pos);
	}
	
	public void reset() {
		pos = -1;
	}

	public int getPosition() {
		return pos;
	}

}
