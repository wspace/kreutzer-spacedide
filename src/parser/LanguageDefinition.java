package parser;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LanguageDefinition {

	private static LanguageDefinition whitespace;

	private final String name;
	private final String version;

	private final Map<String, WhitespaceOperationType> operationMap;

	private final char binaryZero;
	private final char binaryOne;
	private final char integerTermination;
	private final char labelTermination;

	private final char[] validChars;

	private LanguageDefinition(String name, String version,
			Map<String, WhitespaceOperationType> operationMap, char binaryZero,
			char binaryOne, char integerTermination, char labelTermination,
			char[] validChars) {
		this.name = name;
		this.version = version;
		this.operationMap = operationMap;
		this.binaryZero = binaryZero;
		this.binaryOne = binaryOne;
		this.integerTermination = integerTermination;
		this.labelTermination = labelTermination;
		this.validChars = validChars;
	}

	public Collection<String> keyWords() {
		return operationMap.keySet();
	}

	public boolean isIgnored(char c) {
		for (char cc : validChars) {
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

	public static LanguageDefinition getWhitespaceLanguageDefinition() {
		if (whitespace == null) {
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
			char[] valid = new char[] { ' ', '\t', '\n' };
			whitespace = new LanguageDefinition("Whitespace", "0.3", lang, ' ',
					'\t', '\n', '\n', valid);
		}
		return whitespace;
	}
	
	public char getBinaryZeroChar() {
		return binaryZero;
	}
	
	public char getBinaryOneChar() {
		return binaryOne;
	}
	
	public char getIntegerTerminationChar() {
		return integerTermination;
	}
	
	public char getLabelTerminationChar() {
		return labelTermination;
	}

	public static LanguageDefinition loadLanguageDefinition(File file) {
		// TODO implement
		return null;
	}

	public int getMaxKeywordLength() {
		int max = 0;
		for (String key : operationMap.keySet()) {
			max = Math.max(max, key.length());
		}
		return max;
	}

}
