package vm;

import java.util.EventListener;

import parser.ParameterizedWhitespaceOperation;

public interface VMListener extends EventListener{
	
	public void vmStarted();
	
	public void vmPaused();
	
	public void vmResumed();
	
	public void vmStopped();
	
	public void newOperation(ParameterizedWhitespaceOperation pwo);

}
