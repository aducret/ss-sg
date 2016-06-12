package parser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import model.Particle;
import model.SimulationData;

/**
 * Generates an input file for Ovito (Visualization Tool)
 */
public class OvitoFileInputGenerator {
	private static final String ENCODING = "UTF-8";
	private static final String BLUE = "0 0 255";
	private static final String GREEN = "0 255 0";

	private PrintWriter writer;
	private String filePath;

	public OvitoFileInputGenerator(String filePath) {
		this.filePath = filePath;
	}

	public void generateFile() throws FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter(filePath, ENCODING);
	}

	public void printSimulationFrame(SimulationData simulationData) {
		// TODO: still +1??
		printHeaders(simulationData.getParticlesAmount() + 1);
		for (Particle particle : simulationData.getParticles()) {
			writer.println(generateLine(particle));
		}
//		System.out.println(simulationData.getParticles());
		printBoundariesParticles(simulationData.getWidth(), simulationData.getHeight(),
				simulationData.getParticlesAmount(), 0.1);
	}

	public void endSimulation() {
		writer.close();
	}

	private void printBoundariesParticles(double width, double height, int particleAmount, double radius) {
		double K = 0;
		printBoundaryParticle(particleAmount + 2, 0 - K, -K);
		printBoundaryParticle(particleAmount + 3, width + K, -K);
		printBoundaryParticle(particleAmount + 4, 0 - K, height + K);
		printBoundaryParticle(particleAmount + 5, width + K, height + K);
	}

	private void printBoundaryParticle(int id, double x, double y) {
		writer.println(id + " " + x + " " + y + " 0 0 " + BLUE + " 0 " + BLUE);
	}

	private void printHeaders(int particlesAmount) {
		writer.println(particlesAmount + 4);
		writer.println("ID X Y dx dy pR pG pB r vR vG vB");
	}

	private String generateLine(Particle particle) {
		StringBuilder line = new StringBuilder();
		String particleColor = generateParticleColor(particle);
		line.append(particle.getId()).append(" ").append(particle.getPosition().getX()).append(" ")
				.append(particle.getPosition().getY()).append(" ").append(particle.getVelocity().getX()).append(" ")
				.append(particle.getVelocity().getY()).append(" ").append(particleColor).append(" ")
				.append(particle.getRadius()).append(" ").append(particleColor);
		return line.toString();
	}

	private String generateParticleColor(Particle particle) {
		// return GREEN;
		// if (particle.getVelocity() == null ||
		// particle.getVelocity().getMagnitude() < 1e-9)
		// return BLUE;
		// double red = (Math.sin(particle.getAngle()) / 2) + 0.5;
		// double green = (Math.cos(particle.getAngle()) / 2) + 0.5;
		// return red + " " + green + " 0.2";
		double speed = particle.getSpeed();
		double factor = 1 - Math.pow(Math.E, -0.8 * speed);
		double red = factor;
		double blue = 1 - factor;
		String color = red + " 0 " + blue;
		return color;
	}
}