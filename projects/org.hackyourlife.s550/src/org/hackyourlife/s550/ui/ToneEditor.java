package org.hackyourlife.s550.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractListModel;
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

import org.hackyourlife.s550.FloppyDisk;
import org.hackyourlife.s550.ToneParameter;
import org.hackyourlife.s550.WaveData;
import org.hackyourlife.s550.layout.LabeledPairLayout;
import org.hackyourlife.s550.riff.DataChunk;
import org.hackyourlife.s550.riff.InstrumentChunk;
import org.hackyourlife.s550.riff.RiffWave;
import org.hackyourlife.s550.riff.SampleChunk;
import org.hackyourlife.s550.riff.SampleChunk.SampleLoop;
import org.hackyourlife.s550.riff.WaveFormatChunk;

@SuppressWarnings("serial")
public class ToneEditor extends JPanel {
	private static final String[] OUTPUT_ASSIGN = { "Output 1", "Output 2", "Output 3", "Output 4", "Output 5",
			"Output 6", "Output 7", "Output 8" };
	private static final String[] ORIGSUB_TONE = { "Original Tone", "Sub-Tone" };
	private static final String[] SAMPLING_FREQUENCY = { "30kHz", "15kHz" };
	private static final String[] WAVE_BANK = { "A", "B" };
	private static final String[] LOOP_MODE = { "Forward", "Alternating", "One Shot", "Reverse" };
	private static final String[] OFF_ON = { "Off", "On" };
	private static final String[] LFO_MODE = { "Normal", "One Shot" };
	private static final String[] LFO_POLARITY = { "Sine", "Peak hold" };
	private static final String[] TVF_EG_POLARITY = { "Normal", "Reverse" };
	private static final String[] REC_PRE_TRIGGER = { "0ms", "10ms", "50ms", "100ms" };
	private static final String[] ORIG_KEY_NUMBER;

	private FloppyDisk disk;

	private ToneListModel toneModel;

	private JList<String> tones;

	private JTextField name;
	private JComboBox<String> outputAssign;
	private JComboBox<String> sourceTone;
	private JComboBox<String> origSubTone;
	private JComboBox<String> samplingFrequency;
	private JComboBox<String> origKeyNumber;
	private JComboBox<String> waveBank;
	private JSpinner waveSegmentTop;
	private JSpinner waveSegmentLength;
	private JSpinner startPoint;
	private JSpinner endPoint;
	private JComboBox<String> loopMode;
	private JSpinner tvaLfoDepth;
	private JSpinner lfoRate;
	private JComboBox<String> lfoSync;
	private JSpinner lfoDelay;
	private JComboBox<String> lfoMode;
	private JSpinner oscLfoDepth;
	private JComboBox<String> lfoPolarity;
	private JSpinner lfoOffset;
	private JSpinner transpose;
	private JSpinner finetune;
	private JSpinner tvfCutoff;
	private JSpinner tvfResonance;
	private JSpinner tvfKeyFollow;
	private JSpinner tvfLfoDepth;
	private JSpinner tvfEgDepth;
	private JComboBox<String> tvfEgPolarity;
	private JSpinner tvfLevelCurve;
	private JSpinner tvfKeyRateFollow;
	private JSpinner tvfVelocityRateFollow;
	private JComboBox<String> tvfSwitch;
	private JComboBox<String> benderSwitch;
	private JSpinner tvaEnvSustainPoint;
	private JSpinner tvaEnvEndPoint;
	private JSpinner tvaEnvKeyRate;
	private JSpinner level;
	private JSpinner envVelRate;
	private JSpinner recThreshold;
	private JSpinner recPreTrigger;
	private JComboBox<String> recSamplingRate;
	private JSpinner recStartPoint;
	private JSpinner recEndPoint;
	private JSpinner recLoopPoint;
	private JSpinner zoomT;
	private JSpinner zoomL;
	private JComboBox<String> copySource;
	private JSpinner loopTune;
	private JSpinner tvaLevelCurve;
	private JSpinner loopLength;
	private JComboBox<String> pitchFollow;
	private JSpinner tvaZoom;
	private JSpinner tvfEnvSustainPoint;
	private JSpinner tvfEnvEndPoint;
	private JComboBox<String> afterTouchSwitch;

	static {
		ORIG_KEY_NUMBER = new String[109];
		for(int i = 11; i < 120; i++) {
			ORIG_KEY_NUMBER[i - 11] = MIDINames.getNoteName(i);
		}
	}

	public ToneEditor(FloppyDisk disk) {
		super(new BorderLayout());
		this.disk = disk;

		tones = new JList<>(toneModel = new ToneListModel());
		tones.addListSelectionListener(e -> showSelected());

		name = new JTextField();
		name.setFont(MainWindow.MONOSPACED_FONT);
		name.addKeyListener(new NameFieldKeyHandler(name, 8) {
			@Override
			public void handle(String value) {
				ToneParameter param = getToneParameter();
				if(param != null) {
					param.setName(value);
					updateCurrent();
				}
			}
		});

		outputAssign = new JComboBox<>(OUTPUT_ASSIGN);
		outputAssign.addActionListener(e -> {
			ToneParameter param = getToneParameter();
			if(param != null && outputAssign.getSelectedIndex() != -1) {
				param.setOutputAssign((byte) outputAssign.getSelectedIndex());
			}
		});

		// TODO: sourceTone

		origSubTone = new JComboBox<>(ORIGSUB_TONE);
		origSubTone.addActionListener(e -> {
			ToneParameter param = getToneParameter();
			if(param != null && origSubTone.getSelectedIndex() != -1) {
				param.setOrigSubTone((byte) origSubTone.getSelectedIndex());
			}
		});

		samplingFrequency = new JComboBox<>(SAMPLING_FREQUENCY);
		samplingFrequency.addActionListener(e -> {
			ToneParameter param = getToneParameter();
			if(param != null && samplingFrequency.getSelectedIndex() != -1) {
				param.setSamplingFrequency((byte) samplingFrequency.getSelectedIndex());
			}
		});

		JPanel parameters = new JPanel(new LabeledPairLayout());
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Name:"));
		parameters.add(LabeledPairLayout.COMPONENT, name);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Output assign:"));
		parameters.add(LabeledPairLayout.COMPONENT, outputAssign);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Orig/Sub tone:"));
		parameters.add(LabeledPairLayout.COMPONENT, origSubTone);
		parameters.add(LabeledPairLayout.LABEL, new JLabel("Sampling frequency:"));
		parameters.add(LabeledPairLayout.COMPONENT, samplingFrequency);

		FileDialog exportWave = new FileDialog((Frame) null, "Export wave data...", FileDialog.SAVE);

		JPanel buttons = new JPanel(new FlowLayout());
		JButton export = new JButton("Export...");
		export.addActionListener(e -> {
			exportWave.setVisible(true);
			if(exportWave.getFile() == null) {
				return;
			}
			String filename = exportWave.getDirectory() + exportWave.getFile();
			try {
				exportTone(new File(filename));
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		});
		buttons.add(export);

		JButton copyFrom = new JButton("Copy...");
		copyFrom.addActionListener(e -> {
			String tone = JOptionPane.showInputDialog("Source tonne:");
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
				int toneId = S550.parseToneNumber(val);
				ToneParameter param = disk.getToneParameter(toneId);
				ToneParameter self = getToneParameter();
				if(self == null) {
					return;
				}
				self.copyFrom(param);
				updateCurrent();
			}
		});
		buttons.add(copyFrom);

		JPanel content = new JPanel(new BorderLayout());
		content.add(BorderLayout.NORTH, parameters);
		content.add(BorderLayout.CENTER, new JPanel());
		content.add(BorderLayout.SOUTH, buttons);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(new JScrollPane(tones));
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
		toneModel.update();
		if(tones.getSelectedIndex() == -1) {
			tones.setSelectedIndex(0);
		}
		showSelected();
	}

	public void updateCurrent() {
		toneModel.updateCurrent();
	}

	private ToneParameter getToneParameter() {
		int selected = tones.getSelectedIndex();
		if(selected == -1) {
			return null;
		} else {
			return disk.getToneParameter(selected);
		}
	}

	public void showSelected() {
		ToneParameter param = getToneParameter();
		if(param != null) {
			name.setText(param.getName());
			outputAssign.setSelectedIndex(param.getOutputAssign());
			origSubTone.setSelectedIndex(param.getOrigSubTone());
			samplingFrequency.setSelectedIndex(param.getSamplingFrequency());
		}
	}

	private class ToneListModel extends AbstractListModel<String> {
		@Override
		public String getElementAt(int i) {
			String id = "x" + (i / 8 + 1) + (i % 8 + 1) + ": ";
			return id + disk.getToneParameter(i).getName();
		}

		@Override
		public int getSize() {
			return FloppyDisk.TONE_COUNT;
		}

		public void update() {
			fireContentsChanged(this, 0, getSize());
		}

		public void updateCurrent() {
			int i = tones.getSelectedIndex();
			if(i == -1) {
				update();
			} else {
				fireContentsChanged(this, i, i);
			}
		}
	}

	private WaveData getWaveData(int i) {
		ToneParameter param = getToneParameter();
		if(param == null) {
			return null;
		}
		if(param.getWaveBank() == ToneParameter.WAVEBANK_A) {
			return disk.getWaveDataA(i);
		} else {
			return disk.getWaveDataB(i);
		}
	}

	public void exportTone(File path) throws IOException {
		ToneParameter param = getToneParameter();
		if(param == null) {
			return;
		}

		int sampleRate = param.getSamplingFrequency() == ToneParameter.FREQ_30k ? 30_000 : 15_000;
		int segment = param.getWaveSegmentTop();
		int segmentLength = param.getWaveSegmentLength();
		int start = param.getStartPoint();
		int end = param.getEndPoint();

		int sampleCount = end - start + 1;

		short[] samples = new short[sampleCount];
		if(segmentLength == 1) {
			WaveData data = getWaveData(segment);
			short[] s16 = data.getSamples();
			for(int i = 0; i < sampleCount; i++) {
				samples[i] = s16[start + i];
			}
		} else {
			// get all wave segments
			short[] tmp = new short[segmentLength * WaveData.SAMPLES_PER_SEGMENT];
			for(int i = 0; i < segmentLength; i++) {
				WaveData data = getWaveData(segment + i);
				short[] s16 = data.getSamples();
				int off = i * WaveData.SAMPLES_PER_SEGMENT;
				for(int j = 0; j < s16.length; j++) {
					tmp[off + j] = s16[j];
				}
			}

			// get the sample
			for(int i = 0; i < sampleCount; i++) {
				samples[i] = tmp[start + i];
			}
		}

		// write WAV file with 30kHz sample rate
		RiffWave out = new RiffWave();
		out.set(new WaveFormatChunk());
		out.set(new DataChunk());
		out.setSampleRate(sampleRate);
		out.setSampleFormat(WaveFormatChunk.WAVE_FORMAT_PCM);
		out.setChannels(1);
		out.setBitsPerSample(16);
		out.set16bitSamples(samples);

		SampleChunk smpl = new SampleChunk();
		smpl.setMidiUnityNote(param.getOrigKeyNumber());
		smpl.setMidiPitchFraction(0);
		smpl.setSamplePeriod(1_000_000_000 / sampleRate);
		int loopStart = param.getLoopPoint() - start;
		int loopEnd = end - start;
		switch(param.getLoopMode()) {
		case ToneParameter.LOOP_FORWARD:
			smpl.addSampleLoop(new SampleLoop(0, SampleLoop.LOOP_FORWARD, loopStart, loopEnd - 1, 0, 0));
			break;
		case ToneParameter.LOOP_ALTERNATING:
			smpl.addSampleLoop(
					new SampleLoop(0, SampleLoop.ALTERNATING_LOOP, loopStart, loopEnd - 1, 0, 0));
			break;
		}
		out.set(smpl);

		if(param.getFineTune() != 0) {
			byte fineTune = param.getFineTune();
			InstrumentChunk inst = new InstrumentChunk();
			inst.setUnshiftedNote(param.getOrigKeyNumber());
			inst.setFineTune(fineTune);
			inst.setGain((byte) 0);
			inst.setLowNote((byte) 0);
			inst.setHighNote((byte) 127);
			inst.setLowVelocity((byte) 1);
			inst.setHighVelocity((byte) 127);
			out.set(inst);
		}

		try(BufferedOutputStream wav = new BufferedOutputStream(new FileOutputStream(path))) {
			out.write(wav);
		}
	}
}
