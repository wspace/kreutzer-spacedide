package diagram;

import java.util.Map;

import javax.swing.JTable;

public class MemoryTable extends JTable {

	private static final long serialVersionUID = 1L;

	private MemoryTableModel model;

	public MemoryTable() {
		model = new MemoryTableModel();
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
