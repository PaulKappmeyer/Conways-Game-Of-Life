package de;

import java.awt.Graphics;
import java.util.Random;

public class Cell {

	private static final Random random = new Random();
	
	private int xPos;
	private int yPos;
	private Cellstate state;

	public Cell(int xPos, int yPos, Cellstate state) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.state = state;
	}

	public Cell(int xPos, int yPos) {
		this(xPos, yPos, Cellstate.DEAD);
	}

	public void draw(Graphics graphics, int size) {
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
	
	public void updatePos(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

}
