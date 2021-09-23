package org.hackyourlife.s550;

import java.io.IOException;

import org.hackyourlife.s550.io.WordInputStream;
import org.hackyourlife.s550.io.WordOutputStream;

public class RawString extends Struct {
	private final byte[] value;

	public RawString(int size) {
		value = new byte[size];
	}

	public void set(String s) {
		if(s.length() <= value.length) {
			int i;
			for(i = 0; i < s.length(); i++) {
				value[i] = (byte) s.charAt(i);
			}
			for(; i < value.length; i++) {
				value[i] = ' ';
			}
		} else {
			throw new IllegalArgumentException("String too long");
		}
	}

	public String get() {
		char[] chars = new char[value.length];
		for(int i = 0; i < value.length; i++) {
			chars[i] = (char) (value[i] & 0xFF);
		}
		return new String(chars);
	}

	@Override
	public void read(WordInputStream in) throws IOException {
		in.read(value);
	}

	@Override
	public void write(WordOutputStream out) throws IOException {
		out.write(value);
	}

	@Override
	public String toString() {
		return "[" + get() + "]";
	}
}
