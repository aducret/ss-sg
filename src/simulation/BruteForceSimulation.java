package simulation;

import model.Particle;
import model.SimulationData;

public class BruteForceSimulation implements Simulation {

	private SimulationData simulationData;

	@Override
	public void simulate(SimulationData simulationData, SimulationListener simulationListener) {
		this.simulationData = simulationData;
		clearNeighbors();
		calculateDistances();
	}

	private void clearNeighbors() {
		for (Particle particle : simulationData.getParticles()) {
			particle.getNeighbors().clear();
		}
	}

	private void calculateDistances() {
		for (Particle particleA : simulationData.getParticles()) {
			for (Particle particleB : simulationData.getParticles()) {
				if (particleA.equals(particleB))
					continue;
				if (!satisfiesDistance(particleA, particleB))
					continue;
				if (particleA.getNeighbors().contains(particleB))
					continue;
				// System.out.println(particleA.getId() + " with " +
				// particleB.getId());
				particleA.getNeighbors().add(particleB);
				particleB.getNeighbors().add(particleA);
			}
		}
	}

	private boolean satisfiesDistance(Particle particleA, Particle particleB) {
		return particleA.getPosition().distanceTo(particleB.getPosition()) < simulationData.getInteractionRadius();
	}

	private double distanceBetween(Particle particleA, Particle particleB) {
		double ax = particleA.getPosition().getX();
		double ay = particleA.getPosition().getY();
		double bx = particleB.getPosition().getX();
		double by = particleB.getPosition().getY();
		return distanceBetween(ax, ay, bx, by) - particleA.getRadius() - particleB.getRadius();
	}

	private double distanceBetween(double x1, double y1, double x2, double y2) {
		double a = x1 - x2;
		double b = y1 - y2;
		return Math.sqrt(a * a + b * b);
	}
}
