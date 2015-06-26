package baron.jakub.controller.Loaders;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import baron.jakub.controller.ViewModifier;
import baron.jakub.model.Parameters;
import baron.jakub.model.ProcessorFile;

public class OptimizedDataLoader extends DataLoader {
	private int counter;
	private int maxThreads;
	// private Hashtable<Integer, Hashtable<Integer, Hashtable<Integer,
	// Double>>> optParticles;
	private ExecutorService taskExecutor;
	private long startTime;

	public OptimizedDataLoader(ViewModifier vm, String series) {
		super(vm, series);
		counter = 0;
	}

	@Override
	protected Void doInBackground() throws Exception {
		int type = Parameters.getDataType();
		int maxLoc = Parameters.getMaxlocal();
		ProcessorFile[] fileList = vm.getListOfFiles(getSeries());

		if (vm.getMaxThreads() <= Runtime.getRuntime().availableProcessors()) {
			maxThreads = vm.getMaxThreads();
			vm.addLogMessage("Assigning: " + maxThreads
					+ " threads to do the job", Color.BLACK);
		} else {
			maxThreads = Runtime.getRuntime().availableProcessors();
			vm.addLogMessage("Not enough cores, assigning all(" + maxThreads
					+ ") of the availables threads to do the work", Color.RED);
		}
		setProgress(0);
		counter = 0;
		vm.addLogMessage("Start reading files", Color.BLACK);

		startTime = System.nanoTime();
		taskExecutor = Executors.newFixedThreadPool(maxThreads);
		for(ProcessorFile proc: fileList){
			taskExecutor.submit(new Runnable() {
				@Override
				public void run() {
					readFile(proc, type, maxLoc);
				}
			});
		}
		taskExecutor.shutdown();
		// vm.addLogMessage(
		// "Time elapsed: "
		// + Double.toString((System.nanoTime() - startTime) / 1000000000.0)
		// + " s", Color.BLACK);
		setLoaded(true);
		return null;
	}

	@Override
	public double[][][] getParticles() {
		if (!isLoaded()) {
			loadData();
		}
		return particles;
	}

	@Override
	public void loadData() {
		defaultParticles();

		setMaxVal(Double.MIN_VALUE);
		setMinVal(Double.MAX_VALUE);

		try {
			doInBackground();
		} catch (Exception e) {
			vm.addLogMessage(e.toString(), Color.RED);
		}

	}

	@Override
	protected void process(List<String> chunks) {
		// here is called when you call publish in doInBackground, this is
		// executed in EDT
		if (chunks == null || chunks.isEmpty())
			return;
		firePropertyChange("message", null, chunks.get(0));
	}

	@Override
	protected void readFile(ProcessorFile proc, int dataType, int maxLoc) {

		// System.out.println(filename);

		// TODO TUTAJ PORPAWIC
		int perLev = vm.getProcRows() * vm.getProcColumns();// how many levels,
															// so we calc
															// rows*cols to get
															// the number per
															// level
		int perCol = vm.getProcRows(); // how many are they in the column, so we
										// need how many rows
		int perRow = vm.getProcColumns(); // how many are they in the row, so we
		int procNo = proc.number; // need how many cols
		// TODO TUTAJ PORPAWIC
		int level = procNo / perLev; // z coords
		int col = procNo % perCol; // x coords
		int row = (procNo % perLev) / perRow; // y coordse

		String line;
		String valSpacer = Parameters.getValueSpacer();
		String filename = proc.filename;

		int x = 0, y = 0, z = 0;
		int xx = 0, yy = 0, zz = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (z = 0; z < maxLoc; ++z) {
				zz = level * maxLoc + z;
				for (y = 0; y < maxLoc; ++y) {
					yy = row * maxLoc + y;
					for (x = 0; x < maxLoc; ++x) {
						xx = col * maxLoc + x;
						if ((line = br.readLine()) != null) {
							String[] values = line.split(valSpacer);
							double val = Double.parseDouble(values[dataType]);
							minMax(val);
							particles[zz][yy][xx] = val;
						}
					}
				}
			}
		} catch (Exception e) {
			vm.addLogMessage(e.getMessage(), Color.RED);
			vm.addLogMessage("X: " + xx + " Y: " + yy + " Z: " + zz, Color.RED);
			vm.addLogMessage("Line: " + (z * maxLoc * maxLoc + y * maxLoc + x),
					Color.RED);
			vm.addLogMessage(filename, Color.RED);
		}
		setProgress(++counter);
		publish("Loaded "
				+ Integer.toString(counter)
				+ " files - timestep: "
				+ getSeries()
				+ ". Time elapsed: "
				+ Double.toString((System.nanoTime() - startTime) / 1000000000.0)
				+ " s");
	}
}
