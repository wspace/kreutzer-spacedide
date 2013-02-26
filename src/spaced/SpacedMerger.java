package spaced;

import java.util.List;

public class SpacedMerger {


	/**
	 * Contains the chars that should be replaced
	 */
	private List<Character> replaceList;

	public SpacedMerger(List<Character> replaceList) {
		this.replaceList = replaceList;
	}

	public String merge(String sourceCode, String text) {
		int codePos = 0;
		StringBuilder builder = new StringBuilder();
		char c;
		boolean replace;
		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			replace = shouldBeReplaced(c);
			if (replace) {
				if (codePos < sourceCode.length()) {
					builder.append(sourceCode.charAt(codePos));
					codePos++;
				}
			} else {
				builder.append(c);
			}
		}
		if (codePos < text.length()) {
			builder.append(sourceCode.substring(codePos));
		}
		return builder.substring(0, builder.length() -1);
	}

	public boolean shouldBeReplaced(char c) {
		return replaceList.contains(c);
	}

}
