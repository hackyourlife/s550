package org.hackyourlife.s550.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import org.hackyourlife.s550.util.StringUtils;

public abstract class NameFieldKeyHandler extends KeyAdapter {
	private final JTextField textfield;
	private final int length;

	protected NameFieldKeyHandler(JTextField textfield, int length) {
		this.textfield = textfield;
		this.length = length;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_HOME:
		case KeyEvent.VK_END:
			return;
		}

		String value = textfield.getText();
		if(value.length() > length) {
			value = value.substring(0, length);
		} else if(value.length() < length) {
			value = StringUtils.pad(value, length);
		}

		int caret = textfield.getCaretPosition();
		if(caret > value.length()) {
			caret = value.length();
		}
		textfield.setText(value);
		textfield.setCaretPosition(caret);

		handle(value);
	}

	public abstract void handle(String value);
}
