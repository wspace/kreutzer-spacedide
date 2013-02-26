package spaced;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SpacedPrompt extends JTextField {

	private static final long serialVersionUID = 1L;

	private InputStream inputStream;

	private PrintStream out;

	public SpacedPrompt(PrintStream out) {
		this.out = out;
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("fjklsjl");
				setText(getText() + "\0"); // Not the cleanest way, but it
											// should work
			}
		});
	}

	public InputStream getInputStream() {
		if (inputStream == null) {
			inputStream = new InputStream() {

				@Override
				public int read() throws IOException {
					setEditable(true);
					while (getText().isEmpty()) {
						// System.out.println("Waiting for input...");
						// Wait
						// if (inputSubmitted == true)
						// System.out.println("inputSubmitted: "
						// + inputSubmitted);
					}
					String text = getText();
					out.print(text);
					setText("");
					setEditable(false);
					return text.charAt(0);
				}

			};
		}
		return inputStream;
	}

}
