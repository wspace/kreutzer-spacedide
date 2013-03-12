package diagram;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;

public class MemoryTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public static final int ADDRESS_COLUMN = 0;
	public static final int STACK_COLUMN = 1;
	public static final int HEAP_COLUMN = 2;

	private int rows;

	private Object[] colNames;
	private Object[][] data;

	public MemoryTableModel() {
		clear();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return rows;
	}

	@Override
	public String getColumnName(int col) {
		return colNames[col].toString();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	public void update(Map<Integer, Integer> stackMap,
			Map<Integer, Integer> heapMap) {
		Set<Integer> addressSet = new TreeSet<Integer>();
		addressSet.addAll(stackMap.keySet());
		addressSet.addAll(heapMap.keySet());
		this.rows = addressSet.size();
		data = new String[rows][colNames.length];
//		System.out.println(addressSet);
		Iterator<Integer> iterator = addressSet.iterator();
		int address = 0;
		for (int i = 0; i < rows; i++) {
			address = iterator.next();
			data[i][ADDRESS_COLUMN] = Integer.toString(address);
			if (stackMap.containsKey(address)) {
				data[i][STACK_COLUMN] = Integer.toString(stackMap.get(address));
			} else {
				data[i][STACK_COLUMN] = "X";
			}
			if (heapMap.containsKey(address)) {
				data[i][HEAP_COLUMN] = Integer.toString(heapMap.get(address));
			} else {
				data[i][HEAP_COLUMN] = "X";
			}
		}
		fireTableDataChanged();
	}

	public void clear() {
		colNames = new String[] { "Address", "Stack", "Heap" };
		data = new String[0][colNames.length];
	}
}
