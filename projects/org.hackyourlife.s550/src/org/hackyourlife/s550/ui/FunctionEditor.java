package org.hackyourlife.s550.ui;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.hackyourlife.s550.FloppyDisk;
import org.hackyourlife.s550.FunctionParameter;
import org.hackyourlife.s550.PatchParameter;
import org.hackyourlife.s550.layout.LabeledPairLayout;
import org.hackyourlife.s550.widget.ExtendedTableModel;
import org.hackyourlife.s550.widget.MixedTable;

@SuppressWarnings("serial")
public class FunctionEditor extends JPanel {
	private static final String[] VOICE_MODE = { "Auto mode: last note priority", "Auto mode: first note priority",
			"Fix mode 1", "Fix mode 1", "Fix mode 3", "Fix mode 4", "Fix mode 5", "Fix mode 6",
			"Fix mode 7", "Fix mode 8", "Fix mode 9", "Fix mode 10", "Fix mode 11", "Fix mode 12",
			"Fix mode 13", "Fix mode 14", "Fix mode 15", "Fix mode 16", "Fix mode 17", "Fix mode 18",
			"Fix mode 19", "Fix mode 20", "Fix mode 21", "Fix mode 22"
	};

	private static final String[] EXTERNAL_CONTROLLER = { "Off", "Mouse", "RC-100" };

	private FloppyDisk disk;

	private JTextField name;
	private JTextField note1;
	private JTextField note2;
	private JTextField note3;
	private JTextField note4;

	private JSpinner masterTune;
	private JComboBox<String> voiceMode;
	private JComboBox<String> externalController;
	private MixedTable multiPatch;

	private MultiPatchModel model;

	public FunctionEditor(FloppyDisk disk) {
		super(new BorderLayout());
		this.disk = disk;

		name = new JTextField();
		note1 = new JTextField();
		note2 = new JTextField();
		note3 = new JTextField();
		note4 = new JTextField();
		name.setFont(MainWindow.MONOSPACED_FONT);
		note1.setFont(MainWindow.MONOSPACED_FONT);
		note2.setFont(MainWindow.MONOSPACED_FONT);
		note3.setFont(MainWindow.MONOSPACED_FONT);
		note4.setFont(MainWindow.MONOSPACED_FONT);

		name.addKeyListener(new KeyHandler(name, 0));
		note1.addKeyListener(new KeyHandler(note1, 1));
		note2.addKeyListener(new KeyHandler(note2, 2));
		note3.addKeyListener(new KeyHandler(note3, 3));
		note4.addKeyListener(new KeyHandler(note4, 4));

		masterTune = new JSpinner(new SpinnerNumberModel(0, -64, 63, 1));
		masterTune.addChangeListener(e -> {
			byte value = ((SpinnerNumberModel) masterTune.getModel()).getNumber().byteValue();
			disk.getFunctionParameter().setMasterTune(value);
		});

		voiceMode = new JComboBox<>(VOICE_MODE);
		voiceMode.addActionListener(e -> {
			if(voiceMode.getSelectedIndex() != -1) {
				disk.getFunctionParameter().setVoiceMode((byte) voiceMode.getSelectedIndex());
			}
		});

		externalController = new JComboBox<>(EXTERNAL_CONTROLLER);
		externalController.addActionListener(e -> {
			if(externalController.getSelectedIndex() != -1) {
				disk.getFunctionParameter()
						.setExternalController((byte) externalController.getSelectedIndex());
			}
		});

		multiPatch = new MixedTable(model = new MultiPatchModel());

		JPanel parameters = new JPanel(new LabeledPairLayout());
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Disk name:"));
		parameters.add(LabeledPairLayout.COMPONENT, name);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Note 1:"));
		parameters.add(LabeledPairLayout.COMPONENT, note1);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Note 2:"));
		parameters.add(LabeledPairLayout.COMPONENT, note2);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Note 3:"));
		parameters.add(LabeledPairLayout.COMPONENT, note3);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Note 4:"));
		parameters.add(LabeledPairLayout.COMPONENT, note4);

		parameters.add(LabeledPairLayout.LABEL, new JLabel("Master tune:"));
		parameters.add(LabeledPairLayout.COMPONENT, masterTune);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Voice mode:"));
		parameters.add(LabeledPairLayout.COMPONENT, voiceMode);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("External controller:"));
		parameters.add(LabeledPairLayout.COMPONENT, externalController);

		add(BorderLayout.NORTH, parameters);
		add(BorderLayout.CENTER, new JScrollPane(multiPatch));

		update();
	}

	private class KeyHandler extends NameFieldKeyHandler {
		private final int i;

		public KeyHandler(JTextField textfield, int i) {
			super(textfield, 12);
			this.i = i;
		}

		@Override
		public void handle(String value) {
			FunctionParameter param = disk.getFunctionParameter();
			String[] label = param.getDiskLabel();
			label[i] = value;
			param.setDiskLabel(label);
		}
	}

	public void setDisk(FloppyDisk disk) {
		this.disk = disk;
		update();
	}

	public void update() {
		FunctionParameter param = disk.getFunctionParameter();
		String[] label = param.getDiskLabel();
		name.setText(label[0]);
		note1.setText(label[1]);
		note2.setText(label[2]);
		note3.setText(label[3]);
		note4.setText(label[4]);
		((SpinnerNumberModel) masterTune.getModel()).setValue((int) param.getMasterTune());
		voiceMode.setSelectedIndex(param.getVoiceMode());
		externalController.setSelectedIndex(param.getExternalController());
		model.update();
	}

	private class MultiPatchModel extends ExtendedTableModel {
		@Override
		public int getColumnAlignment(int col) {
			switch(col) {
			case 0:
				return SwingConstants.LEFT;
			case 1:
				return SwingConstants.RIGHT;
			case 3:
				return SwingConstants.LEFT;
			case 4:
				return SwingConstants.RIGHT;
			default:
				return SwingConstants.LEFT;
			}
		}

		@Override
		public String getColumnName(int col) {
			switch(col) {
			case 0:
				return "Part";
			case 1:
				return "MIDI RX-Channel";
			case 2:
				return "Patch Number";
			case 3:
				return "Level";
			default:
				return null;
			}
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			return 8;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return col > 0;
		}

		@Override
		public Object getDisplayValueAt(int row, int col) {
			switch(col) {
			case 0:
				return "Part " + (char) ('A' + row);
			case 1:
				return disk.getFunctionParameter().getMultiMidiRxCh(row) + 1;
			case 2:
				byte toneId = disk.getFunctionParameter().getMultiPatchNumber(row);
				PatchParameter tone = disk.getPatchParameter(toneId);
				return S550.getPatchString(toneId, tone);
			case 3:
				return disk.getFunctionParameter().getMultiLevel(row);
			default:
				return null;
			}
		}

		@Override
		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return row;
			case 1:
				return disk.getFunctionParameter().getMultiMidiRxCh(row) + 1;
			case 2:
				byte toneId = disk.getFunctionParameter().getMultiPatchNumber(row);
				return S550.getPatchNumber(toneId);
			case 3:
				return disk.getFunctionParameter().getMultiLevel(row);
			default:
				return null;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			switch(col) {
			case 1: {
				int val = (int) value;
				if(val < 1 || val > 16) {
					return;
				}
				disk.getFunctionParameter().setMultiMidiRxCh(row, (byte) (val - 1));
				break;
			}
			case 2:
				try {
					byte tone = S550.parsePatchNumber((int) value);
					disk.getFunctionParameter().setMultiPatchNumber(row, tone);
				} catch(IllegalArgumentException e) {
					// nothing
				}
				break;
			case 3: {
				byte val = (byte) value;
				if(val < 0) {
					return;
				}
				disk.getFunctionParameter().setMultiLevel(row, val);
				break;
			}
			}
		}

		public void update() {
			fireTableDataChanged();
		}
	}
}
