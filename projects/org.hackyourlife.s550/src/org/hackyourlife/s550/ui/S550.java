package org.hackyourlife.s550.ui;

import org.hackyourlife.s550.PatchParameter;
import org.hackyourlife.s550.ToneParameter;

public class S550 {
	public static String getPatchString(byte patchId, PatchParameter patch) {
		return "I" + (patchId / 8 + 1) + (patchId % 8 + 1) + " " + patch.getName();
	}

	public static int getPatchNumber(byte tone) {
		return (tone / 8 + 1) * 10 + (tone % 8 + 1);
	}

	public static byte parsePatchNumber(int val) {
		if(val < 11 || val > 28) {
			throw new IllegalArgumentException("invalid patch number");
		}
		int a = val / 10;
		int b = val % 10;
		if(a < 1 || a > 2 || b < 1 || b > 8) {
			throw new IllegalArgumentException("invalid patch number");
		}
		a--;
		b--;
		return (byte) (a * 8 + b);
	}

	public static String getToneString(byte toneId, ToneParameter tone) {
		return "x" + (toneId / 8 + 1) + (toneId % 8 + 1) + " " + tone.getName();
	}

	public static int getToneNumber(byte tone) {
		return (tone / 8 + 1) * 10 + (tone % 8 + 1);
	}

	public static byte parseToneNumber(int val) {
		if(val < 11 || val > 48) {
			throw new IllegalArgumentException("invalid tone number");
		}
		int a = val / 10;
		int b = val % 10;
		if(a < 1 || a > 4 || b < 1 || b > 8) {
			throw new IllegalArgumentException("invalid tone number");
		}
		a--;
		b--;
		return (byte) (a * 8 + b);
	}
}
