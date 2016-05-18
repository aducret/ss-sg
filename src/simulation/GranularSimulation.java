package simulation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import integration.VerletAlgorithm;
import model.Particle;
import model.SimulationData;
import model.Vector2;
import util.PhysicsUtils;

public class GranularSimulation implements Simulation {
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
	
	public GranularSimulation(double goalTime, double dt, double dtFrame) {
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
		while (currentTime < goalTime) {
			moveSystemForward();
			killOutsiders();
			if (timeToNextFrame < 0) {
				timeToNextFrame = dtFrame;
				System.out.println("currentTime: " + currentTime);
				if (simulationListener != null)
					simulationListener.onFrameAvailable(currentTime, simulationData);
				long took = System.currentTimeMillis() - lastFrameTime;
				lastFrameTime = System.currentTimeMillis();
				System.out.println("estimated time left: " + timeLeft(took));
			}
		}

	}

	private String timeLeft(long took) {
		long time = (took * framesLeft())/1000;
		long hours = time/3600;
		time -= hours*3600;
		long minutes = time/60;
		time -= minutes*60;
		long seconds = time;
		return hours + "h " + minutes + "m " + seconds + "s";
	}

	private int framesLeft() {
		return (int) Math.floor((goalTime - currentTime) / dtFrame);
	}

	private void moveSystemForward() {

		// TODO: improve with CellIndexMethod (can be used with non-square
		// spaces?)
		// interaction radius = particle radius * 2?
		// new BruteForceSimulation().simulate(simulationData, null);
		new CellIndexMethodSimulation(Main.getOptimalValidM(simulationData), false).simulate(simulationData, null);
		;

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
		// gravity
		Vector2 force = PhysicsUtils.earthGravityForce(particle.getMass());

		// walls
		force = force.sum(leftHWallForce(particle));
		force = force.sum(leftVWallForce(particle));
		force = force.sum(rightHWallForce(particle));
		force = force.sum(rightVWallForce(particle));
		// force = force.sum(topWallForce());

		// neighbors
		// System.out.println("D: " + simulationData.getD());
		// System.out.println("IR: " + simulationData.getInteractionRadius());
		for (Particle neighbor : particle.getNeighbors()) {
			// System.out.println(particle.getPosition().distanceTo(neighbor.getPosition()));
			force = force.sum(forceBy(particle, neighbor));
		}

		particle.resultantForce = force;
	}

	/*
	 * force exerted by p2 to p1
	 */
	private Vector2 forceBy(Particle p1, Particle p2) {
		double e = p1.getRadius() - p2.getPosition().distanceTo(p1.getPosition());
		Vector2 versorN = p2.getPosition().substract(p1.getPosition()).normalize();
		Vector2 versorT = versorN.rotateCCW(Math.PI / 2.0);

		// angle between velocities
		Vector2 velocity = VerletAlgorithm.getVelocity(dt, p1);
		double alpha = velocity.angleWith(versorT);
		double vt = velocity.getMagnitude() * Math.cos(alpha);

		double fN = KN * e;
		double fT = KT * e * vt;

		Vector2 force = versorN.scale(-fN).sum(versorT.scale(-fT));

		return force;
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
		return forceBy(particle, wall);
	}
}
