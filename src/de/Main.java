package de;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import gameengine.GameBase;

public class Main extends GameBase implements KeyListener, MouseListener {

	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 900;

	private static final double GENERATION_SPEED = 0.25;
	private static float currentGenerationTimer;

	public static Grid grid;

	private static boolean running;
	private static boolean redraw;

	public static void main(String[] args) {
		Main main = new Main();
		main.start("Conways Game Of Life", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void init() {
		int size = 10;

		grid = new Grid((int) (SCREEN_WIDTH / size), (int) ((SCREEN_HEIGHT * 0.9) / size), size);
		grid.setAllRandom();
		redraw = true;
		
		// Adding inputManagers to window
		window.addKeyListener(this);
		window.addMouseListener(this);
	}


	@Override
	public void update(double tslf) {
		if (!running) {
			return;
		}

		currentGenerationTimer += tslf;

		if (currentGenerationTimer >= GENERATION_SPEED) {
			currentGenerationTimer -= GENERATION_SPEED;

			grid.nextGeneration();
			redraw = true;
		}
	}

	private static final String INFO_TEXT = "Press 'Space' to run/stop simulation. Press 'G' to toggle the gridlines";
	
	@Override
	public void draw(Graphics graphics) {
		if (!redraw == true) {
			return;
		}
		graphics.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		// draw grid and cells
		grid.draw(graphics);

		// draw text
		graphics.setColor(Color.BLACK);
		graphics.drawString("Generation: " + grid.getGeneration(), 10, (int) (SCREEN_HEIGHT * 0.915));
		
		graphics.drawString(INFO_TEXT, 10, (int) (SCREEN_HEIGHT * 0.930));
		
		
		redraw = false;
	}

	public static void toggleRunning() {
		running = !running;
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		switch (keyCode) {
		case KeyEvent.VK_SPACE:
			toggleRunning();
			break;
			
		case KeyEvent.VK_G:
			grid.toggleGridlines();
			redraw = true;
			break;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
}
