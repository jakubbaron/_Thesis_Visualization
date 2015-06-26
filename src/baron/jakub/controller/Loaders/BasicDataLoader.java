package baron.jakub.controller.Loaders;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import baron.jakub.controller.ViewModifier;
import baron.jakub.model.Parameters;
import baron.jakub.model.ProcessorFile;

public class BasicDataLoader extends DataLoader {
	private int counter = 0;

	public BasicDataLoader(ViewModifier vm, String time) {
		super(vm, time);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Void doInBackground() throws Exception {
		counter = 0;
		int type = Parameters.getDataType();
		int maxLoc = Parameters.getMaxlocal();
		long startTime = System.nanoTime();
		Parameters.setFileAppendix("2");

		setProgress(0);
		vm.addLogMessage("Start reading files", Color.BLACK);
		ProcessorFile[] fileList = vm.getListOfFiles(getSeries());
		for (int i = 0; i < fileList.length; ++i) {
			readFile(fileList[i], type, maxLoc);

		}
		vm.addLogMessage(
				"Time elapsed: "
						+ Double.toString((System.nanoTime() - startTime) / 1000000000.0)
						+ " s", Color.BLACK);
		// setProgress(100);
		vm.addLogMessage("Finished reading files", Color.BLACK);
		setLoaded(true);
		return null;
	}

	@Override
	public void loadData() {
		int size = Parameters.getCubeSize();
		particles = new double[size][size][size];
		for (double[][] a : particles) {
			for (double[] b : a) {
				Arrays.fill(b, Double.MIN_VALUE);
			}
		}
		try {
			doInBackground();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void readFile(ProcessorFile proc, int dataType, int maxLoc) {
		int procNo = proc.number;
		String filename = proc.filename;

		System.out.println(filename);

		int level = procNo / 16; // z coords
		int col = procNo % 4; // x coords
		int row = (procNo % 16) / 4; // y coords
		System.out.println(level + " " + col + " " + row);

		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			for (int z = 0; z < maxLoc; ++z) {
				int zz = level * maxLoc + z;

				for (int y = 0; y < maxLoc; ++y) {
					int yy = row * maxLoc + y;

					for (int x = 0; x < maxLoc; ++x) {
						int xx = col * maxLoc + x;

						if ((line = br.readLine()) != null) {
							String[] values = line.split(Parameters
									.getValueSpacer());

							double val = Double.parseDouble(values[dataType]);
							minMax(val);
							particles[zz][yy][xx] = val;
						}
					}
				}
			}
			setLoaded(true);
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e.getLocalizedMessage());

		}
		setProgress(++counter);
		System.gc();

	}

}
