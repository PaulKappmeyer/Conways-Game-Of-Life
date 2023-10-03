package de;

public enum Cellstate {
	DEAD(0),
	ALIVE(1);

	private int value;    

	private Cellstate(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
