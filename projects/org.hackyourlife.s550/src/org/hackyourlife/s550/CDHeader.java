package org.hackyourlife.s550;

import java.io.IOException;
import java.util.Arrays;

import org.hackyourlife.s550.io.BEInputStream;
import org.hackyourlife.s550.io.RandomAccessMemoryInputStream;
import org.hackyourlife.s550.io.WordInputStream;

public class CDHeader {
	public static final byte[] MAGIC_DISC = { '*', ' ', 'R', 'O', 'L', 'A', 'N', 'D', ' ', 'S', '-', '5', '5', '0',
			' ', '*', -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	public static final byte[] MAGIC_INSTRUMENT_GROUP = { 'I', 'n', 's', 't', 'r', 'u', 'm', 'e', 'n', 't', ' ',
			'G', 'r', 'o', 'u', 'p' };
	public static final byte[] MAGIC_SOUND_DIRECTORY = { 'S', 'o', 'u', 'n', 'd', ' ', 'D', 'i', 'r', 'e', 'c', 't',
			'o', 'r', 'y', ' ' };
	public static final byte[] MAGIC_INSTRUMENT_MAP = { 'm', 'a', 'p', '1', ' ', 'I', 'n', 's', 't', 'r', 'u', 'm',
			'e', 'n', 't', ' ' };

	private final RawString[] info;
	private final CDSectionHeader instrumentGroup = new CDSectionHeader();
	private final CDSectionHeader soundDirectory = new CDSectionHeader();
	private final CDSectionHeader map1Instrument = new CDSectionHeader();
	private final CDSoundDirectory soundDirectoryData = new CDSoundDirectory();

	public CDHeader() {
		info = new RawString[7];
		for(int i = 0; i < info.length; i++) {
			info[i] = new RawString(32);
		}
	}

	public String[] getInfo() {
		String[] result = new String[info.length];
		for(int i = 0; i < info.length; i++) {
			result[i] = info[i].get();
		}
		return result;
	}

	public CDSoundDirectory getSoundDirectory() {
		return soundDirectoryData;
	}

	public void read(RandomAccessMemoryInputStream in) throws IOException {
		in.seek(0);
		WordInputStream win = new BEInputStream(in);
		byte[] magic = new byte[32];
		in.read(magic);
		if(!Arrays.equals(magic, MAGIC_DISC)) {
			throw new IOException("Invalid disc magic");
		}
		for(int i = 0; i < info.length; i++) {
			info[i].read(win);
		}

		instrumentGroup.read(win);
		soundDirectory.read(win);
		map1Instrument.read(win);

		in.seek(soundDirectory.getOffset() * CDImage.SECTOR_SIZE);
		soundDirectoryData.read(win, soundDirectory.getSize());
	}

	@Override
	public String toString() {
		return String.format("CD[igrp=0x%x,0x%x;sd=0x%x,0x%x;map1=0x%x,0x%x]", instrumentGroup.getOffset(),
				instrumentGroup.getSize(), soundDirectory.getOffset(), soundDirectory.getSize(),
				map1Instrument.getOffset(), map1Instrument.getSize());
	}
}
