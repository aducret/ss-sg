package model;
import java.util.List;

public class Matrix {

	private Cell[] matrix;
	private int M;

	public Matrix(int sideLength, int M, List<Particle> particles) {
		this.matrix = new Cell[M * M];
		this.M = M;

		for (Particle p : particles) {
			int row = (int) Math.floor(p.getPosition().getX() * M / sideLength);
			int col = (int) Math.floor(p.getPosition().getY() * M / sideLength);
			int cellArrayPosition = M * row + col;

			Cell cell = this.matrix[cellArrayPosition];
			if (cell != null) {
				cell.getParticles().add(p);
			} else {
				cell = new Cell();
				cell.getParticles().add(p);
				this.matrix[cellArrayPosition] = cell;
			}
		}
	}

	public Cell getCell(int x, int y) {
		int index = M * x + y;

		if (index < 0 || index >= M * M) {
			return null;
		}

		return this.matrix[index];
	}

}
