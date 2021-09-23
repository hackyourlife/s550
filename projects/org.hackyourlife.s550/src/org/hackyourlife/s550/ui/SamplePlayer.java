package org.hackyourlife.s550.ui;

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.hackyourlife.s550.FloppyDisk;
import org.hackyourlife.s550.WaveData;
import org.hackyourlife.s550.widget.WaveformEditor;

public class SamplePlayer {
	private int sampleRate;
	private SourceDataLine waveout;

	private WaveformEditor editor;

	private volatile boolean stop = true;
	private List<Thread> threads = new LinkedList<>();

	public SamplePlayer(int sampleRate) throws LineUnavailableException {
		this.sampleRate = sampleRate;

		// @formatter:off
		AudioFormat format = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,	// encoding
				sampleRate,				// sample rate
				16,					// bit/sample
				1,					// channels
				2,
				sampleRate,
				true					// big-endian
		);
		// @formatter:on

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		if(!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException("Line matching " + info + " not supported");
		}

		waveout = (SourceDataLine) AudioSystem.getLine(info);
		waveout.open(format, 4096);
	}

	public void setWaveformEditor(WaveformEditor editor) {
		this.editor = editor;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void start() {
		stop = false;
		if(editor != null) {
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						long startTime = System.currentTimeMillis();
						while(!stop) {
							Thread.sleep(20);
							long now = System.currentTimeMillis();
							double time = (now - startTime) / 1000.0;
							editor.setCurrentTime(time);
						}
					} catch(InterruptedException e) {
						// stop
					}
				}
			};
			threads.add(t);
			t.start();
		}
		waveout.start();
	}

	public void stop() {
		if(stop) {
			return;
		}
		stop = true;
		waveout.stop();
		waveout.flush();
		for(Thread t : threads) {
			t.interrupt();
		}
		for(Thread t : threads) {
			try {
				t.join();
			} catch(InterruptedException e) {
				// nothing
			}
		}
		threads.clear();
		if(editor != null) {
			editor.setCurrentTime(0);
		}
	}

	public void close() {
		stop();
		waveout.close();
	}

	public void play(FloppyDisk floppy, boolean bankB) {
		byte[] samples = new byte[WaveData.SAMPLES_PER_SEGMENT * 18 * 2];
		for(int i = 0; i < 18; i++) {
			int start = WaveData.SAMPLES_PER_SEGMENT * i * 2;
			WaveData data;
			if(!bankB) {
				data = floppy.getWaveDataA(i);
			} else {
				data = floppy.getWaveDataB(i);
			}
			short[] s16 = data.getSamples();
			for(int j = 0; j < s16.length; j++) {
				samples[start + j * 2] = (byte) (s16[j] >> 8);
				samples[start + j * 2 + 1] = (byte) s16[j];
			}
		}

		Thread t = new Thread() {
			@Override
			public void run() {
				waveout.write(samples, 0, samples.length);
				SamplePlayer.this.stop();
			}
		};
		threads.add(t);
		t.start();
	}
}
