package simulation;

import java.io.FileNotFoundException;

import model.SimulationData;

public interface Simulation {
	public void simulate(SimulationData simulationData, SimulationListener simulationListener) throws FileNotFoundException;
}
