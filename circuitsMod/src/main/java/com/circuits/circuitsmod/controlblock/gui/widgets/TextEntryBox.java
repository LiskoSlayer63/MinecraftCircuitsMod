package com.circuits.circuitsmod.controlblock.gui.widgets;

import java.util.Optional;

import com.circuits.circuitsmod.controlblock.gui.ControlGui;
import com.circuits.circuitsmod.controlblock.gui.ControlGuiPage;


public class TextEntryBox extends UIElement implements UIFocusable {
	
	protected boolean hasFocus = false;
	protected String text = "";
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public TextEntryBox(ControlGui parent, int x, int y, int width, int height, String init) {
		super(parent, x, y, width, height);
		this.text = init;
	}
	public TextEntryBox(ControlGui parent, int x, int y, int width, int height) {
		this(parent, x, y, width, height, "");
	}
	
	public void draw() {
		parent.getFontRenderer().drawString(text, x, y, ControlGuiPage.elementColor);
		parent.drawBox(x, y, width, height);
		if (hasFocus) {
			parent.drawBox(x - 1, y - 1, width + 2, height + 2);
		}
	}
	
	public void requestFocus() {
		hasFocus = true;
	}
	public void unFocus() {
		hasFocus = false;
	}
	
	public boolean hasFocus() {
		return hasFocus;
	}
	
	@Override
	public boolean handleClick(int mouseX, int mouseY) {
		hasFocus = isClickIn(mouseX, mouseY);
		return hasFocus;
	}
	@Override
	public void handleKey(char typed, int keyCode) {
		if (!hasFocus) { return; }
		if (keyCode == 14) {
			//Backspace pressed
			text = text.substring(0, Math.max(text.length() - 1, 0));
		}
		else {
			text = text.concat("" + typed);
		}
		//System.out.println(keyCode);
	}
	
	public static class IntEntryBox extends TextEntryBox {

		public IntEntryBox(ControlGui parent, int x, int y, int width,
				int height, int defaultVal) {
			super(parent, x, y, width, height, Integer.toString(defaultVal));
		}
		public Optional<Integer> getValue() {
			try {
				return Optional.of(Integer.parseInt(this.text));
			}
			catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		public void setValue(int val) {
			this.text = "" + val;
		}
		
	}
	
}