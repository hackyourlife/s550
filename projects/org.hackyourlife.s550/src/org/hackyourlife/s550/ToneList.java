package org.hackyourlife.s550;

import java.io.IOException;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

/*
 * struct ToneList {
 *     u8    name[8];
 *     u8    unknown1;
 *     u8    orgSubTone;
 *     u8    unknown2;
 *     u8    unknown3;
 *     u8    rootKey;
 *     u8    unknown4;
 *     u8    unknown5;
 *     u8    unknown6;
 * };
 */
public class ToneList extends Struct {
	private final RawString name = new RawString(8);
	private byte unknown1;
	private byte orgSubTone;
	private byte unknown2;
	private byte unknown3;
	private byte rootKey;
	private byte unknown4;
	private byte unknown5;
	private byte unknown6;

	public void setName(String name) {
		this.name.set(name);
	}

	public String getName() {
		return name.get();
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		name.read(in);
		unknown1 = in.read8bit();
		orgSubTone = in.read8bit();
		unknown2 = in.read8bit();
		unknown3 = in.read8bit();
		rootKey = in.read8bit();
		unknown4 = in.read8bit();
		unknown5 = in.read8bit();
		unknown6 = in.read8bit();
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		name.write(out);
		out.write8bit(unknown1);
		out.write8bit(orgSubTone);
		out.write8bit(unknown2);
		out.write8bit(unknown3);
		out.write8bit(rootKey);
		out.write8bit(unknown4);
		out.write8bit(unknown5);
		out.write8bit(unknown6);
	}

	@Override
	public String toString() {
		return "ToneList[" + getName() + "]";
	}
}
