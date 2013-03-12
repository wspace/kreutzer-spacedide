package diagram;

/**
 * <p>
 * A wrapper for whitespace commands.<br>
 * Holds all important information like opcode, position in the source code,
 * arguments and a possible debug flag
 * </p>
 * 
 * @author Sebastian Kreutzer
 * 
 */
public class ParameterizedWhitespaceOperation {

	/**
	 * Position in the text
	 */
	public final int textPos;
	/**
	 * Number of chars
	 */
	public final int length;
	public final int index;
	public final int opcode;
	public final int[] args;

	public ParameterizedWhitespaceOperation(int textPos, int length, int index,
			int opcode, int... argument) {
		this.textPos = textPos;
		this.length = length;
		this.index = index;
		this.opcode = opcode;
		this.args = argument;
	}

}
