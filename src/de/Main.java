package de;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import gameengine.GameBase;

public class Main extends GameBase implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	// size of the window (in pixel)
	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 900;

	// size of part with the grid (in pixel)
	public static final int BOARD_WIDTH = 1200;
	public static final int BOARD_HEIGHT = 810;

	private static final double GENERATION_SPEED = 0.025;
	private static float currentGenerationTimer;

	public static Grid grid;

	private static boolean running;
	private static boolean redraw;

	private static final String INFO_TEXT_1 = "Press 'Space' to run/stop simulation. Press 'G' to toggle the gridlines. Press 'C' to clear the grid.";
	private static final String INFO_TEXT_2	= "Use the mouse wheel to zoom. Use the right mouse button to change the state of the cells. Use the left mouse button to move.";

	private static Cellstate draggState;
	private static int mousePressedX;
	private static int mousePressedY;

	public static void main(String[] args) {
		Main main = new Main();
		main.start("Conways Game Of Life", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void init() {
		grid = new Grid(100, 100, 10);
		grid.setAllRandom();
		redraw();

		// Adding inputManagers to window
		window.addKeyListener(this);
		window.addMouseListener(this);
		window.addMouseMotionListener(this);
		window.addMouseWheelListener(this);
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
		// grid background
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		
		// draw grid and cells
		grid.draw(graphics);

		
		// text background
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, BOARD_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		// draw text
		graphics.setColor(Color.BLACK);
		graphics.drawString("Generation: " + grid.getGeneration(), 10, (int) (SCREEN_HEIGHT * 0.915));
		graphics.drawString("Cellsize: " + grid.getCellsize(), 150, (int) (SCREEN_HEIGHT * 0.915));

		graphics.drawString(INFO_TEXT_1, 10, (int) (SCREEN_HEIGHT * 0.930));
		graphics.drawString(INFO_TEXT_2, 10, (int) (SCREEN_HEIGHT * 0.945));

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
			
		case KeyEvent.VK_C:
			if (running) {
				toggleRunning();
				return;
			}
			
			grid.setAll(Cellstate.DEAD);
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
			return;
		}
		
		int mouseX = e.getX() - window.getInsetX();
		int mouseY = e.getY() - window.getInsetY();
		int xInd = (mouseX - grid.getXOffset()) / grid.getCellsize();
		int yInd = (mouseY - grid.getYOffset()) / grid.getCellsize();
		
		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			grid.toggleState(xInd, yInd);
			break;
			
		case MouseEvent.BUTTON3:
			break;
		}
		
		redraw();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX() - window.getInsetX();
		int mouseY = e.getY() - window.getInsetY();
		int xInd = (mouseX - grid.getXOffset()) / grid.getCellsize();
		int yInd = (mouseY - grid.getYOffset()) / grid.getCellsize();

		draggState = null;
		if (SwingUtilities.isLeftMouseButton(e) && !running) {
			draggState = grid.getState(xInd, yInd).toggle();
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			mousePressedX = mouseX;
			mousePressedY = mouseY;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int mouseX = e.getX() - window.getInsetX();
		int mouseY = e.getY() - window.getInsetY();
		int xInd = (mouseX - grid.getXOffset()) / grid.getCellsize();
		int yInd = (mouseY - grid.getYOffset()) / grid.getCellsize();

		if (SwingUtilities.isLeftMouseButton(e) && draggState != null) {
			grid.setState(xInd, yInd, draggState);
		}
		if (SwingUtilities.isRightMouseButton(e)) {	
			grid.setXOffset(grid.getXOffset() + mouseX - mousePressedX);
			grid.setYOffset(grid.getYOffset() + mouseY - mousePressedY);
			
			mousePressedX = mouseX;
			mousePressedY = mouseY;
		}
		
		redraw();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		grid.setCellsize(grid.getCellsize() - e.getWheelRotation());
		redraw();
	}
}
