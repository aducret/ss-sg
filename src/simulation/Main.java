package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import generator.SimulationInputGenerator;
import model.SimulationData;
import parser.InformationParser;
import parser.OvitoFileInputGenerator;
import simulation.SocialForceSimulation.FlowListener;

public class Main {
	private static final String ENCODING = "UTF-8";

	private static final int N = 20;
	private static final double VD_START = 0.5;
	private static final double VD_END = 1.0;
	private static final double STEP = 0.1;
	private static final int M = 6;
	private static final double L = 20.0;
	private static final double W = 20.0;
	private static final double D = 1.2;

	// private static String DYNAMIC_FILE_PATH = "doc/examples/Dynamic" +
	// "Testing" + ".txt";
	// private static String STATIC_FILE_PATH = "doc/examples/Static" +
	// "Testing" + ".txt";

	private static String DYNAMIC_FILE_PATH = "doc/examples/Dynamic" + N + "-" + L + "-" + W + "-" + D + ".txt";
	private static String STATIC_FILE_PATH = "doc/examples/Static" + N + "-" + L + "-" + W + "-" + D + ".txt";

	private static final double TIME_DELTA = 0.01;
	private static final double TIME_FRAME = 0.1;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		for (double vd = VD_START; vd <= VD_END; vd += STEP) {
			System.out.println("starting with vd: " + vd);
			for (int m = 1; m <= M; m++) {
				SimulationInputGenerator.generateWithConstants(N, vd);
				System.out.println("generated input: " + m + ", starting simulation...");
				runSimulation(m);
			}
		}
	}

	private static void runSimulation(int m) throws FileNotFoundException, UnsupportedEncodingException {
		SimulationData simulationData = parseSimulationData();
		if (simulationData == null) {
			throw new IllegalArgumentException("couldn't read simulationData!");
		}

		double vd = simulationData.getVd();
		final OvitoFileInputGenerator ovito = new OvitoFileInputGenerator(
				"doc/examples/result" + N + "-" + vd + "-" + m + ".txt", W, D);
		ovito.generateFile();

		PrintWriter flowWriter = new PrintWriter("doc/examples/flow" + N + "-" + vd + "-" + m + ".txt", ENCODING);

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
				ovito.printSimulationFrame(frame);
			}
		});
		ovito.endSimulation();
		flowWriter.close();
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
