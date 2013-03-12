package diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class MergeDialog extends JDialog {

	public static final long serialVersionUID = 1L;
	
	private File inputFile;
	private File outputFile;
	
	
	public MergeDialog(JFrame owner) {
		super(owner, ModalityType.APPLICATION_MODAL);
		init();
	}
	
	private void init() {
		setSize(200, 200);
		setVisible(true);
		JButton b = new JButton("Merge");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
		getContentPane().add(b);
	}
	
	public File getInputFile() {
		return inputFile;
	}
	
	public File getOutputFile() {
		return outputFile;
	}
	
	
}
