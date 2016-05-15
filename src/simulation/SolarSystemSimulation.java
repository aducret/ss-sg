package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import model.Collision;
import model.Particle;
import model.SimulationData;
import model.Vector2;

public class SolarSystemSimulation implements Simulation {

	private static final double G = Math.pow(6.693, -11);
	private double goalTime;
	private double currentTime;
	private double dt;

	private SimulationListener simulationListener;
	private SimulationData simulationData;

	public SolarSystemSimulation(double goalTime, double dt) {
		this.goalTime = goalTime;
		this.dt = dt;
	}

	@Override
	public void simulate(SimulationData simulationData, SimulationListener simulationListener)
			throws FileNotFoundException {
		this.simulationData = simulationData;
		this.simulationListener = simulationListener;
		currentTime = 0;

		if (simulationListener != null)
			simulationListener.onFrameAvailable(simulationData);
		PrintWriter writer = new PrintWriter("doc/examples/simulationEnergy.csv");
		// writer.println("EP, EC, E");
		while (currentTime < goalTime) {
			System.out.println("time left: " + (goalTime - currentTime));
			double EP = 0;
			double EC = 0;
			boolean flag = false;
			for (Particle particle : simulationData.getParticles()) {
				if (particle.oldVelocity == null) {
					flag = true;
					break;
				}
				double particleMass = particle.getMass();
				double specialParticleMass = simulationData.getSpecialParticle().getMass();
				double distanceBetweenParticles = particle.getPosition()
						.distanceTo(simulationData.getSpecialParticle().getPosition());
				EP += calculatePotentialEnergy(specialParticleMass, particleMass, distanceBetweenParticles);
				EC += calculateCineticEnergy(particle);
			}
			double E = EC + EP;
			if (!flag) {
				writer.println(EP + ", " + EC + ", " + E);
			}
			handleCollisions();
			moveSystemForward();
			if (simulationListener != null)
				simulationListener.onFrameAvailable(simulationData);
		}
		writer.close();
	}

	private double calculateCineticEnergy(Particle particle) {
		double vx = particle.oldVelocity.getX();
		double vy = particle.oldVelocity.getY();
		double speedPow2 = vx * vx + vy * vy;
		return 0.5 * particle.getMass() * speedPow2;
	}

	private double calculatePotentialEnergy(double mi, double mj, double rij) {
		return (-G * mi * mj) / rij;
	}

	/*
	 * aplica todas las colisiones que sean posibles para el instante actual
	 */
	private void handleCollisions() {
		boolean flag = false;
		while (!flag) {
			Collision collision = checkIfCollision();
			if (collision == null)
				flag = true;
			else
				collide(collision);
		}
	}

	/*
	 * usa cellIndexMethod sobre simulationData con 10^6 de interaction radius
	 * para chequear si hay alguna colision. Devuelve la primera que encuentra
	 */
	private Collision checkIfCollision() {
		BruteForceSimulation bfs = new BruteForceSimulation();
		bfs.simulate(simulationData, null);
		for (Particle particle : simulationData.getParticles()) {
			if (particle.getNeighbors().isEmpty())
				continue;
			return new Collision(particle, particle.getNeighbors().get(0));
		}
		return null;
	}

	/*
	 * este metodo elimina de SimulationData las 2 particulas de la colision y
	 * agrega una tercera que tiene su centro en el centro de masa de las 2
	 * anteriores, preserva el momento angular y demas gilada
	 */
	private void collide(Collision collision) {
		System.out.println("COLLISION!");
		Particle sun = simulationData.getSpecialParticle();
		Particle newParticle = collision.getP1();
		Particle deletedParticle = collision.getP2();
		simulationData.removeParticleById(deletedParticle.getId());
//		double newMomentum = newParticle.getMomentum(sun) + deletedParticle.getMomentum(sun);
		newParticle.setMass(newParticle.getMass() + deletedParticle.getMass());

		// centro de masa
//		newParticle.setPosition(newParticle.getPosition().scale(newParticle.getMass())
//				.sum(deletedParticle.getPosition().scale(deletedParticle.getMass()))
//				.scale(1.0 / (newParticle.getMass() + deletedParticle.getMass())));
//		double newSpeed = newMomentum
//				/ (newParticle.getPosition().distanceTo(sun.getPosition()) * newParticle.getMass());
//		Vector2 newVelocity = newParticle.getPosition().substract(sun.getPosition()).normalize().scale(newSpeed)
//				.rotateCW(Math.PI / 2.0);
//		newParticle.setVelocity(newVelocity);
		newParticle.setVelocity(PhysicsUtils.orbitalVelocity(newParticle, simulationData.getSpecialParticle()));
		newParticle.oldVelocity = null;
		newParticle.oldPosition = null;
		simulationData.fixIds();
	}

	/*
	 * Mueve el sistema hacia adelante un tiempo dt, usando el algoritmo
	 * provisto por la catedra.
	 */
	private void moveSystemForward() {
		for (Particle particle : simulationData.getParticles()) {
			// System.out.println(particle.getPosition());
			VerletAlgorithm.move(dt, particle, simulationData.getSpecialParticle());
			// particle.setVelocity(PhysicsUtils.orbitalVelocity(particle,
			// simulationData.getSpecialParticle()));
		}
		currentTime += dt;
	}
}
