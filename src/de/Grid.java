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
		cells[xInd][yInd].setState(cells[xInd][yInd].getState().toggle());
		
		generations.clear();
		generations.add(cells);
	}
	
	public void setState(int xInd, int yInd, Cellstate state) {
		if (xInd < 0 || xInd >= numCols || yInd < 0 || yInd >= numRows) {
			return;
		}
		cells[xInd][yInd].setState(state);
		
		generations.clear();
		generations.add(cells);
	}
	
	public void setAllRandom() {
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].setStateRandom();
			}	
		}
		
		generations.clear();
		generations.add(cells);
	}

	public void setAll(Cellstate state) {
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].setState(state);
			}
		}

		generations.clear();
		generations.add(cells);
	}
	
	public Cellstate getState(int xInd, int yInd) {
		if (xInd < 0 || xInd >= numCols || yInd < 0 || yInd >= numRows) {
			return null;
		}
		return cells[xInd][yInd].getState();
	}

	public void nextGeneration() {
		Cell[][] next = calculateNextGeneration(cells);
		cells = next;
		
		generations.add(next);
	}

	public void previousGeneration() {
		if (generations.size() < 2) {
			return;
		}
		
		generations.removeLast();
		cells = generations.getLast();
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
	
	public int getGeneration() {
		return generations.size();
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