package org.hackyourlife.s550;

public class DiskLabel {
	public static String encode(String decoded) {
		if(decoded.length() != 60) {
			throw new IllegalArgumentException("invalid length");
		}

		char[] input = decoded.toCharArray();
		char[] result = new char[60];

		for(int i = 0; i < 12; i++) {
			result[i] = input[i];
			result[12 + 4 * i] = input[12 + i];
			result[13 + 4 * i] = input[24 + i];
			result[14 + 4 * i] = input[36 + i];
			result[15 + 4 * i] = input[48 + i];
		}

		return new String(result);
	}

	public static String decode(String encoded) {
		if(encoded.length() != 60) {
			throw new IllegalArgumentException("invalid length");
		}

		char[] input = encoded.toCharArray();
		char[] result = new char[60];

		for(int i = 0; i < 12; i++) {
			result[i] = input[i];
			result[12 + i] = input[12 + 4 * i];
			result[24 + i] = input[13 + 4 * i];
			result[36 + i] = input[14 + 4 * i];
			result[48 + i] = input[15 + 4 * i];
		}

		return new String(result);
	}
}
