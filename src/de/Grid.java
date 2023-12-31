package de;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Grid {
	
	private int numCols;
	private int numRows;
	private int cellsize;
	private Cell[][] cells;
	private LinkedList<Cell[][]> generations;
	private final int listCapacity = 100;
	private int numGenerations;
	private int numAliveCells;
	
	private boolean isStable;
	
	private boolean showGridlines = true;
	
	private int xOffset;
	private int yOffset;

	public Grid(int numCols, int numRows, int cellsize) {
		this.numCols = numCols;
		this.numRows = numRows;
		this.cellsize = cellsize;
		cells = new Cell[numCols][numRows];
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {			
				cells[x][y] = new Cell(Cellstate.DEAD);
			}
		}
		generations = new LinkedList<>();
		generations.add(cells);
		numGenerations = 1;
		numAliveCells = 0;
		
		// center board
		xOffset = (Main.BOARD_WIDTH - numCols * cellsize) / 2;
		yOffset = (Main.BOARD_HEIGHT - numRows * cellsize) / 2;
	}
	
	public void draw(Graphics graphics) {
		// save offset in local variable for thread safety consistency -> avoid flickering
		int xOffset = getXOffset();
		int yOffset = getYOffset();
		int cellsize = getCellsize();
		graphics.translate(xOffset, yOffset);
		
		// draw cells
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].draw(graphics, x * cellsize, y * cellsize, cellsize);
			}	
		}

		// draw gridlines
		if (showGridlines && cellsize >= 3) {
			graphics.setColor(Color.LIGHT_GRAY);
			for (int x = 0; x <= numCols; x++) {
				int colX = x*cellsize;
				graphics.drawLine(colX, 0, colX, numRows*cellsize);
			}
			for (int y = 0; y <= numRows; y++) { 
				int rowY = y*cellsize;
				graphics.drawLine(0, rowY, numCols*cellsize, rowY);
			}
		}
		
		// reset offset
		graphics.translate(-xOffset, -yOffset);
	}

	public void toggleGridlines() {
		showGridlines = !showGridlines;
	}
	
	public void toggleState(int xInd, int yInd) {
		if (xInd < 0 || xInd >= numCols || yInd < 0 || yInd >= numRows) {
			return;
		}
		setState(xInd, yInd, cells[xInd][yInd].getState().toggle());
	}
	
	public void setState(int xInd, int yInd, Cellstate state) {
		if (xInd < 0 || xInd >= numCols || yInd < 0 || yInd >= numRows) {
			return;
		}
		numAliveCells -= cells[xInd][yInd].getState().getValue();
		cells[xInd][yInd].setState(state);
		numAliveCells += cells[xInd][yInd].getState().getValue();
		
		generations.clear();
		generations.add(cells);
		numGenerations = 1;
		isStable = false;
	}
	
	public void setAllRandom() {
		numAliveCells = 0;
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].setStateRandom();
				numAliveCells += cells[x][y].getState().getValue();
			}	
		}
		
		generations.clear();
		generations.add(cells);
		numGenerations = 1;
		isStable = false;
	}

	public void setAll(Cellstate state) {
		switch(state) {
		case DEAD:
			numAliveCells = 0;
			break;
			
		case ALIVE:
			numAliveCells = numCols * numRows;
			break;
		}
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].setState(state);
			}
		}

		generations.clear();
		generations.add(cells);
		numGenerations = 1;
		isStable = false;
	}
	
	private void updateNumAlive() {
		numAliveCells = 0;
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				numAliveCells += cells[x][y].getState().getValue();
			}
		}
	}
	
	public Cellstate getState(int xInd, int yInd) {
		if (xInd < 0 || xInd >= numCols || yInd < 0 || yInd >= numRows) {
			return null;
		}
		return cells[xInd][yInd].getState();
	}

	public boolean isStable() {
		return isStable;
	}
	
	public void nextGeneration() {
		if (isStable) {
			return;
		}
		
		// calculate next generation
		Cell[][] next = calculateNextGeneration(cells);
		
		// check if stable configuration is reached
		isStable = true;
		outer: for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				if (cells[x][y].getState() != next[x][y].getState()) {
					isStable = false;
					break outer;
				}
			}
		}
		// if so break
		if (isStable) {
			return;
		}
		
		cells = next;
		
		generations.add(next);
		if (generations.size() > listCapacity) {
			generations.removeFirst();
		}
		numGenerations++;
		updateNumAlive();
	}

	public void previousGeneration() {
		if (generations.size() < 2) {
			return;
		}
		
		generations.removeLast();
		cells = generations.getLast();
		numGenerations--;
		updateNumAlive();
		isStable = false;
	}
	
	public static Cell[][] calculateNextGeneration(Cell[][] grid){
		// calculate next generation
		int numCols = grid.length;
		int numRows = grid[0].length;
		Cell[][] next = new Cell[numCols][numRows];

		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				int numAlive = 0;

				for (int xOff = -1; xOff <= 1; xOff++) {
					for (int yOff = -1; yOff <= 1; yOff++) {
						int xInd = (x + xOff + numCols) % numCols;
						int yInd = (y + yOff + numRows) % numRows;
						numAlive += grid[xInd][yInd].getState().getValue();
					}
				}
				int numAliveNeighbours = numAlive - grid[x][y].getState().getValue();

				// default value is dead
				next[x][y] = new Cell(Cellstate.DEAD);

				// apply rules for alive
				switch (grid[x][y].getState()) {
				case DEAD:
					if (numAliveNeighbours == 3) {
						next[x][y].setState(Cellstate.ALIVE);
					}
					break;

				case ALIVE:
					if (numAliveNeighbours == 2 || numAliveNeighbours == 3) {
						next[x][y].setState(Cellstate.ALIVE);
					}
					break;
				}
			}
		}
		return next;
	}
	
	public int getNumAliveCells() {
		return numAliveCells;
	}
	
	public int getGeneration() {
		return numGenerations;
	}
	
	public synchronized void setCellsize(int cellsize) {
		if (cellsize < 1) {
			return;
		}
		this.cellsize = cellsize;
	}
	
	public synchronized int getCellsize() {
		return cellsize;
	}
	
	public synchronized void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	
	public synchronized void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
	public synchronized int getXOffset() {
		return xOffset;
	}
	
	public synchronized int getYOffset() {
		return yOffset;
	}

}