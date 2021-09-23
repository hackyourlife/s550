package org.hackyourlife.s550.test;

import static org.junit.Assert.assertEquals;

import org.hackyourlife.s550.DiskLabel;
import org.junit.Test;

public class DiskLabelTest {
	private static final String ENCODED_TWOVIOLINS = "Two Violins CC  LO  UP  BY   R  5I  0G   H  1T  9   8   9   ";
	private static final String DECODED_TWOVIOLINS = "Two Violins CLUB 50 1989COPYRIGHT                           ";

	private static final String ENCODED_RSB508_10 = "Rainy Day   R& Ra  SiLSBnit-,go5Thr0htm8un  ni  dn  eg  r   ";
	private static final String DECODED_RSB508_10 = "Rainy Day   Rain,Thunder& Lightning   Storm     RSB-508     ";

	private static void check(String encoded, String decoded) {
		String decode = DiskLabel.decode(encoded);
		String encode = DiskLabel.encode(decoded);
		assertEquals(decoded, decode);
		assertEquals(encoded, encode);
	}

	@Test
	public void twoviolins() {
		check(ENCODED_TWOVIOLINS, DECODED_TWOVIOLINS);
	}

	@Test
	public void rsb508_10() {
		check(ENCODED_RSB508_10, DECODED_RSB508_10);
	}
}
