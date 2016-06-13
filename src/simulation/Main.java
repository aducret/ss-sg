package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import model.SimulationData;
import parser.InformationParser;
import parser.OvitoFileInputGenerator;
import simulation.SocialForceSimulation.FlowListener;

public class Main {
	private static final String ENCODING = "UTF-8";

	private static final int N = 30;
	private static final double L = 20.0;
	private static final double W = 20.0;
	private static final double D = 1.2;

//	 private static String DYNAMIC_FILE_PATH = "doc/examples/Dynamic" +
//	 "Testing" + ".txt";
//	 private static String STATIC_FILE_PATH = "doc/examples/Static" +
//	 "Testing" + ".txt";

	private static String DYNAMIC_FILE_PATH = "doc/examples/Dynamic" + N + "-" + L + "-" + W + "-" + D + ".txt";
	private static String STATIC_FILE_PATH = "doc/examples/Static" + N + "-" + L + "-" + W + "-" + D + ".txt";

	private static final double TIME_DELTA = 0.005;
	private static final double TIME_FRAME = 0.01;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		SimulationData simulationData = parseSimulationData();
		if (simulationData == null) {
			throw new IllegalArgumentException("couldn't read simulationData!");
		}

		final OvitoFileInputGenerator ovito = new OvitoFileInputGenerator("doc/examples/result.txt", W, D);
		ovito.generateFile();

		PrintWriter flowWriter = new PrintWriter("doc/examples/flow.txt", ENCODING);
		PrintWriter kineticWriter = new PrintWriter("doc/examples/kinetic.txt", ENCODING);

		SocialForceSimulation granularSimulation = new SocialForceSimulation(TIME_DELTA, TIME_FRAME);
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
		double rMax = simulationData.getMaxRadius();
		int M = (int) Math.floor(L / (rMax + 1));
		return M;
	}
}
