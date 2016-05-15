package simulation;

import model.SimulationData;

public interface SimulationListener {
	public void onFrameAvailable(SimulationData frame);
}
