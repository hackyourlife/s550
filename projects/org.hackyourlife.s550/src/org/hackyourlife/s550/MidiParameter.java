package org.hackyourlife.s550;

import java.io.IOException;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class MidiParameter extends Struct {
	// S-50 only stuff
	private byte txChannel;
	private byte txProgramChange;
	private byte txBender;
	private byte txModulation;
	private byte txHold;
	private byte txAfterTouch;
	private byte txVolume;
	private final byte[] rxProgramNumber = new byte[8];
	private final byte[] txProgramNumber = new byte[8];
	private byte txBendRange;

	// S-550 stuff
	private final byte[] rxChannel = new byte[8];
	private final byte[] rxProgramChange = new byte[8];
	private final byte[] rxBender = new byte[8];
	private final byte[] rxModulation = new byte[8];
	private final byte[] rxHold = new byte[8];
	private final byte[] rxAfterTouch = new byte[8];
	private final byte[] rxVolume = new byte[8];
	private final byte[] rxBendRange = new byte[8];
	private byte systemExclusive;
	private byte deviceId;
	private final byte[] rxProgramChangeNumber = new byte[32];

	public byte[] getRxChannel() {
		return getArray(rxChannel);
	}

	public void setRxChannel(byte[] rxChannel) {
		setArray(rxChannel, this.rxChannel);
	}

	public byte[] getRxProgramChange() {
		return getArray(rxProgramChange);
	}

	public void setRxProgramChange(byte[] rxProgramChange) {
		setArray(rxProgramChange, this.rxProgramChange);
	}

	public byte[] getRxBender() {
		return getArray(rxBender);
	}

	public void setRxBender(byte[] rxBender) {
		setArray(rxBender, this.rxBender);
	}

	public byte[] getRxModulation() {
		return getArray(rxModulation);
	}

	public void setRxModulation(byte[] rxModulation) {
		setArray(rxModulation, this.rxModulation);
	}

	public byte[] getRxHold() {
		return getArray(rxHold);
	}

	public void setRxHold(byte[] rxHold) {
		setArray(rxHold, this.rxHold);
	}

	public byte[] getRxAfterTouch() {
		return getArray(rxAfterTouch);
	}

	public void setRxAfterTouch(byte[] rxAfterTouch) {
		setArray(rxAfterTouch, this.rxAfterTouch);
	}

	public byte[] getRxVolume() {
		return getArray(rxVolume);
	}

	public void setRxVolume(byte[] rxVolume) {
		setArray(rxVolume, this.rxVolume);
	}

	public byte[] getRxBendRange() {
		return getArray(rxBendRange);
	}

	public void setRxBendRange(byte[] rxBendRange) {
		setArray(rxBendRange, this.rxBendRange);
	}

	public byte getSystemExclusive() {
		return systemExclusive;
	}

	public void setSystemExclusive(byte systemExclusive) {
		this.systemExclusive = systemExclusive;
	}

	public byte getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(byte deviceId) {
		this.deviceId = deviceId;
	}

	public byte[] getRxProgramChangeNumber() {
		return getArray(rxProgramChangeNumber);
	}

	public void setRxProgramChangeNumber(byte[] rxProgramChangeNumber) {
		setArray(rxProgramChangeNumber, this.rxProgramChangeNumber);
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		// S-50 leftover
		in.skip(8);
		txChannel = in.read8bit();
		txProgramChange = in.read8bit();
		txBender = in.read8bit();
		txModulation = in.read8bit();
		txHold = in.read8bit();
		txAfterTouch = in.read8bit();
		txVolume = in.read8bit();
		in.skip(1);
		in.read(rxProgramNumber);
		in.read(txProgramNumber);

		// S-550 stuff
		in.read(rxChannel);
		in.read(rxProgramChange);
		in.read(rxBender);
		in.read(rxModulation);
		in.read(rxHold);
		in.read(rxAfterTouch);
		in.read(rxVolume);
		in.read(rxBendRange);
		txBendRange = in.read8bit();
		systemExclusive = in.read8bit();
		deviceId = in.read8bit();
		in.skip(1);
		in.read(rxProgramChangeNumber);
		in.skip(124);
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		out.write((byte) 0, 8);
		out.write8bit(txChannel);
		out.write8bit(txProgramChange);
		out.write8bit(txBender);
		out.write8bit(txModulation);
		out.write8bit(txHold);
		out.write8bit(txAfterTouch);
		out.write8bit(txVolume);
		out.write8bit((byte) 0);
		out.write(rxProgramNumber);
		out.write(txProgramNumber);

		out.write(rxChannel);
		out.write(rxProgramChange);
		out.write(rxBender);
		out.write(rxModulation);
		out.write(rxHold);
		out.write(rxAfterTouch);
		out.write(rxVolume);
		out.write(rxBendRange);
		out.write8bit(txBendRange);
		out.write8bit(systemExclusive);
		out.write8bit(deviceId);
		out.write8bit((byte) 0);
		out.write(rxProgramChangeNumber);
		out.write((byte) 0, 124);
	}
}
