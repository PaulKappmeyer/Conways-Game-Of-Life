package de;

import java.awt.Color;

public enum Cellstate {
	DEAD(0, new Color(10, 140, 250)),
	ALIVE(1, new Color(250, 240, 70));

	private int value;
	private Color color;

	private Cellstate(int value, Color color) {
		
		this.value = value;
		this.color = color;
	}

	public int getValue() {
		return value;
	}
	
	public Color getColor() {
		return color;
	}
}
