package org.hackyourlife.s550.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.AdjustmentListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.hackyourlife.s550.FloppyDisk;
import org.hackyourlife.s550.WaveData;
import org.hackyourlife.s550.riff.DataChunk;
import org.hackyourlife.s550.riff.RiffWave;
import org.hackyourlife.s550.riff.WaveFormatChunk;
import org.hackyourlife.s550.widget.WaveformEditor;

@SuppressWarnings("serial")
public class WavebankEditor extends JPanel {
	private FloppyDisk disk;

	private SamplePlayer player;

	private WaveformEditor editorA;
	private WaveformEditor editorB;

	public WavebankEditor(FloppyDisk disk) {
		super(new BorderLayout());
		this.disk = disk;

		editorA = new WaveformEditor();
		editorB = new WaveformEditor();
		editorA.setSampleRate(30_000);
		editorB.setSampleRate(30_000);
		editorA.setTimeDivision(0.5);
		editorB.setTimeDivision(0.5);
		editorA.setVoltageDivision(0.1);
		editorB.setVoltageDivision(0.1);
		editorA.setDefaultDivision(0.1, 0.5);
		editorB.setDefaultDivision(0.1, 0.5);

		JScrollBar intensityScroller = new JScrollBar(JScrollBar.VERTICAL, 0, 10, 0, 400);
		AdjustmentListener l = e -> {
			int raw = intensityScroller.getValue();
			float value = 1.0f - raw / 390.0f;
			float intensity = 1.0f + value * 40.0f;
			editorA.setBeamIntensity(intensity);
			editorB.setBeamIntensity(intensity);
		};
		intensityScroller.addAdjustmentListener(l);
		l.adjustmentValueChanged(null);

		JPanel viewA = new JPanel(new BorderLayout());
		viewA.setBorder(BorderFactory.createTitledBorder("Wavebank A"));
		viewA.add(BorderLayout.CENTER, editorA);

		JPanel viewB = new JPanel(new BorderLayout());
		viewB.setBorder(BorderFactory.createTitledBorder("Wavebank B"));
		viewB.add(BorderLayout.CENTER, editorB);

		JPanel wavebanks = new JPanel(new GridLayout(2, 1));
		wavebanks.add(viewA);
		wavebanks.add(viewB);

		try {
			player = new SamplePlayer(30_000);
		} catch(LineUnavailableException e) {
			e.printStackTrace();
			player = null;
		}

		JButton playA = new JButton("Play Bank A");
		playA.addActionListener(e -> {
			if(player != null) {
				player.stop();
				player.setWaveformEditor(editorA);
				player.play(this.disk, false);
				player.start();
			}
		});

		JButton playB = new JButton("Play Bank B");
		playB.addActionListener(e -> {
			if(player != null) {
				player.stop();
				player.setWaveformEditor(editorB);
				player.play(this.disk, true);
				player.start();
			}
		});

		JButton stop = new JButton("Stop");
		stop.addActionListener(e -> {
			if(player != null) {
				player.stop();
				editorA.setCurrentTime(0);
				editorB.setCurrentTime(0);
			}
		});

		FileDialog exportWave = new FileDialog((Frame) null, "Export wave data...", FileDialog.SAVE);

		JButton exportA = new JButton("Export Bank A...");
		exportA.addActionListener(e -> {
			exportWave.setVisible(true);
			if(exportWave.getFile() == null) {
				return;
			}
			String filename = exportWave.getDirectory() + exportWave.getFile();
			try {
				exportBank(new File(filename), false);
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		});

		JButton exportB = new JButton("Export Bank B...");
		exportB.addActionListener(e -> {
			exportWave.setVisible(true);
			if(exportWave.getFile() == null) {
				return;
			}
			String filename = exportWave.getDirectory() + exportWave.getFile();
			try {
				exportBank(new File(filename), true);
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		});

		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(playA);
		buttons.add(playB);
		buttons.add(stop);
		buttons.add(exportA);
		buttons.add(exportB);

		add(BorderLayout.CENTER, wavebanks);
		add(BorderLayout.SOUTH, buttons);
		add(BorderLayout.EAST, intensityScroller);
	}

	public void setDisk(FloppyDisk disk) {
		this.disk = disk;
		update();
	}

	public void update() {
		player.stop();
		editorA.setSignal(getSamples(false));
		editorB.setSignal(getSamples(true));
	}

	private float[] getSamples(boolean bankB) {
		float[] samples = new float[WaveData.SAMPLES_PER_SEGMENT * 18];
		for(int i = 0; i < 18; i++) {
			int start = WaveData.SAMPLES_PER_SEGMENT * i;
			WaveData data;
			if(!bankB) {
				data = disk.getWaveDataA(i);
			} else {
				data = disk.getWaveDataB(i);
			}
			short[] s16 = data.getSamples();
			for(int j = 0; j < s16.length; j++) {
				samples[start + j] = s16[j] / (float) Short.MAX_VALUE;
			}
		}
		return samples;
	}

	public void exportBank(File path, boolean bankB) throws IOException {
		// concatenate wave data segments
		short[] samples = new short[WaveData.SAMPLES_PER_SEGMENT * 18];
		for(int i = 0; i < 18; i++) {
			int start = WaveData.SAMPLES_PER_SEGMENT * i;
			WaveData data;
			if(!bankB) {
				data = disk.getWaveDataA(i);
			} else {
				data = disk.getWaveDataB(i);
			}
			short[] s16 = data.getSamples();
			for(int j = 0; j < s16.length; j++) {
				samples[start + j] = s16[j];
			}
		}

		// write WAV file with 30kHz sample rate
		RiffWave out = new RiffWave();
		out.set(new WaveFormatChunk());
		out.set(new DataChunk());
		out.setSampleRate(30000);
		out.setSampleFormat(WaveFormatChunk.WAVE_FORMAT_PCM);
		out.setChannels(1);
		out.setBitsPerSample(16);
		out.set16bitSamples(samples);
		try(BufferedOutputStream wav = new BufferedOutputStream(new FileOutputStream(path))) {
			out.write(wav);
		}
	}
}
