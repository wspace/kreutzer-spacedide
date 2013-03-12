package diagram;


import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.EventListenerList;

public class Stack {

	private PrintStream out;
	
	private int[] data;
	private int currentSize;

	private EventListenerList listeners;

	public Stack(int maxSize, PrintStream out) {
		this.out = out;
		data = new int[maxSize];
		listeners = new EventListenerList();
		reset();
	}

	public void addStackListener(StackListener l) {
		listeners.add(StackListener.class, l);
	}

	public void removeStackListener(StackListener l) {
		listeners.remove(StackListener.class, l);
	}

	public void push(int value) {
		if (currentSize + 1 >= data.length) {
			out.println("Error: Stack Overflow!");
			throw new WhitespaceRuntimeException("Stack Overflow");
		}
		data[currentSize] = value;
		StackEvent e = new StackEvent(this, currentSize, value);
		for (StackListener l : listeners.getListeners(StackListener.class)) {
			l.valuePushed(e);
		}
		currentSize++;
	}

	public int pop() {
		if (currentSize <= 0) {
			String msg = "Can't pop element: stack is empty";
			out.println("Error: " + msg);
			throw new WhitespaceRuntimeException(msg);
		
		}
			int element = topElement();
		data[currentSize - 1] = 0;
		StackEvent e = new StackEvent(this, currentSize - 1, element);
		for (StackListener l : listeners.getListeners(StackListener.class)) {
			l.valueRemoved(e);
		}
		currentSize--;
		return element;
	}

	public void copyToTop(int n) {
		push(getElement(n));
	}

	public void swapTopElements() {
		int first = getElement(0);
		int second = getElement(1);
		pop();
		pop();
		push(first);
		push(second);
	}

	public void discardElements(int n) {
		int first = pop();
		for (int i = 0; i < n; i++) {
			pop();
		}
		push(first);
	}

	public int getElement(int n) {
		n = currentSize - n - 1;
		if (n < 0 || n >= currentSize) {
			String msg = "Invalid stack address: " + n;
			out.println(msg);
			throw new WhitespaceRuntimeException(msg);
		}
		return data[n];
	}

	public int topElement() {
		return getElement(0);
	}

	public void reset() {
		Arrays.fill(data, 0);
		currentSize = 0;
	}
	
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("[");
		for(int i = 0; i < currentSize; i++) {
			string.append(data[i]);
			string.append(", ");
		}
		string.append("]");
		return string.toString();
	}

	public int getSize() {
		return currentSize;
	}
	
	public Map<Integer, Integer> getValueMap() {
		HashMap<Integer, Integer> valueMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < currentSize; i++) {
			valueMap.put(i, getElement(i));
		}
		return valueMap;
	}

}
