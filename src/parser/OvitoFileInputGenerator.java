package parser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import model.Particle;
import model.SimulationData;
import model.Vector2;

/**
 * Generates an input file for Ovito (Visualization Tool)
 */
public class OvitoFileInputGenerator {
	private static final String ENCODING = "UTF-8";
	private static final String BLUE = "0 0 255";
	private static final String WHITE = "255 255 255";
	
	private double D;
	private double W;
	private PrintWriter writer;
	private String filePath;

	public OvitoFileInputGenerator(String filePath, double W, double D) {
		this.filePath = filePath;
		this.W = W;
		this.D = D;
	}

	public void generateFile() throws FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter(filePath, ENCODING);
	}

	public void printSimulationFrame(SimulationData simulationData) {
		// TODO: still +1??
		int wallsParticlesCount = wallsParticlesCount();
		printHeaders(simulationData.getParticlesAmount() + 1, wallsParticlesCount);
		for (Particle particle : simulationData.getParticles()) {
			writer.println(generateLine(particle));
		}
//		System.out.println(simulationData.getParticles());
		printBoundariesParticles(simulationData.getWidth(), simulationData.getHeight(),
				simulationData.getParticlesAmount(), 0.1);
		printWalls();
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

	private void printHeaders(int particlesAmount, int wallsParticlesCount) {
		writer.println(particlesAmount + 4 + wallsParticlesCount);
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
	
	private int wallsParticlesCount() {
		int count = 0;
		for(double x = 0; x< (W - D) / 2; x += 0.01) {
			count++;
		}
		
		for(double x = (W + D) / 2; x < W; x += 0.01) {
			count++;
		}
		return count;
	}
	
	private void printWalls() {
		for(double x = 0; x < (W - D) / 2; x += 0.01) {
			writer.println(1 + " " + x + " " + 0.95 + " 0 0 " + WHITE + " 0.01 " + WHITE);
		}
		
		for(double x = (W + D) / 2; x < W; x += 0.01) {
			writer.println(1 + " " + x + " " + 0.95 + " 0 0 " + WHITE + " 0.01 " + WHITE);
		}
	}
	
}