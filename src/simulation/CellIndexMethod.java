package simulation;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Cell;
import model.Matrix;
import model.Particle;

public class CellIndexMethod {

	public static void calculateDistance(int sideLength, int M, double rc, List<Particle> particles,
			boolean edgeCondition) {
		double maxRadius = 0;
		for (Particle particle : particles) {
			maxRadius = particle.getRadius() > maxRadius ? particle.getRadius() : maxRadius;
		}
		double aux = (sideLength * 1.0) / (M * 1.0);
		if (aux <= rc + 2 * maxRadius) {
			throw new IllegalArgumentException("Invalid M");
		}
		
		Matrix m = new Matrix(sideLength, M, particles);

		for (Particle particle : particles) {
			int row = (int) Math.floor(particle.getPosition().getX() * M / sideLength);
			int col = (int) Math.floor(particle.getPosition().getY() * M / sideLength);

			List<Point> indexes = CellIndexMethod.calculateCellNeighbors(row, col, M, edgeCondition);

			for (Point index : indexes) {
				Cell cell = m.getCell(index.x, index.y);
				if (cell == null && edgeCondition && (index.x == M || index.y == -1 || index.y == M)) {
					index.x = index.x == M ? 0 : index.x;
					index.y = index.y == M ? 0 : (index.y == -1 ? M - 1 : index.y);
					cell = m.getCell(index.x, index.y);
				}
				if (cell != null) {
					Set<Particle> cellParticles = cell.getParticles();

					for (Particle cellParticle : cellParticles) {

						double xDistance = Math.abs(cellParticle.getPosition().getX() - particle.getPosition().getX());
						xDistance = edgeCondition &&
								xDistance > 2 * sideLength / M ? xDistance - sideLength : xDistance;

						double yDistance = cellParticle.getPosition().getY() - particle.getPosition().getY();
						yDistance = edgeCondition &&
								Math.abs(yDistance) > 2 * sideLength / M
								? (yDistance > 0 ? Math.abs(yDistance - sideLength) : Math.abs(yDistance + sideLength))
								: Math.abs(yDistance);

						double distance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2))
								- cellParticle.getRadius() - particle.getRadius();

						if (cellParticle.getId() != particle.getId() && distance < rc) {
							particle.getNeighbors().add(cellParticle);
							if (row != index.x || col != index.y) {
								cellParticle.getNeighbors().add(particle);
							}
						}
					}
				}
			}
		}
	}

	private static List<Point> calculateCellNeighbors(int row, int col, int M, boolean edgeCondition) {
		List<Point> indexes = new ArrayList<Point>();
		// cell
		indexes.add(new Point(row, col));
		// upper
		indexes.add(new Point(row - 1, col));
		// upper right
		indexes.add(new Point(row - 1, col + 1));
		// right
		indexes.add(new Point(row, col + 1));
		// lower right
		indexes.add(new Point(row + 1, col + 1));

		return indexes;
	}
}
