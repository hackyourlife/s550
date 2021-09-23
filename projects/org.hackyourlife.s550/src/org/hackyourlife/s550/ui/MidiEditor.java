package org.hackyourlife.s550.ui;

import javax.swing.JPanel;

import org.hackyourlife.s550.FloppyDisk;

@SuppressWarnings("serial")
public class MidiEditor extends JPanel {
	private FloppyDisk disk;

	public MidiEditor(FloppyDisk disk) {
		this.disk = disk;
	}

	public void setDisk(FloppyDisk disk) {
		this.disk = disk;
		update();
	}

	public void update() {
		// nothing
	}
}
