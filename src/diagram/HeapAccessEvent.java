package diagram;

import java.util.EventObject;

public class HeapAccessEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	public final int address;
	public final int value;

	public HeapAccessEvent(Object src,int address, int value) {
		super(src);
		this.address = address;
		this.value = value;
	}

}
