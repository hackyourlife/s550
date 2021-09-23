package org.hackyourlife.s550.widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class WaveformEditor extends JComponent {
	private int divisions = 20;
	private double velocityScale = 10;
	private double velocityThreshold = 10;
	private boolean useVelocityThreshold = false;

	private double beamIntensity = 1.0;

	private int sampleRate;
	private double timeDivision;
	private double scale;

	private double defaultTimeDivision = 0.1;
	private double defaultVoltageDivision = 0.1;

	private double offset;
	private float[] samples;

	private Color gridColor;
	private Color currentTimeColor;
	private Color signalColor;
	private int signalColorRGB;

	private boolean cursorTimeVisible;
	private boolean cursorLevelVisible;
	private float cursorLevel1;
	private float cursorLevel2;
	private double cursorTime1;
	private double cursorTime2;

	private double currentTime;

	private BufferedImage buffer;
	private boolean dirty;

	public WaveformEditor() {
		samples = new float[0];

		setSampleRate(44100);
		setTimeDivision(defaultTimeDivision);
		setVoltageDivision(defaultVoltageDivision);
		setOffset(0);

		setBackground(Color.BLACK);
		setForeground(Color.GREEN);

		setGridColor(new Color(0, 64, 0));
		setCurrentTimeColor(Color.RED);
		setSignalColor(Color.CYAN);

		setCursorTimeVisible(false);
		setCursorLevelVisible(false);
		setCursorLevel1(0);
		setCursorLevel2(0);
		setCursorTime1(0);
		setCursorTime2(0);
		setCurrentTime(0);

		buffer = null;
		dirty = true;

		MouseController mouse = new MouseController();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addMouseWheelListener(mouse);
	}

	public void setLogicAnalyzer(boolean value) {
		useVelocityThreshold = value;
		dirty = true;
		repaint();
	}

	public void setGridColor(Color color) {
		gridColor = color;
		repaint();
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setCurrentTimeColor(Color color) {
		currentTimeColor = color;
		repaint();
	}

	public Color getCurrentTimeColor() {
		return currentTimeColor;
	}

	public void setSignalColor(Color color) {
		signalColor = color;
		signalColorRGB = color.getRGB() & 0x00FFFFFF;
		dirty = true;
		repaint();
	}

	public Color getSignalColor() {
		return signalColor;
	}

	public void setCursorLevel1(float value) {
		cursorLevel1 = value;
		if(cursorLevelVisible) {
			repaint();
		}
	}

	public void setCursorLevel2(float value) {
		cursorLevel2 = value;
		if(cursorLevelVisible) {
			repaint();
		}
	}

	public void setCursorTime1(double value) {
		cursorTime1 = value;
		if(cursorTimeVisible) {
			repaint();
		}
	}

	public void setCursorTime2(double value) {
		cursorTime2 = value;
		if(cursorTimeVisible) {
			repaint();
		}
	}

	public float getCursorLevel1() {
		return cursorLevel1;
	}

	public float getCursorLevel2() {
		return cursorLevel2;
	}

	public double getCursorTime1() {
		return cursorTime1;
	}

	public double getCursorTime2() {
		return cursorTime2;
	}

	public void setCursorLevelVisible(boolean visible) {
		cursorLevelVisible = visible;
		repaint();
	}

	public boolean isCursorLevelVisible() {
		return cursorLevelVisible;
	}

	public void setCursorTimeVisible(boolean visible) {
		cursorTimeVisible = visible;
		repaint();
	}

	public boolean isCursorTimeVisible() {
		return cursorTimeVisible;
	}

	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
		repaint();
	}

	public double getCurrentTime() {
		return currentTime;
	}

	private void prepare() {
		int width = getWidth();
		int height = getHeight();

		boolean draw = false;
		if(buffer == null) {
			buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			draw = true;
		} else if(buffer.getWidth() != width || buffer.getHeight() != height) {
			buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			draw = true;
		}

		if(!draw && !dirty) {
			return;
		}

		dirty = false;

		int cy = height / 2;

		double tickDistanceY = height / (double) divisions;

		double visibleSamples = (timeDivision * divisions) * sampleRate;
		double startSample = offset * sampleRate;

		double yscale = tickDistanceY / scale;

		double samplesPerPixel = visibleSamples / width;

		// draw signal
		float last = 0;
		float lastN = 0;

		if(samplesPerPixel <= 1) {
			// variant 1: using lines, because there are more pixels than samples
			Graphics2D g = buffer.createGraphics();
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, width, height);

			for(int i = 0; i < width; i++) {
				double start = startSample + i * samplesPerPixel;
				float value = getSample(start);
				float valueN = getNearestSample(start);
				float valueN1 = getNearestSample(Math.floor(start));
				float valueN2 = getNearestSample(Math.ceil(start));
				if(Float.isNaN(value)) {
					continue;
				}
				if(Float.isNaN(valueN)) {
					valueN = value;
				}
				if(Float.isNaN(valueN1)) {
					valueN1 = valueN;
				}
				if(Float.isNaN(valueN2)) {
					valueN2 = valueN;
				}
				if(i == 0) {
					last = value;
					lastN = valueN;
				}

				int y = (int) Math.round(value * yscale);
				int y1 = (int) Math.round(last * yscale);
				double velocity = Math.abs(value - last) * velocityScale / scale;

				double velocityN = Math.abs(valueN2 - valueN1) * velocityScale / scale;

				if(useVelocityThreshold && velocityN > velocityThreshold) {
					y = (int) Math.round(valueN * yscale);
					y1 = (int) Math.round(lastN * yscale);
					velocity = Math.abs(valueN - lastN) * velocityScale / scale;
					last = valueN;
				} else {
					last = value;
				}
				lastN = valueN;

				double intensity = velocity > 0 ? 1 / velocity : 1;
				if(intensity > 1) {
					intensity = 1;
				}

				g.setColor(getSignalColor(intensity));
				if(y != y1) {
					g.drawLine(i, cy - y1, i, cy - y);
				} else {
					g.drawLine(i, cy - y1, i + 1, cy - y);
				}
			}

			g.dispose();
		} else {
			// variant 2: using pixels, because there are more pixels than samples
			DataBufferInt buf = (DataBufferInt) buffer.getRaster().getDataBuffer();
			int[] data = buf.getData();

			double[] sum = new double[height];
			for(int i = 0; i < width; i++) {
				double start = startSample + i * samplesPerPixel;
				for(int n = 0; n < sum.length; n++) {
					sum[n] = 0;
				}
				last = getSample(start);
				{
					last = getSample(start - 1);
					float value = getSample(start);
					if(Float.isNaN(last)) {
						last = value;
					}
					int y = cy - (int) Math.round(value * yscale);
					int y1 = cy - (int) Math.round(last * yscale);
					double velocity = Math.abs(value - last) * velocityScale / scale;
					double intensity = velocity > 0 ? 1 / velocity : 1;
					double contribution = Math.ceil(start) - start;
					if(y1 > y) {
						int tmp = y;
						y = y1;
						y1 = tmp;
					}
					if(y == y1) {
						if(y >= 0 && y < sum.length) {
							sum[y] += intensity * contribution;
						}
					} else {
						for(int n = y1; n < y; n++) {
							if(n >= 0 && n < sum.length) {
								sum[n] += intensity * contribution;
							}
						}
					}
					last = value;
				}
				for(int sample = 1; sample < Math.ceil(samplesPerPixel); sample++) {
					float value = getSample(start + sample);
					int y = cy - (int) Math.round(value * yscale);
					int y1 = cy - (int) Math.round(last * yscale);
					double velocity = Math.abs(value - last) * velocityScale / scale;
					double intensity = velocity > 0 ? 1 / velocity : 1;
					if(y1 > y) {
						int tmp = y;
						y = y1;
						y1 = tmp;
					}
					if(y == y1) {
						if(y >= 0 && y < sum.length) {
							sum[y] += intensity;
						}
					} else {
						for(int n = y1; n < y; n++) {
							if(n >= 0 && n < sum.length) {
								sum[n] += intensity;
							}
						}
					}
					last = value;
				}
				if(samplesPerPixel != Math.floor(samplesPerPixel)) {
					float value = getSample(start + samplesPerPixel);
					int y = cy - (int) Math.round(value * yscale);
					int y1 = cy - (int) Math.round(last * yscale);
					double velocity = Math.abs(value - last) * velocityScale / scale;
					double intensity = velocity > 0 ? 1 / velocity : 1;
					double contribution = samplesPerPixel - Math.floor(samplesPerPixel);
					if(y1 > y) {
						int tmp = y;
						y = y1;
						y1 = tmp;
					}
					if(y == y1) {
						if(y >= 0 && y < sum.length) {
							sum[y] += intensity * contribution;
						}
					} else {
						for(int n = y1; n < y; n++) {
							if(n >= 0 && n < sum.length) {
								sum[n] += intensity * contribution;
							}
						}
					}
					last = value;
				}

				for(int y = 0; y < height; y++) {
					double intensity = sum[y] / samplesPerPixel;
					if(intensity > 1) {
						intensity = 1;
					}
					if(intensity > 0) {
						int color = getSignalColorRGB(intensity);
						data[y * width + i] = color;
						// g.drawLine(i, y, i, y + 1);
					} else {
						data[y * width + i] = 0;
					}
				}
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		int cx = width / 2;
		int cy = height / 2;

		double tickDistanceX = width / (double) divisions;
		double tickDistanceY = height / (double) divisions;

		double visibleSamples = (timeDivision * divisions) * sampleRate;
		double startSample = offset * sampleRate;

		double yscale = tickDistanceY / scale;

		double samplesPerPixel = visibleSamples / width;

		double offsetX = offset / timeDivision;
		double offsetDivX = offsetX - Math.floor(offsetX);

		g.setColor(gridColor);
		for(int i = 0; i <= divisions; i++) {
			int x = (int) Math.round(tickDistanceX * (i - offsetDivX));
			g.drawLine(x, 0, x, height);
		}

		for(int i = 0; i <= (divisions / 2); i++) {
			int y = (int) Math.round(tickDistanceY * i);
			g.drawLine(0, cy - y, width, cy - y);
			g.drawLine(0, cy + y, width, cy + y);
		}

		if(cursorLevelVisible) {
			g.setColor(getForeground());
			// cursor 1
			int y = (int) Math.round(cursorLevel1 * yscale);
			for(int i = 0; i < width / 2; i += 20) {
				g.drawLine(cx + i - 3, cy - y, cx + i + 3, cy - y);
				g.drawLine(cx - i + 3, cy - y, cx - i - 3, cy - y);
			}

			// cursor 2
			y = (int) Math.round(cursorLevel2 * yscale);
			for(int i = 0; i < width / 2; i += 20) {
				g.drawLine(cx + i - 3, cy - y, cx + i + 3, cy - y);
				g.drawLine(cx - i + 3, cy - y, cx - i - 3, cy - y);
			}
		}

		// TODO: implement time cursor

		// grid
		g.setColor(getForeground());
		g.drawLine(0, cy, width, cy);
		g.drawLine(0, 0, 0, height);

		// ticks: X axis
		for(int i = 0; i <= divisions; i++) {
			int x = (int) Math.round(tickDistanceX * (i - offsetDivX));
			g.drawLine(x, cy - 5, x, cy + 5);
		}

		// ticks: Y axis
		for(int i = 0; i <= (divisions / 2); i++) {
			int y = (int) Math.round(tickDistanceY * i);
			g.drawLine(0, cy - y, 5, cy - y);
			g.drawLine(0, cy + y, 5, cy + y);
		}

		prepare();
		g.drawImage(buffer, 0, 0, this);

		if(currentTime >= offset && currentTime <= (offset + timeDivision * divisions)) {
			double currentSample = currentTime * sampleRate;
			int pos = (int) ((currentSample - startSample) / samplesPerPixel);
			g.setColor(getCurrentTimeColor());
			g.drawLine(pos, 0, pos, height);
		}
	}

	public Color getSignalColor(double intensity) {
		int i = (int) (intensity * beamIntensity * 255.0);
		if(i > 255) {
			i = 255;
		}
		return new Color(signalColor.getRed(), signalColor.getGreen(), signalColor.getBlue(), i);
	}

	public int getSignalColorRGB(double intensity) {
		int i = (int) (intensity * beamIntensity * 255.0);
		if(i > 255) {
			i = 255;
		}
		return signalColorRGB | (i << 24);
	}

	private float getSample(double sample) {
		if(sample < 0 || sample >= samples.length) {
			return Float.NaN;
		} else {
			int first = (int) Math.floor(sample);
			int second = (int) Math.ceil(sample);
			if(first < 0) {
				first = 0;
			}
			if(second >= samples.length) {
				second = samples.length - 1;
			}
			float val1 = samples[first];
			float val2 = samples[second];
			float k = val2 - val1;
			float d = (float) (sample - first);
			return val1 + k * d;
		}
	}

	private float getNearestSample(double sample) {
		if(sample < 0 || sample >= samples.length) {
			return Float.NaN;
		} else {
			int index = (int) Math.round(sample);
			if(index < 0) {
				index = 0;
			}
			if(index >= samples.length) {
				index = samples.length - 1;
			}
			return samples[index];
		}
	}

	public void setTimeDivision(double timeDivision) {
		this.timeDivision = timeDivision;
		dirty = true;
		repaint();
	}

	public double getTimeDivision() {
		return timeDivision;
	}

	public void setVoltageDivision(double voltageDivision) {
		if(voltageDivision < 0.0001) {
			return;
		} else if(voltageDivision > 10) {
			return;
		}
		scale = voltageDivision;
		dirty = true;
		repaint();
	}

	public double getVoltageDivision() {
		return scale;
	}

	public void setDefaultDivision(double voltage, double time) {
		defaultVoltageDivision = voltage;
		defaultTimeDivision = time;
	}

	public void setSignal(float[] signal) {
		samples = signal;
		dirty = true;
		repaint();
	}

	public float[] getSignal() {
		return samples;
	}

	public void setOffset(double offset) {
		double limit = getScreenStartTime();
		if(limit < 0) {
			this.offset = 0;
		} else if(offset < 0) {
			this.offset = 0;
		} else if(offset > limit) {
			this.offset = limit;
		} else {
			this.offset = offset;
		}
		dirty = true;
		repaint();
	}

	public double getOffset() {
		return offset;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
		dirty = true;
		repaint();
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setBeamIntensity(double intensity) {
		beamIntensity = intensity;
		dirty = true;
		repaint();
	}

	public double getBeamIntensity() {
		return beamIntensity;
	}

	public double getScreenStartTime() {
		return samples.length / (double) sampleRate - timeDivision * divisions;
	}

	public double getMinTimeDivision() {
		return 2.0f / sampleRate;
	}

	public double getMaxTimeDivision() {
		return samples.length / (double) sampleRate;
	}

	private class MouseController extends MouseAdapter {
		private int x;
		private double startOffset;

		@Override
		public void mousePressed(MouseEvent e) {
			x = e.getX();
			startOffset = offset;

			if(e.getButton() == MouseEvent.BUTTON3) {
				JMenuItem reset = new JMenuItem("Reset");
				reset.setMnemonic('R');
				reset.addActionListener(ev -> {
					setTimeDivision(defaultTimeDivision);
					setVoltageDivision(defaultVoltageDivision);
					setOffset(0);
				});
				JPopupMenu menu = new JPopupMenu();
				menu.add(reset);
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
				// middle mouse button
				int px = e.getX();
				int moved = px - x;
				int width = getWidth();
				double samplesPerPixel = ((timeDivision * divisions) * sampleRate) / width;
				double movedSamples = moved * samplesPerPixel;
				if(getScreenStartTime() < 0) {
					setOffset(0);
				} else {
					setOffset(startOffset - movedSamples / sampleRate);
				}
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();

			if((e.getModifiersEx() & MouseWheelEvent.CTRL_DOWN_MASK) != 0) {
				double div = getVoltageDivision() * Math.pow(2.0, notches);
				setVoltageDivision(div);
			} else {
				double div = getTimeDivision() * Math.pow(2.0, notches);
				int px = e.getX();
				int width = getWidth();

				double samplesPerPixel = ((timeDivision * divisions) * sampleRate) / width;
				double cursorSample = offset * sampleRate + px * samplesPerPixel;

				samplesPerPixel = ((div * divisions) * sampleRate) / width;
				double off = (cursorSample - px * samplesPerPixel) / sampleRate;

				if(div >= getMinTimeDivision() && div <= getMaxTimeDivision()) {
					setTimeDivision(div);
					setOffset(off);
				}
			}
		}
	}
}
