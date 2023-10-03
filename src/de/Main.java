package de;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import gameengine.GameBase;

public class Main extends GameBase implements KeyListener, MouseListener, MouseMotionListener {

	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 900;

	private static final double GENERATION_SPEED = 0.25;
	private static float currentGenerationTimer;

	public static Grid grid;

	private static boolean running;
	private static boolean redraw;

	private static final String INFO_TEXT = "Press 'Space' to run/stop simulation. Press 'G' to toggle the gridlines";
	
	private static Cellstate draggState;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.start("Conways Game Of Life", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void init() {
		int size = 10;

		grid = new Grid((int) (SCREEN_WIDTH / size), (int) ((SCREEN_HEIGHT * 0.9) / size), size);
		grid.setAllRandom();
		redraw();

		// Adding inputManagers to window
		window.addKeyListener(this);
		window.addMouseListener(this);
		window.addMouseMotionListener(this);
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
			redraw();
		}
	}

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

	public static void redraw() {
		redraw = true;
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
			redraw();
			break;
			
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (running) {
			toggleRunning();
		} else {
			int mouseX = e.getX() - window.getInsetX();
			int mouseY = e.getY() - window.getInsetY();
			int xInd = mouseX / grid.getSize();
			int yInd = mouseY / grid.getSize();
			
			grid.toggleState(xInd, yInd);
			redraw();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (running) {
			return;
		}
		int mouseX = e.getX() - window.getInsetX();
		int mouseY = e.getY() - window.getInsetY();
		int xInd = mouseX / grid.getSize();
		int yInd = mouseY / grid.getSize();
	
		draggState = grid.getState(xInd, yInd).toggle();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (running || draggState == null) {
			return;
		}
		
		int mouseX = e.getX() - window.getInsetX();
		int mouseY = e.getY() - window.getInsetY();
		int xInd = mouseX / grid.getSize();
		int yInd = mouseY / grid.getSize();
		
		grid.setState(xInd, yInd, draggState);
		redraw();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
