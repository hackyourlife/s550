package org.hackyourlife.s550;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class CDSoundDirectory {
	public static final byte[] EMPTY = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	private final List<CDSoundDirectoryEntry> entries = new ArrayList<>();

	public int getCount() {
		return entries.size();
	}

	public int getSize() {
		int bytes = entries.size() * CDSoundDirectoryEntry.SIZE;
		int sectors = bytes / CDImage.SECTOR_SIZE;
		if(bytes % CDImage.SECTOR_SIZE != 0) {
			sectors++;
		}
		return sectors;
	}

	public CDSoundDirectoryEntry get(int i) {
		return entries.get(i);
	}

	public void add(CDSoundDirectoryEntry entry) {
		entries.add(entry);
	}

	public void remove(int i) {
		entries.remove(i);
	}

	public void read(WordInputStream in, int size) throws IOException {
		int count = size * CDImage.SECTOR_SIZE / CDSoundDirectoryEntry.SIZE;
		for(int i = 0; i < count; i++) {
			CDSoundDirectoryEntry entry = new CDSoundDirectoryEntry();
			entry.read(in);
			if(entry.getName().charAt(0) == 0xFF) {
				continue;
			} else {
				entries.add(entry);
			}
		}
	}

	public void write(WordOutputStream out) throws IOException {
		int cnt = entries.size();
		int count = getSize() * CDImage.SECTOR_SIZE / CDSoundDirectoryEntry.SIZE;
		int i;
		for(i = 0; i < cnt; i++) {
			entries.get(i).write(out);
		}
		for(; i < count; i++) {
			out.write(EMPTY);
		}
	}
}
