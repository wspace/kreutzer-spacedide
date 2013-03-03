package parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WhitespaceLang {

	private static WhitespaceLang whitespace3;

	private final String name;
	private final String version;

	private final Map<String, WhitespaceOperationType> operationMap;

	public static final char SPACE = ' ';
	public static final char TAB = '\t';
	public static final char LINEFEED = '\n';

	public static final char MINUS = TAB;
	public static final char PLUS = SPACE;
	public static final char BINARY_ZERO = SPACE;
	public static final char BINARY_ONE = TAB;
	public static final char INT_TERMINATION = LINEFEED;
	public static final char LABEL_TERMINATION = LINEFEED;

	public static final char[] VALID_CHARS = { SPACE, TAB, LINEFEED };

	private WhitespaceLang(String name, String version,
			Map<String, WhitespaceOperationType> operationMap) {
		this.name = name;
		this.version = version;
		this.operationMap = operationMap;
	}

	public Collection<String> keyWords() {
		return operationMap.keySet();
	}

	public boolean isIgnored(char c) {
		for (char cc : VALID_CHARS) {
			if (cc == c)
				return false;
		}
		return true;
	}

	public boolean isKeyWord(String expr) {
		return operationMap.containsKey(expr);
	}

	public WhitespaceOperationType getOperation(String expr) {
		if (isKeyWord(expr)) {
			return operationMap.get(expr);
		}
		return WhitespaceOperationType.UNDEFINED;
	}

	public String getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}

	public static WhitespaceLang getWhitespaceVersion3() {
		if (whitespace3 == null) {
			HashMap<String, WhitespaceOperationType> lang = new HashMap<String, WhitespaceOperationType>();
			lang.put("  ", WhitespaceOperationType.PUSH);
			lang.put(" \n ", WhitespaceOperationType.DUPLICATE);
			lang.put(" \t ", WhitespaceOperationType.COPY_N);
			lang.put(" \n\t", WhitespaceOperationType.SWAP);
			lang.put(" \n\n", WhitespaceOperationType.POP);
			lang.put(" \t\n", WhitespaceOperationType.DISCARD_N);
			lang.put("\t   ", WhitespaceOperationType.ADD);
			lang.put("\t  \t", WhitespaceOperationType.SUBTRACT);
			lang.put("\t  \n", WhitespaceOperationType.MULTIPLY);
			lang.put("\t \t ", WhitespaceOperationType.DIVIDE);
			lang.put("\t \t\t", WhitespaceOperationType.MODULO);
			lang.put("\t\t ", WhitespaceOperationType.STORE);
			lang.put("\t\t\t", WhitespaceOperationType.RETRIEVE);
			lang.put("\n  ", WhitespaceOperationType.MARK_LABEL);
			lang.put("\n \t", WhitespaceOperationType.CALL_SUBROUTINE);
			lang.put("\n \n", WhitespaceOperationType.GOTO);
			lang.put("\n\t ", WhitespaceOperationType.GOTO_IF_ZERO);
			lang.put("\n\t\t", WhitespaceOperationType.GOTO_IF_NEGATIVE);
			lang.put("\n\t\n", WhitespaceOperationType.RETURN);
			lang.put("\n\n\n", WhitespaceOperationType.EXIT);
			lang.put("\t\n  ", WhitespaceOperationType.PRINT_CHAR);
			lang.put("\t\n \t", WhitespaceOperationType.PRINT_INT);
			lang.put("\t\n\t ", WhitespaceOperationType.READ_CHAR);
			lang.put("\t\n\t\t", WhitespaceOperationType.READ_INT);
			whitespace3 = new WhitespaceLang("Whitespace", "0.3", lang);
		}
		return whitespace3;
	}

	public static WhitespaceLang getLatestVersion() {
		return getWhitespaceVersion3();
	}

	public int getMaxKeywordLength() {
		int max = 0;
		for (String key : operationMap.keySet()) {
			max = Math.max(max, key.length());
		}
		return max;
	}

}
