package generator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;
import model.Vector2;
import util.RandomUtils;

public class SimulationInputGenerator {
	private static final double PARTICLE_MASS = 80;
	private static final double MAX_RADIUS = 0.35;
	private static final double MIN_RADIUS = 0.25;
	private static final double TAO = 0.5;
	private static final double A = 2000;
	private static final double B = 0.08;
	private static final double KN = 1.2e5;
	private static final double KT = 2 * KN;
	private static final double D = 1.2;
	private static final double LW = 20.0;
	
	//estos son los unicos parametros que varian
	private static final double VD = 1.5;
	private static final int N = 30;

	public static void generateRandomInput(int N, double L, double W, double D)
			throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter dynamicWriter = new PrintWriter("doc/examples/Dynamic" + N + "-" + L + "-" + W + "-" + D + ".txt",
				"UTF-8");
		PrintWriter staticWriter = new PrintWriter("doc/examples/Static" + N + "-" + L + "-" + W + "-" + D + ".txt",
				"UTF-8");

		List<Pair<Vector2, Double>> positions = getPositions(N, L, W);

		staticWriter.println(positions.size());
		staticWriter.println(W + " " + (L + 1));
		staticWriter.println(L + " " + W + " " + D);
		staticWriter.println(KN + " " + KT + " " + A + " " + B + " " + VD + " " + TAO);

		for (Pair<Vector2, Double> position : positions) {
			printParticleLine(staticWriter, dynamicWriter, position.getKey(), PARTICLE_MASS, position.getValue());
		}

		staticWriter.close();
		dynamicWriter.close();
	}

	private static List<Pair<Vector2, Double>> getPositions(int N, double L, double W) {
		long tolerance = 4000;
		List<Pair<Vector2, Double>> positions = new ArrayList<>();
		long time = 0;
		long start = 0;
		long end = 0;
		while (time < tolerance) {
			start = System.currentTimeMillis();

			boolean flag = false;
			while (!flag) {
				if ((System.currentTimeMillis() - start) > tolerance)
					break;
				double radius = RandomUtils.randomBetween(MIN_RADIUS, MAX_RADIUS);
				double x = RandomUtils.randomBetween(0 + radius, W - radius);
				double y = RandomUtils.randomBetween(1 + radius, 1 + L - radius);
				Vector2 position = new Vector2(x, y);
				boolean found = false;
				for (Pair<Vector2, Double> other : positions) {
					if (other.getKey().distanceTo(position) - radius - other.getValue() < 0) {
						found = true;
						break;
					}
				}
				if (!found) {
					positions.add(new Pair<Vector2, Double>(position, radius));
					flag = true;

					if (positions.size() >= N) {
						end = System.currentTimeMillis();
						time = end - start;
						System.out.println("found all N in: " + time + ", now: " + positions.size());
						return positions;
					}
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
	
	public static void generateWithConstants() throws FileNotFoundException, UnsupportedEncodingException {
		generateRandomInput(N, LW, LW, D);
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		generateRandomInput(N, LW, LW, D);
	}

}
