package de;

import java.awt.Graphics;

import gameengine.GameBase;

public class Main extends GameBase {

	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 900;

	private static final double GENERATION_SPEED = 0.25;
	private float currentGenerationTimer;

	private Grid grid;
	private boolean gridChanged;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.start("Conways Game Of Life", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void init() {
		int size = 10;
		
		grid = new Grid((int) ((SCREEN_WIDTH * 0.9) / size), (int) ((SCREEN_HEIGHT * 1) / size), size);
		grid.setAllRandom();
		gridChanged = true;
	}


	@Override
	public void update(double tslf) {
		currentGenerationTimer += tslf;

		if (currentGenerationTimer >= GENERATION_SPEED) {
			currentGenerationTimer -= GENERATION_SPEED;

			grid.nextGeneration();
			gridChanged = true;
		}
	}

	@Override
	public void draw(Graphics graphics) {
		if (gridChanged == true) {
			graphics.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

			grid.draw(graphics);
			gridChanged = false;
		}
	}

}
