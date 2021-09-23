package org.hackyourlife.s550.widget;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class MixedTable extends JTable {
	private static final long serialVersionUID = 1L;
	private Class<?> editingClass;

	public static final Border focusedCellBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
	public static final Border unfocusedCellBorder = createEmptyBorder();

	private static Border createEmptyBorder() {
		Insets i = focusedCellBorder.getBorderInsets(new JLabel());
		return BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right);
	}

	public MixedTable(AbstractTableModel model) {
		super(model);
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		editingClass = null;
		int modelColumn = convertColumnIndexToModel(column);
		TableModel model = getModel();
		final Object value;
		int align;
		if(model instanceof ExtendedTableModel) {
			value = ((ExtendedTableModel) model).getDisplayValueAt(row, modelColumn);
			align = ((ExtendedTableModel) model).getColumnAlignment(modelColumn);
		} else {
			value = model.getValueAt(row, modelColumn);
			align = modelColumn == 0 ? SwingConstants.LEFT : SwingConstants.RIGHT;
		}
		Class<?> rowClass = value != null ? value.getClass() : String.class;
		if(rowClass == String.class && model instanceof ExtendedTableModel) {
			return new TableCellRenderer() {
				private JLabel text = new JLabel();

				{
					text.setOpaque(true);
					text.setHorizontalAlignment(align);
				}

				@Override
				public Component getTableCellRendererComponent(JTable table, Object val,
						boolean isSelected, boolean hasFocus, int rowIdx, int colIdx) {
					int modelCol = convertColumnIndexToModel(colIdx);
					ExtendedTableModel xm = (ExtendedTableModel) model;
					Object v = xm.getDisplayValueAt(rowIdx, modelCol);
					text.setText(String.valueOf(v));
					if(isSelected) {
						text.setForeground(table.getSelectionForeground());
						text.setBackground(table.getSelectionBackground());
					} else {
						text.setForeground(table.getForeground());
						text.setBackground(table.getBackground());
					}
					text.setBorder(hasFocus ? focusedCellBorder : unfocusedCellBorder);
					text.setFont(table.getFont());
					return text;
				}
			};
		}
		return getDefaultRenderer(rowClass);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		editingClass = null;
		int modelColumn = convertColumnIndexToModel(column);
		Object value = getModel().getValueAt(row, modelColumn);
		editingClass = value != null ? value.getClass() : String.class;
		return getDefaultEditor(editingClass);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return editingClass != null ? editingClass : super.getColumnClass(column);
	}
}
