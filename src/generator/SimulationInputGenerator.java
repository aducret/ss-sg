package generator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import model.Vector2;
import util.RandomUtils;

public class SimulationInputGenerator {
	private static final double PARTICLE_MASS = 0.01;

	public static void generateRandomInput(double L, double W, double D)
			throws FileNotFoundException, UnsupportedEncodingException {
		if (D >= W || W >= L) {
			throw new IllegalArgumentException("No se respetaron las cotas (L > W > D)");
		}

		PrintWriter dynamicWriter = new PrintWriter("doc/examples/Dynamic" + L + "-" + W + "-" + D + ".txt", "UTF-8");
		PrintWriter staticWriter = new PrintWriter("doc/examples/Static" + L + "-" + W + "-" + D + ".txt", "UTF-8");

		double radius = D / 10.0;
		double fitsWidth = Math.floor(W / (radius * 2));
		double fitsHeight = Math.floor(L / (radius * 2));
		
		double coef = 1.8;

		ArrayList<Vector2> positions = new ArrayList<>();

		for (int i = 0; i < fitsHeight/coef; i++) {
			for (int j = 0; j < fitsWidth/coef; j++) {
				Vector2 p = new Vector2(j * (radius * 2)*coef, i * (radius * 2)*coef + 1);
				double x = RandomUtils.randomBetween(0, (radius * 2)*coef - (radius * 2));
				double y = RandomUtils.randomBetween(0, (radius * 2)*coef - (radius * 2));
				Vector2 position = new Vector2(p.x + x + radius, p.y + y + radius);
				positions.add(position);
			}
		}

		staticWriter.println(positions.size());
		staticWriter.println(W + " " + (L + 1));
		staticWriter.println(L + " " + W + " " + D);

		for (Vector2 position : positions) {
			printParticleLine(staticWriter, dynamicWriter, position, PARTICLE_MASS);
		}

		staticWriter.close();
		dynamicWriter.close();
	}

	private static void printParticleLine(PrintWriter staticWriter, PrintWriter dynamicWriter, Vector2 position,
			double mass) {
		StaticFileEntry staticEntry = new StaticFileEntry(mass);
		DynamicFileEntry dynamicEntry = new DynamicFileEntry(position.getX(), position.getY());

		dynamicWriter.println(dynamicEntry);
		staticWriter.println(staticEntry);
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		generateRandomInput(10, 5, 2);
	}
	
}
