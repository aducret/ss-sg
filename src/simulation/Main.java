package simulation;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import model.Particle;
import model.SimulationData;
import model.Vector2;
import parser.InformationParser;
import parser.OvitoFileInputGenerator;
import util.PhysicsUtils;

public class Main {
	public static final double INTERACTION_RADIUS = 10e6;

	private static final double L = 10.0;
	private static final double W = 9.9;
	private static final double D = 3.0;

	private static String DYNAMIC_FILE_PATH = "doc/examples/Dynamic" + L + "-" + W + "-" + D + ".txt";
	private static String STATIC_FILE_PATH = "doc/examples/Static" + L + "-" + W + "-" + D + ".txt";

	private static final double TIME_DELTA = 0.00001;
	private static final double TIME_FRAME = 0.01;
	private static final double TIME_GOAL = 120.0;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		SimulationData simulationData = parseSimulationData();
		if (simulationData == null) {
			throw new IllegalArgumentException("couldn't read simulationData!");
		}

		final OvitoFileInputGenerator ovito = new OvitoFileInputGenerator("doc/examples/result.txt");
		ovito.generateFile();

//		new CellIndexMethodSimulation(getOptimalValidM(simulationData), false).simulate(simulationData, null);
//		System.out.println("M: " + getOptimalValidM(simulationData));
//		System.out.println("IR: " + simulationData.getInteractionRadius());
//		new BruteForceSimulation().simulate(simulationData, null);
		
//		for (Particle particle: simulationData.getParticles()) {
//			System.out.println("neighbors: " + particle.getNeighbors().size());
//		}
		GranularSimulation granularSimulation = new GranularSimulation(TIME_GOAL, TIME_DELTA, TIME_FRAME);
		granularSimulation.simulate(simulationData, new SimulationListener() {

			@Override
			public void onFrameAvailable(SimulationData frame) {
				ovito.printSimulationFrame(frame);
			}
		});
		ovito.endSimulation();
	}

	private static SimulationData parseSimulationData() {
		try {
			SimulationData sd = InformationParser.generateCellIndexObject(DYNAMIC_FILE_PATH, STATIC_FILE_PATH).build();
			return sd;
		} catch (FileNotFoundException e) {
			System.err.println("Can not generate cell index object. Error: " + e.getMessage());
			return null;
		}
	}

	public static int getOptimalValidM(SimulationData simulationData) {
		double L = simulationData.getL() + 1;
		double r = simulationData.getInteractionRadius();
		int M = (int) Math.floor(L / (r + 1));
		return M;
	}
}
