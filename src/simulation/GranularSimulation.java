package simulation;

import java.io.FileNotFoundException;

import integration.VerletAlgorithm;
import model.Collision;
import model.Particle;
import model.SimulationData;

public class GranularSimulation implements Simulation {
	private static final double KN = Math.pow(10, 5);
	private static final double KT = 2 * KN;
	
	private double goalTime;
	private double currentTime;
	private double dt;
	
	private SimulationListener simulationListener;
	private SimulationData simulationData;
	
	public GranularSimulation(double goalTime, double dt) {
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
		
		while (currentTime < goalTime) {
			moveSystemForward();
			if (simulationListener != null)
				simulationListener.onFrameAvailable(simulationData);
		}
		
	}
	
	private void moveSystemForward() {
		
		//TODO: run CellIndexMethod
		
		for (Particle particle: simulationData.getParticles()) {
			//TODO: calculate resultaltForce for particle
		}
		
		for (Particle particle : simulationData.getParticles()) {
			VerletAlgorithm.move(dt, particle);
		}
		currentTime += dt;
	}
	
}
