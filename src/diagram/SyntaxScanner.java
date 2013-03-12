package diagram;


public class SyntaxScanner {

	private WhitespaceLang lang;
	private final String sourceCode;
	private int pos;

	private static int END_REACHED = -1;

	public SyntaxScanner(String sourceCode, WhitespaceLang lang) {
		this.sourceCode = sourceCode;
		this.lang = lang;
		reset();
	}

	public WhitespaceOperationType nextOperation() throws WhitespaceSyntaxError {
		String prefix = "";
		int max = lang.getMaxKeywordLength();
		for (int i = 0; i < max; i++) {
			prefix += next();
			if (lang.isKeyWord(prefix)) {
				return lang.getOperation(prefix);
			}
		}
		return WhitespaceOperationType.UNDEFINED;
	}

	public boolean hasNext() {
		return nextValidCharPos() != END_REACHED;
	}

	public int nextInteger() throws WhitespaceSyntaxError {
		char c;
		boolean negative;
		c = next();
		if (c == WhitespaceLang.MINUS) {
			negative = true;
		} else if (c == WhitespaceLang.PLUS) {
			negative = false;
		} else {
			throw new WhitespaceSyntaxError("Expected sign definition", pos);
		}

		int value = 0;
		boolean nextDigitAvailable = true;
		do {
			c = next();
			if (c == WhitespaceLang.BINARY_ZERO) {
				value <<= 1;
			} else if (c == WhitespaceLang.BINARY_ONE) {
				value = (value << 1) | 1;
			} else {
				nextDigitAvailable = false;
			}
		} while (nextDigitAvailable);
		return negative ? -value : value;
	}

	public String nextLabel() throws WhitespaceSyntaxError {
		char c;
		StringBuilder label = new StringBuilder();
		while ((c = next()) != WhitespaceLang.LABEL_TERMINATION) {
			label.append(c);
		}
		return label.toString();
	}

	private int nextValidCharPos() {
		for (int i = pos + 1; i < sourceCode.length(); i++) {
			if (!lang.isIgnored(sourceCode.charAt(i)))
				return i;
		}
		return END_REACHED;
	}

	private char next() throws WhitespaceSyntaxError {
		pos = nextValidCharPos();
		if (pos == END_REACHED) {
			throw new WhitespaceSyntaxError("Unexpected end", pos);
		}
		return sourceCode.charAt(pos);
	}

	public void reset() {
		pos = -1;
	}

	public int getPosition() {
		return pos;
	}

}
