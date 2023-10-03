package de;

import java.awt.Color;
import java.awt.Graphics;

public class Grid {

	private int numCols;
	private int numRows;
	private int cellsize;
	private Cell[][] cells;

	private int numGenerations;

	private boolean showGridlines = true;

	public Grid(int numCols, int numRows, int cellsize) {
		this.numCols = numCols;
		this.numRows = numRows;
		this.cellsize = cellsize;
		cells = new Cell[numCols][numRows];
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {			
				cells[x][y] = new Cell(x * cellsize, y * cellsize);
			}
		}
	}

	public void draw(Graphics graphics) {
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].draw(graphics, cellsize);
			}	
		}

		// grid
		if (showGridlines) {
			graphics.setColor(Color.LIGHT_GRAY);
			for (int x = 0; x <= numCols; x++) {
				graphics.drawLine(x*cellsize, 0, x*cellsize, numRows*cellsize);
			}
			for (int y = 0; y <= numRows; y++) { 
				graphics.drawLine(0, y*cellsize, numCols*cellsize, y*cellsize);
			}
		}
	}

	public void toggleGridlines() {
		showGridlines = !showGridlines;
	}
	
	public void toggleState(int xInd, int yInd) {
		if (xInd < 0 || xInd >= numCols || yInd < 0 || yInd >= numRows) {
			return;
		}
		
		cells[xInd][yInd].setState(cells[xInd][yInd].getState().toggle());
		
		numGenerations = 0;
	}
	
	public void setState(int xInd, int yInd, Cellstate state) {
		if (xInd < 0 || xInd >= numCols || yInd < 0 || yInd >= numRows) {
			return;
		}
		cells[xInd][yInd].setState(state);

		numGenerations = 0;
	}
	
	public Cellstate getState(int xInd, int yInd) {
		if (xInd < 0 || xInd >= numCols || yInd < 0 || yInd >= numRows) {
			return null;
		}
		return cells[xInd][yInd].getState();
	}
	
	public void setAllRandom() {
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].setStateRandom();
			}	
		}
		numGenerations = 0;
	}

	public void setAll(Cellstate state) {
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].setState(state);
			}
		}

		numGenerations = 0;
	}

	public void nextGeneration() {
		Cellstate[][] states = calculateNextGeneration(cells);
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				cells[x][y].setState(states[x][y]);
			}
		}
		numGenerations++;
	}

	public static Cellstate[][] calculateNextGeneration(Cell[][] grid){
		// calculate next generation
		int numCols = grid.length;
		int numRows = grid[0].length;
		Cellstate[][] next = new Cellstate[numCols][numRows];

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
				next[x][y] = Cellstate.DEAD;

				// apply rules for alive
				switch (grid[x][y].getState()) {
				case DEAD:
					if (numAliveNeighbours == 3) {
						next[x][y] = Cellstate.ALIVE;
					}
					break;

				case ALIVE:
					if (numAliveNeighbours == 2 || numAliveNeighbours == 3) {
						next[x][y] = Cellstate.ALIVE;
					}
					break;
				}
			}
		}
		return next;
	}
	
	public int getGeneration() {
		return numGenerations;
	}
	
	public void changeCellsize(int amount) {
		cellsize += amount;
		if (cellsize < 1) {
			cellsize = 1;
		}
		
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {			
				cells[x][y].updatePos(x * cellsize, y * cellsize);
			}
		}
	}
	
	public int getCellsize() {
		return cellsize;
	}

}