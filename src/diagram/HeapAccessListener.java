package diagram;

import java.util.EventListener;

public interface HeapAccessListener extends EventListener {
	
	public void valueStored(HeapAccessEvent e);

	public void valueLoaded(HeapAccessEvent e);
}
