package org.hackyourlife.s550;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.hackyourlife.s550.io.BEInputStream;
import org.hackyourlife.s550.io.BEOutputStream;
import org.hackyourlife.s550.io.Endianess;
import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class ToneParameter extends Struct {
	public static final byte ORG = 0;
	public static final byte SUB = 1;
	public static final byte FREQ_30k = 0;
	public static final byte FREQ_15k = 1;
	public static final byte WAVEBANK_A = 0;
	public static final byte WAVEBANK_B = 1;
	public static final byte LOOP_FORWARD = 0;
	public static final byte LOOP_ALTERNATING = 1;
	public static final byte LOOP_ONESHOT = 2;
	public static final byte LOOP_REVERSE = 3;

	private final RawString toneName = new RawString(8);
	private byte outputAssign;
	private byte sourceTone;
	private byte orgSubTone;
	private byte samplingFrequency;
	private byte origKeyNumber;
	private byte waveBank;
	private byte waveSegmentTop;
	private byte waveSegmentLength;
	private final byte[] startPoint = new byte[3];
	private final byte[] endPoint = new byte[3];
	private final byte[] loopPoint = new byte[3];
	private byte loopMode;
	private byte tvaLfoDepth;
	private byte lfoRate;
	private byte lfoSync;
	private byte lfoDelay;
	private byte lfoMode;
	private byte oscLfoDepth;
	private byte lfoPolarity;
	private byte lfoOffset;
	private byte transpose;
	private byte fineTune;
	private byte tvfCutOff;
	private byte tvfResonance;
	private byte tvfKeyFollow;
	private byte tvfLfoDepth;
	private byte tvfEgDepth;
	private byte tvfEgPolarity;
	private byte tvfLevelCurve;
	private byte tvfKeyRateFollow;
	private byte tvfVelocityRateFollow;
	private byte tvfZoom;
	private byte tvfSwitch;
	private byte benderSwitch;
	private byte tvaEnvSustainPoint;
	private byte tvaEnvEndPoint;
	private byte tvaEnvLevel1;
	private byte tvaEnvRate1;
	private byte tvaEnvLevel2;
	private byte tvaEnvRate2;
	private byte tvaEnvLevel3;
	private byte tvaEnvRate3;
	private byte tvaEnvLevel4;
	private byte tvaEnvRate4;
	private byte tvaEnvLevel5;
	private byte tvaEnvRate5;
	private byte tvaEnvLevel6;
	private byte tvaEnvRate6;
	private byte tvaEnvLevel7;
	private byte tvaEnvRate7;
	private byte tvaEnvLevel8;
	private byte tvaEnvRate8;
	private byte tvaEnvKeyRate;
	private byte level;
	private byte envVelRate;
	private byte recThreshold;
	private byte recPreTrigger;
	private byte recSamplingFrequency;
	private final byte[] recStartPoint = new byte[3];
	private final byte[] recEndPoint = new byte[3];
	private final byte[] recLoopPoint = new byte[3];
	private byte zoomT;
	private byte zoomL;
	private byte copySource;
	private byte loopTune;
	private byte tvaLevelCurve;
	private final byte[] loopLength = new byte[3];
	private byte pitchFollow;
	private byte tvaZoom;
	private byte tvfEnvSustainPoint;
	private byte tvfEnvEndPoint;
	private byte tvfEnvLevel1;
	private byte tvfEnvRate1;
	private byte tvfEnvLevel2;
	private byte tvfEnvRate2;
	private byte tvfEnvLevel3;
	private byte tvfEnvRate3;
	private byte tvfEnvLevel4;
	private byte tvfEnvRate4;
	private byte tvfEnvLevel5;
	private byte tvfEnvRate5;
	private byte tvfEnvLevel6;
	private byte tvfEnvRate6;
	private byte tvfEnvLevel7;
	private byte tvfEnvRate7;
	private byte tvfEnvLevel8;
	private byte tvfEnvRate8;
	private byte afterTouchSwitch;

	public String getName() {
		return toneName.get();
	}

	public void setName(String name) {
		toneName.set(name);
	}

	public byte getOutputAssign() {
		return outputAssign;
	}

	public void setOutputAssign(byte outputAssign) {
		if(outputAssign < 0 || outputAssign > 7) {
			throw new IllegalArgumentException("invalid output assign");
		}
		this.outputAssign = outputAssign;
	}

	public byte getSourceTone() {
		return sourceTone;
	}

	public void setSourceTone(byte sourceTone) {
		if(sourceTone < 0 || sourceTone > 31) {
			throw new IllegalArgumentException("invalid source tone");
		}
		this.sourceTone = sourceTone;
	}

	public byte getOrigSubTone() {
		return orgSubTone;
	}

	public void setOrigSubTone(byte orgSubTone) {
		if(orgSubTone < 0 || orgSubTone > 1) {
			throw new IllegalArgumentException("invalid org/sub tone");
		}
		this.orgSubTone = orgSubTone;
	}

	public byte getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(byte samplingFrequency) {
		if(samplingFrequency < 0 || samplingFrequency > 1) {
			throw new IllegalArgumentException("invalid sampling frequency");
		}
		this.samplingFrequency = samplingFrequency;
	}

	public byte getOrigKeyNumber() {
		return origKeyNumber;
	}

	public void setOrigKeyNumber(byte origKeyNumber) {
		if(origKeyNumber < 11 || origKeyNumber > 120) {
			throw new IllegalArgumentException("invalid orig key number");
		}
		this.origKeyNumber = origKeyNumber;
	}

	public byte getWaveBank() {
		return waveBank;
	}

	public void setWaveBank(byte waveBank) {
		if(waveBank < 0 || waveBank > 1) {
			throw new IllegalArgumentException("invalid wave bank");
		}
		this.waveBank = waveBank;
	}

	public byte getWaveSegmentTop() {
		return waveSegmentTop;
	}

	public void setWaveSegmentTop(byte waveSegmentTop) {
		if(waveSegmentTop < 0 || waveSegmentTop > 17) {
			throw new IllegalArgumentException("invalid wave segment top");
		}
		this.waveSegmentTop = waveSegmentTop;
	}

	public byte getWaveSegmentLength() {
		return waveSegmentLength;
	}

	public void setWaveSegmentLength(byte waveSegmentLength) {
		if(waveSegmentLength < 0 || waveSegmentLength > 18) {
			throw new IllegalArgumentException("invalid wave segment length");
		}
		this.waveSegmentLength = waveSegmentLength;
	}

	public int getStartPoint() {
		return Endianess.get24bitBE(startPoint);
	}

	public void setStartPoint(int startPoint) {
		if(startPoint < 0 || startPoint > 221180) {
			throw new IllegalArgumentException("invalid start point");
		}
		Endianess.set24bitBE(this.startPoint, startPoint);
	}

	public int getEndPoint() {
		return Endianess.get24bitBE(endPoint);
	}

	public void setEndPoint(int endPoint) {
		if(endPoint < 4 || endPoint > 221184) {
			throw new IllegalArgumentException("invalid end point");
		}
		Endianess.set24bitBE(this.endPoint, endPoint);
	}

	public int getLoopPoint() {
		return Endianess.get24bitBE(loopPoint);
	}

	public void setLoopPoint(int loopPoint) {
		if(loopPoint < 0 || loopPoint > 221184) {
			throw new IllegalArgumentException("invalid loop point");
		}
		Endianess.set24bitBE(this.loopPoint, loopPoint);
	}

	public byte getLoopMode() {
		return loopMode;
	}

	public void setLoopMode(byte loopMode) {
		if(loopMode < 0 || loopMode > 3) {
			throw new IllegalArgumentException("invalid loop mode");
		}
		this.loopMode = loopMode;
	}

	public byte getTvaLfoDepth() {
		return tvaLfoDepth;
	}

	public void setTvaLfoDepth(byte tvaLfoDepth) {
		if(tvaLfoDepth < 0) {
			throw new IllegalArgumentException("invalid TVA LFO depth");
		}
		this.tvaLfoDepth = tvaLfoDepth;
	}

	public byte getLfoRate() {
		return lfoRate;
	}

	public void setLfoRate(byte lfoRate) {
		if(lfoRate < 0 || lfoRate > 1) {
			throw new IllegalArgumentException("invalid LFO rate");
		}
		this.lfoRate = lfoRate;
	}

	public byte getLfoMode() {
		return lfoMode;
	}

	public void setLfoMode(byte lfoMode) {
		if(lfoMode < 0 || lfoMode > 1) {
			throw new IllegalArgumentException("invalid LFO mode");
		}
		this.lfoMode = lfoMode;
	}

	public byte getOscLfoDepth() {
		return oscLfoDepth;
	}

	public void setOscLfoDepth(byte oscLfoDepth) {
		if(oscLfoDepth < 0) {
			throw new IllegalArgumentException("invalid OSC LFO depth");
		}
		this.oscLfoDepth = oscLfoDepth;
	}

	public byte getLfoPolarity() {
		return lfoPolarity;
	}

	public void setLfoPolarity(byte lfoPolarity) {
		if(lfoPolarity < 0 || lfoPolarity > 1) {
			throw new IllegalArgumentException("invalid LFO polarity");
		}
		this.lfoPolarity = lfoPolarity;
	}

	public byte getLfoOffset() {
		return lfoOffset;
	}

	public void setLfoOffset(byte lfoOffset) {
		if(lfoOffset < 0) {
			throw new IllegalArgumentException("invalid LFO offset");
		}
		this.lfoOffset = lfoOffset;
	}

	public byte getTranspose() {
		return transpose;
	}

	public void setTranspose(byte transpose) {
		if(transpose < 0) {
			throw new IllegalArgumentException("invalid tranpsose");
		}
		this.transpose = transpose;
	}

	public byte getFineTune() {
		return (byte) (fineTune << 1 >> 1);
	}

	public void setFineTune(byte fineTune) {
		if(fineTune < -64 || fineTune > 63) {
			throw new IllegalArgumentException("invalid fine tune");
		}
		this.fineTune = fineTune;
	}

	public byte getTvfCutoff() {
		return tvfCutOff;
	}

	public void setTvfCutoff(byte tvfCutoff) {
		if(tvfCutoff < 0) {
			throw new IllegalArgumentException("invalid TVF cutoff");
		}
		this.tvfCutOff = tvfCutoff;
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		toneName.read(in);
		outputAssign = in.read8bit();
		sourceTone = in.read8bit();
		orgSubTone = in.read8bit();
		samplingFrequency = in.read8bit();
		origKeyNumber = in.read8bit();
		waveBank = in.read8bit();
		waveSegmentTop = in.read8bit();
		waveSegmentLength = in.read8bit();
		in.read(startPoint);
		in.read(endPoint);
		in.read(loopPoint);
		loopMode = in.read8bit();
		tvaLfoDepth = in.read8bit();
		in.skip(1);
		lfoRate = in.read8bit();
		lfoSync = in.read8bit();
		lfoDelay = in.read8bit();
		in.skip(1);
		lfoMode = in.read8bit();
		oscLfoDepth = in.read8bit();
		lfoPolarity = in.read8bit();
		lfoOffset = in.read8bit();
		transpose = in.read8bit();
		fineTune = in.read8bit();
		tvfCutOff = in.read8bit();
		tvfResonance = in.read8bit();
		tvfKeyFollow = in.read8bit();
		in.skip(1);
		tvfLfoDepth = in.read8bit();
		tvfEgDepth = in.read8bit();
		tvfEgPolarity = in.read8bit();
		tvfLevelCurve = in.read8bit();
		tvfKeyRateFollow = in.read8bit();
		tvfVelocityRateFollow = in.read8bit();
		tvfZoom = in.read8bit();
		tvfSwitch = in.read8bit();
		benderSwitch = in.read8bit();
		tvaEnvSustainPoint = in.read8bit();
		tvaEnvEndPoint = in.read8bit();
		tvaEnvLevel1 = in.read8bit();
		tvaEnvRate1 = in.read8bit();
		tvaEnvLevel2 = in.read8bit();
		tvaEnvRate2 = in.read8bit();
		tvaEnvLevel3 = in.read8bit();
		tvaEnvRate3 = in.read8bit();
		tvaEnvLevel4 = in.read8bit();
		tvaEnvRate4 = in.read8bit();
		tvaEnvLevel5 = in.read8bit();
		tvaEnvRate5 = in.read8bit();
		tvaEnvLevel6 = in.read8bit();
		tvaEnvRate6 = in.read8bit();
		tvaEnvLevel7 = in.read8bit();
		tvaEnvRate7 = in.read8bit();
		tvaEnvLevel8 = in.read8bit();
		tvaEnvRate8 = in.read8bit();
		in.skip(1);
		tvaEnvKeyRate = in.read8bit();
		level = in.read8bit();
		envVelRate = in.read8bit();
		recThreshold = in.read8bit();
		recPreTrigger = in.read8bit();
		recSamplingFrequency = in.read8bit();
		in.read(recStartPoint);
		in.read(recEndPoint);
		in.read(recLoopPoint);
		zoomT = in.read8bit();
		zoomL = in.read8bit();
		copySource = in.read8bit();
		loopTune = in.read8bit();
		tvaLevelCurve = in.read8bit();
		in.skip(12);
		in.read(loopLength);
		pitchFollow = in.read8bit();
		tvaZoom = in.read8bit();
		tvfEnvSustainPoint = in.read8bit();
		tvfEnvEndPoint = in.read8bit();
		tvfEnvLevel1 = in.read8bit();
		tvfEnvRate1 = in.read8bit();
		tvfEnvLevel2 = in.read8bit();
		tvfEnvRate2 = in.read8bit();
		tvfEnvLevel3 = in.read8bit();
		tvfEnvRate3 = in.read8bit();
		tvfEnvLevel4 = in.read8bit();
		tvfEnvRate4 = in.read8bit();
		tvfEnvLevel5 = in.read8bit();
		tvfEnvRate5 = in.read8bit();
		tvfEnvLevel6 = in.read8bit();
		tvfEnvRate6 = in.read8bit();
		tvfEnvLevel7 = in.read8bit();
		tvfEnvRate7 = in.read8bit();
		tvfEnvLevel8 = in.read8bit();
		tvfEnvRate8 = in.read8bit();
		afterTouchSwitch = in.read8bit();
		in.skip(2);
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		toneName.write(out);
		out.write8bit(outputAssign);
		out.write8bit(sourceTone);
		out.write8bit(orgSubTone);
		out.write8bit(samplingFrequency);
		out.write8bit(origKeyNumber);
		out.write8bit(waveBank);
		out.write8bit(waveSegmentTop);
		out.write8bit(waveSegmentLength);
		out.write(startPoint);
		out.write(endPoint);
		out.write(loopPoint);
		out.write8bit(loopMode);
		out.write8bit(tvaLfoDepth);
		out.write8bit((byte) 0);
		out.write8bit(lfoRate);
		out.write8bit(lfoSync);
		out.write8bit(lfoDelay);
		out.write8bit((byte) 0);
		out.write8bit(lfoMode);
		out.write8bit(oscLfoDepth);
		out.write8bit(lfoPolarity);
		out.write8bit(lfoOffset);
		out.write8bit(transpose);
		out.write8bit(fineTune);
		out.write8bit(tvfCutOff);
		out.write8bit(tvfResonance);
		out.write8bit(tvfKeyFollow);
		out.write8bit((byte) 0);
		out.write8bit(tvfLfoDepth);
		out.write8bit(tvfEgDepth);
		out.write8bit(tvfEgPolarity);
		out.write8bit(tvfLevelCurve);
		out.write8bit(tvfKeyRateFollow);
		out.write8bit(tvfVelocityRateFollow);
		out.write8bit(tvfZoom);
		out.write8bit(tvfSwitch);
		out.write8bit(benderSwitch);
		out.write8bit(tvaEnvSustainPoint);
		out.write8bit(tvaEnvEndPoint);
		out.write8bit(tvaEnvLevel1);
		out.write8bit(tvaEnvRate1);
		out.write8bit(tvaEnvLevel2);
		out.write8bit(tvaEnvRate2);
		out.write8bit(tvaEnvLevel3);
		out.write8bit(tvaEnvRate3);
		out.write8bit(tvaEnvLevel4);
		out.write8bit(tvaEnvRate4);
		out.write8bit(tvaEnvLevel5);
		out.write8bit(tvaEnvRate5);
		out.write8bit(tvaEnvLevel6);
		out.write8bit(tvaEnvRate6);
		out.write8bit(tvaEnvLevel7);
		out.write8bit(tvaEnvRate7);
		out.write8bit(tvaEnvLevel8);
		out.write8bit(tvaEnvRate8);
		out.write8bit((byte) 0);
		out.write8bit(tvaEnvKeyRate);
		out.write8bit(level);
		out.write8bit(envVelRate);
		out.write8bit(recThreshold);
		out.write8bit(recPreTrigger);
		out.write8bit(recSamplingFrequency);
		out.write(recStartPoint);
		out.write(recEndPoint);
		out.write(recLoopPoint);
		out.write8bit(zoomT);
		out.write8bit(zoomL);
		out.write8bit(copySource);
		out.write8bit(loopTune);
		out.write8bit(tvaLevelCurve);
		out.write((byte) 0, 12);
		out.write(loopLength);
		out.write8bit(pitchFollow);
		out.write8bit(tvaZoom);
		out.write8bit(tvfEnvSustainPoint);
		out.write8bit(tvfEnvEndPoint);
		out.write8bit(tvfEnvLevel1);
		out.write8bit(tvfEnvRate1);
		out.write8bit(tvfEnvLevel2);
		out.write8bit(tvfEnvRate2);
		out.write8bit(tvfEnvLevel3);
		out.write8bit(tvfEnvRate3);
		out.write8bit(tvfEnvLevel4);
		out.write8bit(tvfEnvRate4);
		out.write8bit(tvfEnvLevel5);
		out.write8bit(tvfEnvRate5);
		out.write8bit(tvfEnvLevel6);
		out.write8bit(tvfEnvRate6);
		out.write8bit(tvfEnvLevel7);
		out.write8bit(tvfEnvRate7);
		out.write8bit(tvfEnvLevel8);
		out.write8bit(tvfEnvRate8);
		out.write8bit(afterTouchSwitch);
		out.write((byte) 0, 2);
	}

	public void copyFrom(ToneParameter param) {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
				WordOutputStream words = new BEOutputStream(bos)) {
			param.write(words);
			words.flush();
			byte[] data = bos.toByteArray();
			try(WordInputStream in = new BEInputStream(new ByteArrayInputStream(data))) {
				read(in);
			}
		} catch(IOException e) {
			// this should never happen
			e.printStackTrace();
		}
	}
}
