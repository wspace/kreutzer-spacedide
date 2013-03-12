package diagram;

import java.util.EventListener;

public interface StackListener extends EventListener{

	public void valuePushed(StackEvent e);
	
	public void valueRemoved(StackEvent e);
	
}
