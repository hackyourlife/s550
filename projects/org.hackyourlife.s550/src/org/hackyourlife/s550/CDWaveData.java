package org.hackyourlife.s550;

import java.io.IOException;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class CDWaveData extends Struct {
	public static final int SAMPLES_PER_SEGMENT = 12288;

	private final byte[] data = new byte[SAMPLES_PER_SEGMENT * 2];

	public byte[] getData() {
		return data;
	}

	public short[] getSamples() {
		short[] result = new short[SAMPLES_PER_SEGMENT];
		for(int i = 0; i < result.length; i++) {
			result[i] = (short) (Byte.toUnsignedInt(data[i * 2]) |
					(Byte.toUnsignedInt(data[i * 2 + 1]) << 8));
		}
		return result;
	}

	public void setSamples(short[] samples) {
		if(samples.length != SAMPLES_PER_SEGMENT) {
			throw new IllegalArgumentException("invalid sample count");
		}
		for(int i = 0; i < samples.length; i++) {
			data[i * 2 + 0] = (byte) samples[i];
			data[i * 2 + 1] = (byte) (samples[i] >> 8);
		}
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		in.read(data);
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		out.write(data);
	}
}
