package diagram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 * TODO Has to be worked over, IO is behaving pretty badly so far
 * 
 * @author Sebastian
 * 
 */
public class SpacedConsole extends JTextPane /* implements DocumentListener */{

	private static final long serialVersionUID = 1L;

	private LinkedList<Integer> holder = new LinkedList<Integer>();
	private boolean waitingForInput;

	private PrintStream printStream;
	private InputStream inputStream;

	public SpacedConsole() {
		// setEditable(false);
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					synchronized (holder) {
						if (waitingForInput) {
							char c = e.getDocument().getText(e.getOffset(), 1)
									.charAt(0);
//							System.out.println("Char: " + c);
							holder.add((int) c);
							holder.notify();
						}
					}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});
	}

	private void append(char c) {
		String text = getText();
		text += c;
		setText(text);
	}

	public PrintStream getPrintStream() {
		if (printStream == null) {
			printStream = new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					append((char) b);
				}
			});
		}
		return printStream;
	}

	public InputStream getInputStream() {
		if (inputStream == null) {
			inputStream = new InputStream() {

				@Override
				public int read() throws IOException {
					try {
						synchronized (holder) {
							setCaretPosition(getDocument().getEndPosition()
									.getOffset() - 1);
							waitingForInput = true;
							while (holder.isEmpty()) {
								holder.wait();
							}
							waitingForInput = false;
							int n = holder.pop();
							System.out.println(n);
							return n;
						}
					} catch (InterruptedException e) {
						holder.notifyAll();
					}
					return 0;
				}
			};
		}
		return inputStream;
	}

	public void clear() {
		setText("");
	}
	
}
