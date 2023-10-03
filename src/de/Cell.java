package de;

import java.awt.Graphics;
import java.util.Random;

public class Cell {

	private static final Random random = new Random();
	
	private int xPos;
	private int yPos;
	private int size;
	private Cellstate state;

	public Cell(int xPos, int yPos, int size, Cellstate state) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.size = size;
		this.state = state;
	}

	public Cell(int xPos, int yPos, int size) {
		this(xPos, yPos, size, Cellstate.DEAD);
	}

	public void draw(Graphics graphics) {
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