package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import model.Particle;
import model.SimulationData;
import model.Vector2;
import parser.InformationParser;
import parser.OvitoFileInputGenerator;
import simulation.GranularSimulation.FlowListener;
import util.PhysicsUtils;

public class Main {
	private static final String ENCODING = "UTF-8";

	private static final double L = 6.0;
	private static final double W = 3.0;
	private static final double D = 2.0;

	private static String DYNAMIC_FILE_PATH = "doc/examples/Dynamic" + L + "-" + W + "-" + D + ".txt";
	private static String STATIC_FILE_PATH = "doc/examples/Static" + L + "-" + W + "-" + D + ".txt";

	private static final double TIME_DELTA = 0.00001;
	private static final double TIME_FRAME = 0.01;
	private static final double TIME_GOAL = 1.5;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		SimulationData simulationData = parseSimulationData();
		if (simulationData == null) {
			throw new IllegalArgumentException("couldn't read simulationData!");
		}

		final OvitoFileInputGenerator ovito = new OvitoFileInputGenerator("doc/examples/result.txt");
		ovito.generateFile();

		PrintWriter flowWriter = new PrintWriter("doc/examples/flow.txt", ENCODING);
		PrintWriter kineticWriter = new PrintWriter("doc/examples/kinetic.txt", ENCODING);
		
		GranularSimulation granularSimulation = new GranularSimulation(TIME_GOAL, TIME_DELTA, TIME_FRAME);
		granularSimulation.setFlowListener(new FlowListener() {
			
			@Override
			public void onBelowOutsiderDetected(double time) {
				flowWriter.println(time);
			}
		});
		granularSimulation.simulate(simulationData, new SimulationListener() {

			@Override
			public void onFrameAvailable(double time, SimulationData frame) {
				kineticWriter.println(time + "," + frame.getKineticEnergy());
				ovito.printSimulationFrame(frame);
			}
		});
		ovito.endSimulation();
		flowWriter.close();
		kineticWriter.close();
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
