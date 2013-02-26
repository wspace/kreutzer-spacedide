package spaced;


import java.util.Map;

import javax.swing.JTable;

import spaced.MemoryTableModel.MemoryType;
import vm.HeapAccessEvent;
import vm.HeapAccessListener;
import vm.StackEvent;
import vm.StackListener;

public class MemoryTable extends JTable implements HeapAccessListener, StackListener {

	private static final long serialVersionUID = 1L;
	private MemoryTableModel model;

	public MemoryTable() {
		model = new MemoryTableModel();
		setModel(model);
	}
	
	public void update() {
		
	}

	@Override
	public void valuePushed(StackEvent e) {
		model.showAddress(MemoryType.STACK, e.address, e.value);
	}

	@Override
	public void valueRemoved(StackEvent e) {
		model.hideAddress(MemoryType.STACK, e.address);
	}

	@Override
	public void valueStored(HeapAccessEvent e) {		
		model.showAddress(MemoryType.HEAP, e.address, e.value);
	}

	@Override
	public void valueLoaded(HeapAccessEvent e) {
		model.showAddress(MemoryType.HEAP, e.address, e.value);
	}
	
	public void clear() {
		model.clear();
	}



}
