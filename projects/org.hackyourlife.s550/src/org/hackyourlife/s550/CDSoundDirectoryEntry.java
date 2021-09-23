package org.hackyourlife.s550;

import java.io.IOException;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

/*
 * struct dirent {
 *     u8    name[32];
 *     u8    shortname[16];
 *     u32   offset; // multiply by 0x200
 *     u32   size; // multiply by 0x200
 *     u8    unknown;
 *     u8    group;
 *     u8    pad[6];
 * };
 */
public class CDSoundDirectoryEntry extends Struct {
	public static final int SIZE = 64;

	private final RawString name = new RawString(32);
	private final RawString shortname = new RawString(16);
	private int offset;
	private int size;
	private byte group;

	public String getName() {
		return name.get();
	}

	public String getShortName() {
		return shortname.get();
	}

	public int getOffset() {
		return offset;
	}

	public int getSize() {
		return size;
	}

	public byte getGroup() {
		return group;
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		name.read(in);
		shortname.read(in);
		offset = in.read32bit();
		size = in.read32bit();
		in.skip(1);
		group = in.read8bit();
		in.skip(6);
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		name.write(out);
		shortname.write(out);
		out.write32bit(offset);
		out.write32bit(size);
		out.write(0x41);
		out.write(group);
		byte[] pad = { -1, -1, -1, -1, -1, -1 };
		out.write(pad);
	}

	@Override
	public String toString() {
		return "SoundEntry[name=\"" + getName() + "\",shortname=\"" + getShortName() + "\",offset=" +
				getOffset() + ",size=" + getSize() + ",group=" + getGroup() + "]";
	}
}
