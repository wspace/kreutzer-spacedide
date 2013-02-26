package spaced;

import java.io.File;

import javax.swing.text.StyledDocument;

public interface IAppController {
	
	public StyledDocument newDocument();

	public StyledDocument openDocument(File file);
	
	public void saveDocument(File file);
	
	public void execute();
	
	public void debug();
	
	public void runStep();
	
	public void resume();
	
	public void stop();
	
	public boolean isDocumentSaved();
	
	public File getDefaultDir();
	
}
