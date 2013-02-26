package parser;

/**
 * <p>A wrapper for whitespace commands.<br>
 * Holds all important information like opcode, position in the source code, arguments and a possible debug flag</p>   
 * 
 * @author Sebastian Kreutzer
 *
 */
public class ParameterizedWhitespaceOperation {
	
	public final int textPos;
	public final int index;
	public final int opcode;
	public final int[] args;

	public ParameterizedWhitespaceOperation(int textPos, int index, int opcode, int... argument) {
		this.textPos = textPos;
		this.index = index;
		this.opcode = opcode;
		this.args = argument;
	}
	
}
