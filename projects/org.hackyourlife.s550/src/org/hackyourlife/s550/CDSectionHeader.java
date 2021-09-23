package org.hackyourlife.s550;

import java.io.IOException;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class CDSectionHeader extends Struct {
	private final RawString name = new RawString(16);
	private int offset;
	private int size;

	public int getOffset() {
		return offset;
	}

	public int getSize() {
		return size;
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		name.read(in);
		offset = in.read32bit();
		size = in.read32bit();
		in.skip(8);
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		name.write(out);
		out.write32bit(offset);
		out.write32bit(size);
		byte[] unknown = new byte[8];
		out.write(unknown);
	}
}
