package org.hackyourlife.s550.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.hackyourlife.s550.FloppyDisk;
import org.hackyourlife.s550.PatchParameter;
import org.hackyourlife.s550.ToneParameter;
import org.hackyourlife.s550.layout.LabeledPairLayout;
import org.hackyourlife.s550.widget.ExtendedTableModel;
import org.hackyourlife.s550.widget.MixedTable;

@SuppressWarnings("serial")
public class PatchEditor extends JPanel {
	private static final String[] KEY_MODE = { "Normal", "V-Switch", "X-Fade", "V-Mix", "Unison" };
	private static final String[] KEY_ASSIGN = { "Rotary", "Fix" };
	private static final String[] AFTER_TOUCH_ASSIGN = { "Modulation", "Volume", "Bend +", "Bend -", "Filter" };
	private static final String[] OUTPUT_ASSIGN = { "Output 1", "Output 2", "Output 3", "Output 4", "Output 5",
			"Output 6", "Output 7", "Output 8", "Tone" };

	private FloppyDisk disk;

	private JList<String> patches;
	private PatchListModel model;
	private ToneToKeyModel toneToKeyModel;

	private JTextField name;
	private JSpinner bendRange;
	private JSpinner afterTouchSense;
	private JComboBox<String> keyMode;
	private JSpinner velocitySwitchThreshold;
	private JComboBox<String> copySource;
	private JSpinner octaveShift;
	private JSpinner outputLevel;
	private JSpinner detune;
	private JSpinner velocityMixRatio;
	private JComboBox<String> afterTouchAssign;
	private JComboBox<String> keyAssign;
	private JComboBox<String> outputAssign;
	private MixedTable toneToKey;

	public PatchEditor(FloppyDisk disk) {
		super(new BorderLayout());
		this.disk = disk;

		patches = new JList<>(model = new PatchListModel());
		patches.addListSelectionListener(e -> showSelected());

		JPanel parameters = new JPanel(new LabeledPairLayout());

		name = new JTextField();
		name.setFont(MainWindow.MONOSPACED_FONT);
		name.addKeyListener(new NameFieldKeyHandler(name, 12) {
			@Override
			public void handle(String value) {
				PatchParameter param = getPatchParameter();
				if(param != null) {
					param.setName(value);
					updateCurrent();
				}
			}
		});

		bendRange = new JSpinner(new SpinnerNumberModel(0, 0, 12, 1));
		bendRange.addChangeListener(e -> {
			byte value = ((SpinnerNumberModel) bendRange.getModel()).getNumber().byteValue();
			PatchParameter param = getPatchParameter();
			if(param != null) {
				param.setBendRange(value);
			}
		});

		afterTouchSense = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
		afterTouchSense.addChangeListener(e -> {
			byte value = ((SpinnerNumberModel) afterTouchSense.getModel()).getNumber().byteValue();
			PatchParameter param = getPatchParameter();
			if(param != null) {
				param.setAfterTouchSense(value);
			}
		});

		keyMode = new JComboBox<>(KEY_MODE);
		keyMode.addActionListener(e -> {
			PatchParameter param = getPatchParameter();
			if(param != null && keyMode.getSelectedIndex() != -1) {
				param.setKeyMode((byte) keyMode.getSelectedIndex());
			}
		});

		velocitySwitchThreshold = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
		velocitySwitchThreshold.addChangeListener(e -> {
			byte value = ((SpinnerNumberModel) velocitySwitchThreshold.getModel()).getNumber().byteValue();
			PatchParameter param = getPatchParameter();
			if(param != null) {
				param.setVelocitySwitchThreshold(value);
			}
		});

		copySource = new JComboBox<>(new CopySourceModel());
		copySource.addActionListener(e -> {
			PatchParameter param = getPatchParameter();
			if(param != null && copySource.getSelectedIndex() != -1) {
				param.setCopySource((byte) copySource.getSelectedIndex());
			}
		});

		octaveShift = new JSpinner(new SpinnerNumberModel(0, -2, 2, 1));
		octaveShift.addChangeListener(e -> {
			byte value = ((SpinnerNumberModel) octaveShift.getModel()).getNumber().byteValue();
			PatchParameter param = getPatchParameter();
			if(param != null) {
				param.setOctaveShift(value);
			}
		});

		outputLevel = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
		outputLevel.addChangeListener(e -> {
			byte value = ((SpinnerNumberModel) outputLevel.getModel()).getNumber().byteValue();
			PatchParameter param = getPatchParameter();
			if(param != null) {
				param.setOutputLevel(value);
			}
		});

		detune = new JSpinner(new SpinnerNumberModel(0, -64, 63, 1));
		detune.addChangeListener(e -> {
			byte value = ((SpinnerNumberModel) detune.getModel()).getNumber().byteValue();
			PatchParameter param = getPatchParameter();
			if(param != null) {
				param.setDetune(value);
			}
		});

		velocityMixRatio = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
		velocityMixRatio.addChangeListener(e -> {
			byte value = ((SpinnerNumberModel) velocityMixRatio.getModel()).getNumber().byteValue();
			PatchParameter param = getPatchParameter();
			if(param != null) {
				param.setVelocityMixRatio(value);
			}
		});

		afterTouchAssign = new JComboBox<>(AFTER_TOUCH_ASSIGN);
		afterTouchAssign.addActionListener(e -> {
			PatchParameter param = getPatchParameter();
			if(param != null && afterTouchAssign.getSelectedIndex() != -1) {
				param.setAfterTouchAssign((byte) afterTouchAssign.getSelectedIndex());
			}
		});

		keyAssign = new JComboBox<>(KEY_ASSIGN);
		keyAssign.addActionListener(e -> {
			PatchParameter param = getPatchParameter();
			if(param != null && keyAssign.getSelectedIndex() != -1) {
				param.setKeyAssign((byte) keyAssign.getSelectedIndex());
			}
		});

		outputAssign = new JComboBox<>(OUTPUT_ASSIGN);
		outputAssign.addActionListener(e -> {
			PatchParameter param = getPatchParameter();
			if(param != null && outputAssign.getSelectedIndex() != -1) {
				param.setOutputAssign((byte) outputAssign.getSelectedIndex());
			}
		});

		parameters.add(LabeledPairLayout.LABEL, new JLabel("Name:"));
		parameters.add(LabeledPairLayout.COMPONENT, name);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Key mode:"));
		parameters.add(LabeledPairLayout.COMPONENT, keyMode);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Key assign:"));
		parameters.add(LabeledPairLayout.COMPONENT, keyAssign);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Unison detune:"));
		parameters.add(LabeledPairLayout.COMPONENT, detune);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Velocity switch threshold:"));
		parameters.add(LabeledPairLayout.COMPONENT, velocitySwitchThreshold);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Velocity mix ratio:"));
		parameters.add(LabeledPairLayout.COMPONENT, velocityMixRatio);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Pitch bend range:"));
		parameters.add(LabeledPairLayout.COMPONENT, bendRange);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("After touch sense:"));
		parameters.add(LabeledPairLayout.COMPONENT, afterTouchSense);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("After touch assign:"));
		parameters.add(LabeledPairLayout.COMPONENT, afterTouchAssign);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Octave shift:"));
		parameters.add(LabeledPairLayout.COMPONENT, octaveShift);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Output assign:"));
		parameters.add(LabeledPairLayout.COMPONENT, outputAssign);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Level:"));
		parameters.add(LabeledPairLayout.COMPONENT, outputLevel);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Copy source:"));
		parameters.add(LabeledPairLayout.COMPONENT, copySource);

		toneToKey = new MixedTable(toneToKeyModel = new ToneToKeyModel());
		toneToKey.setColumnSelectionAllowed(true);

		JPanel buttons = new JPanel(new FlowLayout());
		JButton copyFrom = new JButton("Copy...");
		copyFrom.addActionListener(e -> {
			String patch = JOptionPane.showInputDialog("Source patch:");
			if(patch != null) {
				patch = patch.trim();
				if(patch.length() < 2) {
					return;
				}
				if(patch.charAt(0) == 'i' || patch.charAt(0) == 'I' ||
						(patch.length() == 3 && patch.charAt(0) == '1')) {
					patch = patch.substring(1);
				}
				if(patch.length() != 2) {
					return;
				}
				int val = Integer.parseInt(patch);
				int patchId = S550.parsePatchNumber(val);
				PatchParameter param = disk.getPatchParameter(patchId);
				PatchParameter self = getPatchParameter();
				if(self == null) {
					return;
				}
				self.copyFrom(param);
				update();
			}
		});
		buttons.add(copyFrom);

		JButton setKeys = new JButton("Set keys...");
		setKeys.addActionListener(e -> {
			String tone = JOptionPane.showInputDialog("Tone:");
			if(tone != null) {
				tone = tone.trim();
				if(tone.length() < 2) {
					return;
				}
				if(tone.charAt(0) == 'x' || tone.charAt(0) == 'X') {
					tone = tone.substring(1);
				}
				if(tone.length() != 2) {
					return;
				}
				int val = Integer.parseInt(tone);
				byte toneId = S550.parseToneNumber(val);
				PatchParameter self = getPatchParameter();
				if(self == null) {
					return;
				}
				int[] rows = toneToKey.getSelectedRows();
				int[] cols = toneToKey.getSelectedColumns();
				int firstRow = Integer.MAX_VALUE;
				int lastRow = -1;
				if(rows.length > 0 && cols.length > 0) {
					for(int col : cols) {
						if(col != 2 && col != 3) {
							continue;
						}
						for(int row : rows) {
							self.setToneToKey(col - 2, row, toneId);
							if(row < firstRow) {
								firstRow = row;
							}
							if(row > lastRow) {
								lastRow = row;
							}
						}
					}
				}
				if(lastRow != -1) {
					toneToKeyModel.update(firstRow, lastRow);
				}
			}
		});
		buttons.add(setKeys);

		JPanel content = new JPanel(new BorderLayout());
		content.add(BorderLayout.NORTH, parameters);
		content.add(BorderLayout.CENTER, new JScrollPane(toneToKey));
		content.add(BorderLayout.SOUTH, buttons);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(new JScrollPane(patches));
		split.setRightComponent(content);
		split.setResizeWeight(0.4);
		add(BorderLayout.CENTER, split);

		update();
	}

	public void setDisk(FloppyDisk disk) {
		this.disk = disk;
		update();
	}

	public void update() {
		model.update();
		if(patches.getSelectedIndex() == -1) {
			patches.setSelectedIndex(0);
		}
		showSelected();
	}

	public void updateCurrent() {
		model.updateCurrent();
	}

	private PatchParameter getPatchParameter() {
		int selected = patches.getSelectedIndex();
		if(selected == -1) {
			return null;
		} else {
			return disk.getPatchParameter(selected);
		}
	}

	public void showSelected() {
		PatchParameter param = getPatchParameter();
		if(param != null) {
			name.setText(param.getName());
			((SpinnerNumberModel) bendRange.getModel()).setValue((int) param.getBendRange());
			((SpinnerNumberModel) afterTouchSense.getModel()).setValue((int) param.getAfterTouchSense());
			keyMode.setSelectedIndex(param.getKeyMode());
			((SpinnerNumberModel) velocitySwitchThreshold.getModel())
					.setValue((int) param.getVelocitySwitchThreshold());
			copySource.setSelectedIndex(param.getCopySource());
			((SpinnerNumberModel) octaveShift.getModel()).setValue((int) param.getOctaveShift());
			((SpinnerNumberModel) outputLevel.getModel()).setValue((int) param.getOutputLevel());
			((SpinnerNumberModel) detune.getModel()).setValue((int) param.getDetune());
			((SpinnerNumberModel) velocityMixRatio.getModel()).setValue((int) param.getVelocityMixRatio());
			afterTouchAssign.setSelectedIndex(param.getAfterTouchAssign());
			keyAssign.setSelectedIndex(param.getKeyAssign());
			outputAssign.setSelectedIndex(param.getOutputAssign());
			toneToKeyModel.update();
		}
	}

	private class PatchListModel extends AbstractListModel<String> {
		@Override
		public String getElementAt(int i) {
			String id = "I" + (i / 8 + 1) + (i % 8 + 1) + ": ";
			return id + disk.getPatchParameter(i).getName();
		}

		@Override
		public int getSize() {
			return FloppyDisk.PATCH_COUNT;
		}

		public void update() {
			fireContentsChanged(this, 0, getSize());
		}

		public void updateCurrent() {
			int i = patches.getSelectedIndex();
			if(i == -1) {
				update();
			} else {
				fireContentsChanged(this, i, i);
			}
		}
	}

	private class CopySourceModel extends AbstractListModel<String> implements ComboBoxModel<String> {
		private Object item = getElementAt(0);

		@Override
		public String getElementAt(int i) {
			int selected = patches.getSelectedIndex();
			if(selected == -1) {
				return "I1" + (i + 1);
			} else if(selected < 8) {
				return "I1" + (i + 1);
			} else {
				return "I2" + (i + 1);
			}
		}

		@Override
		public int getSize() {
			return 8;
		}

		@Override
		public Object getSelectedItem() {
			return item;
		}

		@Override
		public void setSelectedItem(Object anItem) {
			item = anItem;
			fireContentsChanged(this, -1, -1);
		}
	}

	private class ToneToKeyModel extends ExtendedTableModel {
		@Override
		public int getColumnAlignment(int col) {
			return SwingConstants.LEFT;
		}

		@Override
		public String getColumnName(int col) {
			switch(col) {
			case 0:
				return "Key";
			case 1:
				return "Key [MIDI]";
			case 2:
				return "1st Tone";
			case 3:
				return "2nd Tone";
			default:
				return null;
			}
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return col > 1;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			return 109;
		}

		@Override
		public Object getDisplayValueAt(int row, int col) {
			if(col == 0) {
				return MIDINames.getNoteName(row + 12);
			} else if(col == 1) {
				return row + 12;
			} else if(col == 2 || col == 3) {
				PatchParameter param = getPatchParameter();
				if(param == null) {
					return "--";
				} else {
					byte toneId = param.getToneToKey(col - 2, row);
					ToneParameter tone = disk.getToneParameter(toneId);
					return S550.getToneString(toneId, tone);
				}
			} else {
				return null;
			}
		}

		@Override
		public Object getValueAt(int row, int col) {
			if(col == 0) {
				return MIDINames.getNoteName(row + 12);
			} else if(col == 1) {
				return row + 12;
			} else if(col == 2 || col == 3) {
				PatchParameter param = getPatchParameter();
				if(param == null) {
					return 0;
				} else {
					byte tone = param.getToneToKey(col - 2, row);
					return S550.getToneNumber(tone);
				}
			} else {
				return null;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			if(col == 2 || col == 3) {
				PatchParameter param = getPatchParameter();
				if(param == null) {
					return;
				}
				try {
					param.setToneToKey(col - 2, row, S550.parseToneNumber((int) value));
				} catch(IllegalArgumentException e) {
					return;
				}
			}
		}

		public void update() {
			fireTableDataChanged();
		}

		public void update(int firstRow, int lastRow) {
			fireTableRowsUpdated(firstRow, lastRow);
		}
	}
}
