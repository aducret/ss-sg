package model;

import java.awt.Point;

public class CellWrapper {
	private Cell cell;
	private int xOffset;
	private int yOffset;
	
	public CellWrapper(Cell cell, int xOffset, int yOffset) {
		this.cell = cell;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public Cell getCell() {
		return cell;
	}

	public int getxOffset() {
		return xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}
}
