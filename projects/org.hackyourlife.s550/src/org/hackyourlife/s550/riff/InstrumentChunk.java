package org.hackyourlife.s550.riff;

import java.io.IOException;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class InstrumentChunk extends Chunk {
	public static final int MAGIC = 0x74736e69; // 'inst'

	private byte unshiftedNote;
	private byte fineTune;
	private byte gain;
	private byte lowNote;
	private byte highNote;
	private byte lowVelocity;
	private byte highVelocity;

	public InstrumentChunk() {
		super(MAGIC);
		fineTune = 0;
		gain = 0;
		lowNote = 0;
		highNote = 127;
		lowVelocity = 0;
		highVelocity = 127;
	}

	@Override
	public int size() {
		return 7;
	}

	public byte getUnshiftedNote() {
		return unshiftedNote;
	}

	public void setUnshiftedNote(byte unshiftedNote) {
		this.unshiftedNote = unshiftedNote;
	}

	public byte getFineTune() {
		return fineTune;
	}

	public void setFineTune(byte fineTune) {
		this.fineTune = fineTune;
	}

	public byte getGain() {
		return gain;
	}

	public void setGain(byte gain) {
		this.gain = gain;
	}

	public byte getLowNote() {
		return lowNote;
	}

	public void setLowNote(byte lowNote) {
		this.lowNote = lowNote;
	}

	public byte getHighNote() {
		return highNote;
	}

	public void setHighNote(byte highNote) {
		this.highNote = highNote;
	}

	public byte getLowVelocity() {
		return lowVelocity;
	}

	public void setLowVelocity(byte lowVelocity) {
		this.lowVelocity = lowVelocity;
	}

	public byte getHighVelocity() {
		return highVelocity;
	}

	public void setHighVelocity(byte highVelocity) {
		this.highVelocity = highVelocity;
	}

	@Override
	protected void writeData(WordOutputStream out) throws IOException {
		out.write8bit(unshiftedNote);
		out.write8bit(fineTune);
		out.write8bit(gain);
		out.write8bit(lowNote);
		out.write8bit(highNote);
		out.write8bit(lowVelocity);
		out.write8bit(highVelocity);
	}

	@Override
	protected void readData(WordInputStream in, int size) throws IOException {
		unshiftedNote = in.read8bit();
		fineTune = in.read8bit();
		gain = in.read8bit();
		lowNote = in.read8bit();
		highNote = in.read8bit();
		lowVelocity = in.read8bit();
		highVelocity = in.read8bit();
		if(size > size()) {
			in.skip(size - size());
		}
	}
}
