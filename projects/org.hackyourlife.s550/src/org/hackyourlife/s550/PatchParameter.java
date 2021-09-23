package org.hackyourlife.s550;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.hackyourlife.s550.io.BEInputStream;
import org.hackyourlife.s550.io.BEOutputStream;
import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class PatchParameter extends Struct {
	// S-50 leftovers
	private byte modulationDepth;

	// S-550 stuff
	private final RawString patchName = new RawString(12);
	private byte bendRange;
	private byte afterTouchSense;
	private byte keyMode;
	private byte velocitySwThreshold;
	private final byte[] toneToKey1 = new byte[109];
	private final byte[] toneToKey2 = new byte[109];
	private byte copySource;
	private byte octaveShift;
	private byte outputLevel;
	private byte detune;
	private byte velocityMixRatio;
	private byte afterTouchAssign;
	private byte keyAssign;
	private byte outputAssign;

	public String getName() {
		return patchName.get();
	}

	public void setName(String name) {
		patchName.set(name);
	}

	public byte getBendRange() {
		return bendRange;
	}

	public void setBendRange(byte bendRange) {
		if(bendRange < 0 || bendRange > 12) {
			throw new IllegalArgumentException("invalid bend range");
		}
		this.bendRange = bendRange;
	}

	public byte getAfterTouchSense() {
		return afterTouchSense;
	}

	public void setAfterTouchSense(byte afterTouchSense) {
		if(afterTouchSense < 0) {
			throw new IllegalArgumentException("invalid after touch sense");
		}
		this.afterTouchSense = afterTouchSense;
	}

	public byte getKeyMode() {
		return keyMode;
	}

	public void setKeyMode(byte keyMode) {
		if(keyMode < 0 || keyMode > 4) {
			throw new IllegalArgumentException("invalid key mode");
		}
		this.keyMode = keyMode;
	}

	public byte getVelocitySwitchThreshold() {
		return velocitySwThreshold;
	}

	public void setVelocitySwitchThreshold(byte velocitySwitchThreshold) {
		if(velocitySwitchThreshold < 0) {
			throw new IllegalArgumentException("invalid velocity switch threshold");
		}
		this.velocitySwThreshold = velocitySwitchThreshold;
	}

	public byte getCopySource() {
		return copySource;
	}

	public void setCopySource(byte copySource) {
		if(copySource < 0 || copySource > 7) {
			throw new IllegalArgumentException("invalid copy source");
		}
		this.copySource = copySource;
	}

	public byte getOctaveShift() {
		return octaveShift;
	}

	public void setOctaveShift(byte octaveShift) {
		if(octaveShift < -2 || octaveShift > 2) {
			throw new IllegalArgumentException("invalid octave shift");
		}
		this.octaveShift = octaveShift;
	}

	public byte getOutputLevel() {
		return outputLevel;
	}

	public void setOutputLevel(byte outputLevel) {
		if(outputLevel < 0) {
			throw new IllegalArgumentException("invalid output level");
		}
		this.outputLevel = outputLevel;
	}

	public byte getDetune() {
		return detune;
	}

	public void setDetune(byte detune) {
		if(detune < -64 || detune > 63) {
			throw new IllegalArgumentException("invalid detune");
		}
		this.detune = detune;
	}

	public byte getVelocityMixRatio() {
		return velocityMixRatio;
	}

	public void setVelocityMixRatio(byte velocityMixRatio) {
		if(velocityMixRatio < 0) {
			throw new IllegalArgumentException("invalid velocity mix ratio");
		}
		this.velocityMixRatio = velocityMixRatio;
	}

	public byte getAfterTouchAssign() {
		return afterTouchAssign;
	}

	public void setAfterTouchAssign(byte afterTouchAssign) {
		if(afterTouchAssign < 0 || afterTouchAssign > 4) {
			throw new IllegalArgumentException("invalid after touch assign");
		}
		this.afterTouchAssign = afterTouchAssign;
	}

	public byte getKeyAssign() {
		return keyAssign;
	}

	public void setKeyAssign(byte keyAssign) {
		if(keyAssign < 0 || keyAssign > 1) {
			throw new IllegalArgumentException("invalid key assign");
		}
		this.keyAssign = keyAssign;
	}

	public byte getOutputAssign() {
		return outputAssign;
	}

	public void setOutputAssign(byte outputAssign) {
		if(outputAssign < 0 || outputAssign > 8) {
			throw new IllegalArgumentException("invalid output assign");
		}
		this.outputAssign = outputAssign;
	}

	public byte getToneToKey(int ab, int key) {
		if(key < 0 || key > 108) {
			throw new IllegalArgumentException("invalid key");
		}
		if(ab == 0) {
			return (byte) (toneToKey1[key] & 0x1F);
		} else if(ab == 1) {
			return (byte) (toneToKey2[key] & 0x1F);
		} else {
			throw new IllegalArgumentException("invalid table");
		}
	}

	public void setToneToKey(int ab, int key, byte tone) {
		if(key < 0 || key > 108) {
			throw new IllegalArgumentException("invalid key");
		}
		if(tone < 0 || tone >= 32) {
			throw new IllegalArgumentException("invalid tone");
		}
		if(ab == 0) {
			toneToKey1[key] = tone;
		} else if(ab == 1) {
			toneToKey2[key] = tone;
		} else {
			throw new IllegalArgumentException("invalid table");
		}
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		patchName.read(in);
		bendRange = in.read8bit();
		in.skip(1);
		afterTouchSense = in.read8bit();
		keyMode = in.read8bit();
		velocitySwThreshold = in.read8bit();
		in.read(toneToKey1);
		in.read(toneToKey2);
		copySource = in.read8bit();
		octaveShift = in.read8bit();
		outputLevel = in.read8bit();
		modulationDepth = in.read8bit();
		detune = in.read8bit();
		velocityMixRatio = in.read8bit();
		afterTouchAssign = in.read8bit();
		keyAssign = in.read8bit();
		outputAssign = in.read8bit();
		in.skip(12);
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		patchName.write(out);
		out.write8bit(bendRange);
		out.write8bit((byte) 0);
		out.write8bit(afterTouchSense);
		out.write8bit(keyMode);
		out.write8bit(velocitySwThreshold);
		out.write(toneToKey1);
		out.write(toneToKey2);
		out.write8bit(copySource);
		out.write8bit(octaveShift);
		out.write8bit(outputLevel);
		out.write8bit(modulationDepth);
		out.write8bit(detune);
		out.write8bit(velocityMixRatio);
		out.write8bit(afterTouchAssign);
		out.write8bit(keyAssign);
		out.write8bit(outputAssign);
		out.write((byte) 0, 12); // TODO: figure this out
	}

	@Override
	public String toString() {
		return "PatchParameter[patchName=" + patchName.get() + "]";
	}

	public void copyFrom(PatchParameter param) {
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
