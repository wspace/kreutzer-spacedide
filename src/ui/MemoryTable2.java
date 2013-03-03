package ui;

import java.util.Map;

import javax.swing.JTable;

public class MemoryTable2 extends JTable {

	private static final long serialVersionUID = 1L;

	private MemoryTableModel2 model;

	public MemoryTable2() {
		model = new MemoryTableModel2();
		setModel(model);
	}

	public void update(Map<Integer, Integer> stackMap,
			Map<Integer, Integer> heapMap) {
		model.update(stackMap, heapMap);
	}

	public void clear() {
		model.clear();
	}
}
