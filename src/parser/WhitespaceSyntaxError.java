package parser;

public class WhitespaceSyntaxError extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WhitespaceSyntaxError(String msg, int pos) {
		super(String.format("Parsing Exception at %d: %s", pos, msg));
	}
}
