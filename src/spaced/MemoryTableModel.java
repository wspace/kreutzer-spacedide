package spaced;

import javax.swing.table.AbstractTableModel;

public class MemoryTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final int ADDRESS_COLUMN = 0;
	private static final int HEAP_COLUMN = 1;
	private static final int STACK_COLUMN = 2;

	public enum MemoryType {
		STACK, HEAP
	}

	private static final String UNDEFINED_VALUE = "X";

	private Object[] colNames;
	private Object[][] data;

	public MemoryTableModel() {
		clear();
	}

	@Override
	public int getColumnCount() {
		return colNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public String getColumnName(int col) {
		return colNames[col].toString();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex >= data.length || columnIndex >= data[rowIndex].length)
			return;
		System.out.println("Set value");
		data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	private synchronized void insertRow(int rowIndex) {
//		System.out.println("Insert row");
		Object[][] updatedData = new Object[getRowCount() + 1][getColumnCount()];
		int rowDelta = 0;
		for (int row = 0; row < data.length; row++) {
			if (row == rowIndex) {
				updatedData[row] = new Object[getColumnCount()];
				rowDelta = 1;
			}
			updatedData[row + rowDelta] = data[row];
		}
		data = updatedData;
		fireTableStructureChanged();
	}

	private synchronized void removeRow(int rowIndex) {
//		System.out.println("Remove row");
		Object[][] updatedData = new Object[getRowCount() - 1][getColumnCount()];
		int rowDelta = 0;
		for (int row = 0; row < updatedData.length; row++) {
			if (row == rowIndex) {
				rowDelta = 1;
			}
			updatedData[row] = data[row + rowDelta];
		}
		data = updatedData;
		fireTableStructureChanged();
	}

	public synchronized void showAddress(MemoryType type, int address, int value) {
//		System.out.println("Show address");
		int insertPosition = 0;
		int valueColumn = HEAP_COLUMN;
		int otherColumn = STACK_COLUMN;
		if (type == MemoryType.STACK) {
			valueColumn = STACK_COLUMN;
			otherColumn = HEAP_COLUMN;
		}
		int rowAddress = 0;
		for (int row = 0; row < getRowCount(); row++) {
			rowAddress = Integer.valueOf(getValueAt(row, ADDRESS_COLUMN)
					.toString());
			if (rowAddress == address) {
				setValueAt(value, row, valueColumn);
				return;
			}
			if (rowAddress > address) {
				insertPosition = row;
				break;
			} else {
				insertPosition = row + 1;
			}
		}
		insertRow(insertPosition);
		setValueAt(address, insertPosition, ADDRESS_COLUMN);
		setValueAt(value, insertPosition, valueColumn);
		setValueAt(UNDEFINED_VALUE, insertPosition, otherColumn);
	}

	public synchronized void hideAddress(MemoryType type, int address) {
//		System.out.println("Hide address");
		int valueColumn = HEAP_COLUMN;
		int otherColumn = STACK_COLUMN;
		if (type == MemoryType.STACK) {
			valueColumn = STACK_COLUMN;
			otherColumn = HEAP_COLUMN;
		}
		int rowAddress = 0;
		for (int row = 0; row < getRowCount(); row++) {
			rowAddress = Integer.valueOf(getValueAt(row, ADDRESS_COLUMN)
					.toString());
			if (rowAddress == address) {
				setValueAt(UNDEFINED_VALUE, row, valueColumn);
				if (getValueAt(row, otherColumn) == UNDEFINED_VALUE) {
					removeRow(row);
				}
				return;
			}
		}
	}

	public synchronized void clear() {
		colNames = new Object[] { "Address", "Heap", "Stack" };
		data = new Object[0][colNames.length];
	}
}
