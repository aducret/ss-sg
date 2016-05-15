package simulation;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import model.Particle;
import model.SimulationData;
import model.Vector2;
import parser.InformationParser;
import parser.OvitoFileInputGenerator;

public class Main {
	public static final double INTERACTION_RADIUS = 10e6;

	private static String DYNAMIC_FILE_PATH = "doc/examples/Dynamic";
	private static String STATIC_FILE_PATH = "doc/examples/Static";
	private static final double TIME_DELTA = 500;
	 private static final double TIME_GOAL = 3600 * 24 * 25;
//	private static final double TIME_GOAL = TIME_DELTA * 2;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		SimulationData simulationData = parseSimulationData();
		if (simulationData == null) {
			throw new IllegalArgumentException("couldn't read simulationData!");
		}

		final OvitoFileInputGenerator ovito = new OvitoFileInputGenerator("doc/examples/result.txt");
		ovito.generateFile();

		SolarSystemSimulation solarSystemSimulation = new SolarSystemSimulation(TIME_GOAL, TIME_DELTA);
		solarSystemSimulation.simulate(simulationData, new SimulationListener() {

			@Override
			public void onFrameAvailable(SimulationData frame) {
				System.out.println("frame");
				ovito.printSimulationFrame(frame);
			}
		});
		ovito.endSimulation();
	}

	private static SimulationData parseSimulationData() {
		try {
			SimulationData sd = InformationParser.generateCellIndexObject(DYNAMIC_FILE_PATH, STATIC_FILE_PATH).build();
			for (Particle particle : sd.getParticles()) {
				particle.setVelocity(PhysicsUtils.orbitalVelocity(particle, sd.getSpecialParticle()));
			}
			sd.getSpecialParticle().setVelocity(new Vector2(0, 0));
			return sd;
		} catch (FileNotFoundException e) {
			System.err.println("Can not generate cell index object. Error: " + e.getMessage());
			return null;
		}
	}

	public static int getOptimalValidM(SimulationData simulationData) {
		double L = simulationData.getSpaceDimension();
		double r = simulationData.getInteractionRadius();
		int M = (int) Math.floor(L / (r + 1));
		return M;
	}
}
