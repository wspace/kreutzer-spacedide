package vm;


import java.util.HashMap;
import java.util.Map;

import javax.swing.event.EventListenerList;

public class Heap {
	
	private Map<Integer, Integer> data;
	
	private EventListenerList listeners;
	
	public Heap(int size) {
		data = new HashMap<Integer, Integer>();
		listeners = new EventListenerList();
		reset();
	}
	
	public void addHeapAccessListener(HeapAccessListener l) {
		listeners.add(HeapAccessListener.class, l);
	}
	
	public void removeHeapAccessListener(HeapAccessListener l) {
		listeners.remove(HeapAccessListener.class, l);
	}
	
	public int load(int address) {
		if (!data.containsKey(address)) {
			throw new WhitespaceRuntimeException("No value assigned");
		}
		int value = data.get(address);
		HeapAccessEvent e = new HeapAccessEvent(this, address, value);
		for (HeapAccessListener l : listeners.getListeners(HeapAccessListener.class)) {
			l.valueLoaded(e);
		}
		return value;
	}
	
	public void store(int address, int value) {
		HeapAccessEvent e = new HeapAccessEvent(this, address, value);
		for (HeapAccessListener l : listeners.getListeners(HeapAccessListener.class)) {
			l.valueStored(e);
		}
		data.put(address, value);
	}
	
	public void reset() {
		data.clear();
	}
	
	public Map<Integer, Integer> getValueMap() {
		return data;
	}

}
