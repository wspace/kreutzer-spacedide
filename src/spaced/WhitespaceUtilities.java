package spaced;

import parser.WhitespaceLang;

public class WhitespaceUtilities {

	public static String toWhitespaceNumber(int n) {
		String numberString = (n >= 0 ? WhitespaceLang.PLUS
				: WhitespaceLang.MINUS)
				+ Integer.toBinaryString(Math.abs(n))
						.replace('1', WhitespaceLang.BINARY_ONE)
						.replace('0', WhitespaceLang.BINARY_ZERO)
				+ WhitespaceLang.INT_TERMINATION;
		return numberString;
	}

	public static String toWhitespaceNumber(char c) {
		return toWhitespaceNumber((int) c);
	}
	
	
	public static int parseWhitespaceNumber(String number) {
		return 0; //FIXME
	}

}
