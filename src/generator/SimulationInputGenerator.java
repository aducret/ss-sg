package generator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.Vector2;
import util.RandomUtils;

public class SimulationInputGenerator {
	private static final double PARTICLE_MASS = 0.01;
	private static final double DEFAULT_RADIUS = 0.8;

	public static void generateRandomInput(double L, double W, double D)
			throws FileNotFoundException, UnsupportedEncodingException {
		if (D >= W || W >= L) {
			throw new IllegalArgumentException("No se respetaron las cotas (L > W > D)");
		}

		PrintWriter dynamicWriter = new PrintWriter("doc/examples/Dynamic" + L + "-" + W + "-" + D + ".txt", "UTF-8");
		PrintWriter staticWriter = new PrintWriter("doc/examples/Static" + L + "-" + W + "-" + D + ".txt", "UTF-8");

		List<Vector2> positions = getPositions(L, W, (D / 10.0) / 2.0);

		staticWriter.println(positions.size());
		staticWriter.println(W + " " + (L + 1));
		staticWriter.println(L + " " + W + " " + D);

		for (Vector2 position : positions) {
			printParticleLine(staticWriter, dynamicWriter, position, PARTICLE_MASS, (D / 10.0) / 2.0);
		}

		staticWriter.close();
		dynamicWriter.close();
	}

	public static void generateRandomInput(double L, double W)
			throws FileNotFoundException, UnsupportedEncodingException {
		if (W >= L) {
			throw new IllegalArgumentException("No se respetaron las cotas (L > W > D)");
		}

		PrintWriter dynamicWriter = new PrintWriter("doc/examples/Dynamic" + L + "-" + W + "-" + 0.0 + ".txt", "UTF-8");
		PrintWriter staticWriter = new PrintWriter("doc/examples/Static" + L + "-" + W + "-" + 0.0 + ".txt", "UTF-8");

		List<Vector2> positions = getPositions(L, W, DEFAULT_RADIUS);

		staticWriter.println(positions.size());
		staticWriter.println(W + " " + (L + 1));
		staticWriter.println(L + " " + W + " " + 0);

		for (Vector2 position : positions) {
			printParticleLine(staticWriter, dynamicWriter, position, PARTICLE_MASS, DEFAULT_RADIUS);
		}

		staticWriter.close();
		dynamicWriter.close();
	}

	private static List<Vector2> getPositions(double L, double W, double radius) {
		long tolerance = 4000;
		List<Vector2> positions = new ArrayList<>();
		long time = 0;
		long start = 0;
		long end = 0;
		while (time < tolerance) {
			start = System.currentTimeMillis();

			boolean flag = false;
			while (!flag) {
				if ((System.currentTimeMillis() - start) > tolerance)
					break;
				double x = RandomUtils.randomBetween(0 + radius, W - radius);
				double y = RandomUtils.randomBetween(1 + radius, 1 + L - radius);
				Vector2 position = new Vector2(x, y);
				boolean found = false;
				for (Vector2 other : positions) {
					if (other.distanceTo(position) < radius * 2) {
						found = true;
						break;
					}
				}
				if (!found) {
					positions.add(position);
					flag = true;
				}
			}

			end = System.currentTimeMillis();
			time = end - start;
			System.out.println("found in: " + time + ", now: " + positions.size());
		}
		return positions;
	}

	private static void printParticleLine(PrintWriter staticWriter, PrintWriter dynamicWriter, Vector2 position,
			double mass, double radius) {
		StaticFileEntry staticEntry = new StaticFileEntry(mass, radius);
		DynamicFileEntry dynamicEntry = new DynamicFileEntry(position.getX(), position.getY());

		dynamicWriter.println(dynamicEntry);
		staticWriter.println(staticEntry);
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		generateRandomInput(6.0, 3.0, 2.0);
	}

}
