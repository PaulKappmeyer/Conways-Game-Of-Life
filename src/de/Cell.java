package de;

import java.awt.Graphics;
import java.util.Random;

public class Cell {

	private static final Random random = new Random();
	
	private Cellstate state;

	public Cell(Cellstate state) {
		this.state = state;
	}

	public void draw(Graphics graphics, int xPos, int yPos, int size) {
		graphics.setColor(state.getColor());
		graphics.fillRect(xPos, yPos, size, size);
	}

	public void setStateRandom() {
		double value = random.nextDouble();
		if (value <= 0.2) {
			setState(Cellstate.ALIVE);
		} else {
			setState(Cellstate.DEAD);
		}
	}

	public void setState(Cellstate state) {
		this.state = state;
	}

	public Cellstate getState() {
		return state;
	}
}
