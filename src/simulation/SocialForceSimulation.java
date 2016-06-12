package simulation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import integration.VerletAlgorithm;
import model.Particle;
import model.SimulationData;
import model.Vector2;
import util.PhysicsUtils;

public class SocialForceSimulation implements Simulation {
	private static final double KN = Math.pow(10, 5);
	private static final double KT = 2 * KN;

	private double goalTime;
	private double currentTime;
	private double dt;
	private double dtFrame;
	private double timeToNextFrame;

	private long lastFrameTime;

	private SimulationListener simulationListener;
	private SimulationData simulationData;

	private FlowListener flowListener;

	public interface FlowListener {
		void onBelowOutsiderDetected(double time);
	}

	public SocialForceSimulation(double goalTime, double dt, double dtFrame) {
		this.goalTime = goalTime;
		this.dt = dt;
		this.dtFrame = dtFrame;
	}

	public void setFlowListener(FlowListener flowListener) {
		this.flowListener = flowListener;
	}

	@Override
	public void simulate(SimulationData simulationData, SimulationListener simulationListener)
			throws FileNotFoundException {
		this.simulationData = simulationData;
		this.simulationListener = simulationListener;
		currentTime = 0;

		if (simulationListener != null)
			simulationListener.onFrameAvailable(0, simulationData);
		lastFrameTime = System.currentTimeMillis();

		timeToNextFrame = dtFrame;
		while (simulationData.getParticles().size() > 0) {
			moveSystemForward();
			killOutsiders();
			if (timeToNextFrame < 0) {
				timeToNextFrame = dtFrame;
//				System.out.println("currentTime: " + currentTime);
				if (simulationListener != null)
					simulationListener.onFrameAvailable(currentTime, simulationData);
				long took = System.currentTimeMillis() - lastFrameTime;
				lastFrameTime = System.currentTimeMillis();
//				System.out.println("estimated time left: " + timeLeft(took));
			}
		}

	}

	private String timeLeft(long took) {
		long time = (took * framesLeft()) / 1000;
		long hours = time / 3600;
		time -= hours * 3600;
		long minutes = time / 60;
		time -= minutes * 60;
		long seconds = time;
		return hours + "h " + minutes + "m " + seconds + "s";
	}

	private int framesLeft() {
		return (int) Math.floor((goalTime - currentTime) / dtFrame);
	}

	private void moveSystemForward() {

		// new BruteForceSimulation().simulate(simulationData, null);

		for (Particle particle : simulationData.getParticles()) {
			calculateForce(particle);
		}

		for (Particle particle : simulationData.getParticles()) {
			VerletAlgorithm.move(dt, particle);
		}
		currentTime += dt;
		timeToNextFrame -= dt;
	}

	private void killOutsiders() {
		List<Integer> ids = new ArrayList<>();
		for (Particle particle : simulationData.getParticles()) {
			if (isOutsider(particle)) {
				ids.add(particle.getId());
				if (isBelowOutsider(particle) && flowListener != null) {
					System.out.println("outsider detected. " + (simulationData.getParticles().size() - 1) + " left...");
					flowListener.onBelowOutsiderDetected(currentTime);
				}
			}
		}
		for (Integer id : ids) {
			simulationData.removeParticleById(id);
		}
		simulationData.fixIds();
	}

	private boolean isOutsider(Particle particle) {
		Vector2 p = particle.getPosition();
		double L = simulationData.getL();
		double W = simulationData.getW();
		return p.y < 0 || p.y > 1 + L || p.x < 0 || p.x > W;
	}

	private boolean isBelowOutsider(Particle particle) {
		Vector2 p = particle.getPosition();
		return p.y < 0;
	}

	private void calculateForce(Particle particle) {
		Vector2 force = new Vector2(0, 0);

		// walls
		force = force.sum(leftHWallForce(particle));
		force = force.sum(leftVWallForce(particle));
		force = force.sum(rightHWallForce(particle));
		force = force.sum(rightVWallForce(particle));
		force = force.sum(topWallForce(particle));

		// neighbors
		// cell index method for contact force
		new CellIndexMethodSimulation(Main.getOptimalValidM(simulationData), false).simulate(simulationData, null);
		for (Particle neighbor : particle.getNeighbors()) {
			force = force.sum(forceBy(particle, neighbor));
		}

		// target force
		force = force.sum(targetForce(particle));

		// social force
		double safeDistance = 1;
		double interactionRadiusBackup = simulationData.getInteractionRadius();
		simulationData.setInteractionRadius(safeDistance);
		new CellIndexMethodSimulation(Main.getOptimalValidM(simulationData), false).simulate(simulationData, null);
		simulationData.setInteractionRadius(interactionRadiusBackup);
		for (Particle neighbor : particle.getNeighbors()) {
			force = force.sum(socialForce(particle, neighbor));
		}
		particle.resultantForce = force;
	}

	/*
	 * force exerted by p2 to p1
	 */
	private Vector2 forceBy(Particle p1, Particle p2) {
		if (p1.getPosition().distanceTo(p2.getPosition()) - p1.getRadius() - p2.getRadius() > 0) {
			System.out.println("what the fuck!");
			return new Vector2(0, 0);
		}
		double e = (p1.getRadius() + p2.getRadius()) - p2.getPosition().distanceTo(p1.getPosition());
		Vector2 versorN = p2.getPosition().substract(p1.getPosition()).normalize();
		Vector2 versorT = versorN.rotateCCW(Math.PI / 2.0);

		// angle between velocities
		Vector2 v1 = VerletAlgorithm.getVelocity(dt, p1);
		Vector2 v2 = VerletAlgorithm.getVelocity(dt, p2);
		double alpha = v1.angleWith(versorN);
		double vt = v1.getMagnitude() * Math.sin(alpha);
		double relativeV = v1.substract(v2).dotProduct(versorT.scale(vt));

		double fN = KN * e;
		double fT = KT * e * relativeV;

		Vector2 force = versorN.scale(-fN).sum(versorT.scale(-fT));
		return force;
	}

	private Vector2 socialForce(Particle particle, Particle neighbor) {
		double A = simulationData.getA();
		double B = simulationData.getB();
		double rij = particle.getPosition().distanceTo(neighbor.getPosition());
		double ri = particle.getRadius();
		double rj = neighbor.getRadius();
		double eij = rij - (ri + rj);
		Vector2 eNij = particle.getPosition().substract(neighbor.getPosition()).normalize();
		double scalingFactor = A * Math.pow(Math.E, -(eij / B));
		return eNij.scale(scalingFactor);
	}

	private Vector2 targetForce(Particle particle) {
		double W = simulationData.getW();
		double D = simulationData.getD();

		Vector2 position = particle.getPosition();
		double tao = simulationData.getTao();
		double vd = simulationData.getVd();
		double mass = particle.getMass();
		Vector2 eTarget = null;
		Vector2 currentVelocity = VerletAlgorithm.getVelocity(dt, particle);

		double xDoorLeft = (W - D) / 2.0 + particle.getRadius();
		double xDoorRight = (W + D) / 2.0 - particle.getRadius();

		// definir eTarget:
		if (position.getY() < 1) {
			eTarget = new Vector2(0, -1);
		} else if (position.getX() > xDoorRight) {
			Vector2 targetPosition = new Vector2(xDoorLeft, 1);
			eTarget = targetPosition.substract(position).normalize();
		} else if (position.getX() < xDoorLeft) {
			Vector2 targetPosition = new Vector2(xDoorRight, 1);
			eTarget = targetPosition.substract(position).normalize();
		} else {
			Vector2 targetPosition = new Vector2(W / 2.0, -1);
			eTarget = targetPosition.substract(position).normalize();
		}

		return eTarget.scale(vd).substract(currentVelocity).scale(mass / tao);
	}

	private Vector2 leftHWallForce(Particle particle) {
		double x = particle.getPosition().x;
		double y = particle.getPosition().y;
		double W = simulationData.getW();
		double D = simulationData.getD();
		if (x <= 0 - particle.getRadius() || x >= (W - D) / 2.0 + particle.getRadius())
			return new Vector2(0, 0);
		if (y <= 1 - particle.getRadius() || y >= 1 + particle.getRadius())
			return new Vector2(0, 0);

		// if we reach this line, there is interaction between this particle and
		Vector2 wallPosition = new Vector2(particle.getPosition().x, 1);
		Particle wall = new Particle(1, wallPosition, particle.getMass());
		wall.setRadius(0);
		return forceBy(particle, wall);
	}

	private Vector2 leftVWallForce(Particle particle) {
		double x = particle.getPosition().x;
		double y = particle.getPosition().y;
		double L = simulationData.getL();
		if (y <= 1 - particle.getRadius() || y >= 1 + L + particle.getRadius())
			return new Vector2(0, 0);
		if (x <= 0 - particle.getRadius() || x >= 0 + particle.getRadius())
			return new Vector2(0, 0);

		// if we reach this line, there is interaction between this particle and
		Vector2 wallPosition = new Vector2(0, particle.getPosition().y);
		Particle wall = new Particle(1, wallPosition, particle.getMass());
		wall.setRadius(0);
		return forceBy(particle, wall);
	}

	private Vector2 rightHWallForce(Particle particle) {
		double x = particle.getPosition().x;
		double y = particle.getPosition().y;
		double W = simulationData.getW();
		double D = simulationData.getD();
		if (x >= W + particle.getRadius() || x <= (W + D) / 2.0 - particle.getRadius())
			return new Vector2(0, 0);
		if (y <= 1 - particle.getRadius() || y >= 1 + particle.getRadius())
			return new Vector2(0, 0);

		// if we reach this line, there is interaction between this particle and
		Vector2 wallPosition = new Vector2(particle.getPosition().x, 1);
		Particle wall = new Particle(1, wallPosition, particle.getMass());
		wall.setRadius(0);
		return forceBy(particle, wall);
	}

	private Vector2 rightVWallForce(Particle particle) {
		double x = particle.getPosition().x;
		double y = particle.getPosition().y;
		double W = simulationData.getW();
		double L = simulationData.getL();
		if (y <= 1 - particle.getRadius() || y >= 1 + L + particle.getRadius())
			return new Vector2(0, 0);
		if (x <= W - particle.getRadius() || x >= W + particle.getRadius())
			return new Vector2(0, 0);

		// if we reach this line, there is interaction between this particle and
		Vector2 wallPosition = new Vector2(W, particle.getPosition().y);
		Particle wall = new Particle(1, wallPosition, particle.getMass());
		wall.setRadius(0);
		return forceBy(particle, wall);
	}

	private Vector2 topWallForce(Particle particle) {
		double x = particle.getPosition().x;
		double y = particle.getPosition().y;
		double W = simulationData.getW();
		double L = simulationData.getL();
		if (y <= L + 1 - particle.getRadius() || y >= 1 + L + particle.getRadius())
			return new Vector2(0, 0);
		if (x <= 0 - particle.getRadius() || x >= W + particle.getRadius())
			return new Vector2(0, 0);

		// if we reach this line, there is interaction between this particle and
		Vector2 wallPosition = new Vector2(particle.getPosition().x, L + 1);
		Particle wall = new Particle(1, wallPosition, particle.getMass());
		wall.setRadius(0);
		return forceBy(particle, wall);
	}
}
