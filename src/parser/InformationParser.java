package parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

import model.Particle;
import model.SimulationData;
import model.Vector2;

public class InformationParser {

	public static SimulationData.Builder generateCellIndexObject(String dynamicFilePath, String staticFilePath)
			throws FileNotFoundException {
		SimulationData.Builder builder = SimulationData.Builder.create();

		InputStream dynamicIS = new FileInputStream(dynamicFilePath);
		Scanner dynamicScanner = new Scanner(dynamicIS);
		dynamicScanner.useLocale(Locale.US);

		InputStream staticIS = new FileInputStream(staticFilePath);
		Scanner staticScanner = new Scanner(staticIS);
		staticScanner.useLocale(Locale.US);

		int particlesAmount = staticScanner.nextInt();
		double width = staticScanner.nextDouble();
		double height = staticScanner.nextDouble();
		double L = staticScanner.nextDouble();
		double W = staticScanner.nextDouble();
		double D = staticScanner.nextDouble();
		double kn = staticScanner.nextDouble();
		double kt = staticScanner.nextDouble();
		double A = staticScanner.nextDouble();
		double B = staticScanner.nextDouble();
		double vd = staticScanner.nextDouble();
		double tao = staticScanner.nextDouble();
		builder = builder.withParticlesAmount(particlesAmount - 1).withSpaceDimension(width, height)
				.withParameters(L, W, D).withKn(kn).withKt(kt).withA(A).withB(B).withVd(vd).withTao(tao);

		for (int i = 1; i <= particlesAmount; i++) {
			double mass = staticScanner.nextDouble();
			double radius = staticScanner.nextDouble();
			double x = dynamicScanner.nextDouble();
			double y = dynamicScanner.nextDouble();
			Particle particle = new Particle(i, new Vector2(x, y), mass);
			particle.setVelocity(new Vector2(0, 0));
			particle.radius = radius;
			builder = builder.withParticle(particle);
		}

		dynamicScanner.close();
		staticScanner.close();

		return builder.withInteractionRadius(0);
	}

}