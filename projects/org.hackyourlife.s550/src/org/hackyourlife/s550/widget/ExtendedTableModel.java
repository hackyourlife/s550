package org.hackyourlife.s550.widget;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public abstract class ExtendedTableModel extends AbstractTableModel {
	public int getColumnAlignment(@SuppressWarnings("unused") int col) {
		return SwingConstants.RIGHT;
	}

	public Object getDisplayValueAt(int row, int col) {
		return getValueAt(row, col);
	}
}
