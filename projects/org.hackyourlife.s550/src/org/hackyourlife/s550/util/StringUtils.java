package org.hackyourlife.s550.util;

public class StringUtils {
	public static String repeat(String s, int count) {
		StringBuffer result = new StringBuffer(count * s.length());
		for(int i = 0; i < count; i++) {
			result.append(s);
		}
		return result.toString();
	}

	public static String pad(String s, int width) {
		return pad(s, width, true);
	}

	public static String rpad(String s, int width) {
		return pad(s, width, false);
	}

	public static String pad(String s, int width, boolean first) {
		if(s.length() > width) {
			if(first) {
				if(width <= 3) {
					return s.substring(0, width);
				}
				return s.substring(0, width - 3) + "...";
			} else {
				if(width <= 3) {
					return s.substring(s.length() - width);
				}
				return "..." + s.substring(s.length() - width + 3);
			}
		} else {
			return s + repeat(" ", width - s.length());
		}
	}
}
