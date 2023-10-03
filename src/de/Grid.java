package de;

import java.awt.Color;
import java.awt.Graphics;

public class Grid {

	private int numCols;
	private int numRows;
	private Cell[][] grid;

	private int numGenerations;
	
	public Grid(int numCols, int numRows, int size) {
		this.numCols = numCols;
		this.numRows = numRows;
		grid = new Cell[numCols][numRows];
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {			
				grid[x][y] = new Cell(x * size, y * size, size);
			}
		}
	}

	public void draw(Graphics graphics) {
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				grid[x][y].draw(graphics);
			}	
		}
		
		graphics.setColor(Color.BLACK);
		graphics.drawString("Generation: " + numGenerations, (int) (Main.SCREEN_WIDTH * 0.92), 15);
	}
	
	public void setAllRandom() {
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				grid[x][y].setStateRandom();
			}	
		}
		numGenerations = 0;
	}

	public void setAll(Cellstate state) {
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				grid[x][y].setState(state);
			}
		}
		
		numGenerations = 0;
	}

	public void nextGeneration() {
		Cellstate[][] states = calculateNextGeneration(grid);
		for (int x = 0; x < numCols; x++) {
			for (int y = 0; y < numRows; y++) {
				grid[x][y].setState(states[x][y]);
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

}