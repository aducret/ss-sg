package simulation;

import model.SimulationData;

public interface SimulationListener {
	public void onFrameAvailable(double time, SimulationData frame);
}
