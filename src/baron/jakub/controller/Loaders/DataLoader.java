package baron.jakub.controller.Loaders;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

import javax.swing.SwingWorker;

import baron.jakub.controller.ViewModifier;

public abstract class DataLoader extends SwingWorker<Void, String> implements
		IDataLoader {

	private boolean loaded = false;
	protected int loadedData;
	private double maxVal = 1;
	private double minVal = 0;
	protected double[][][] particles;
	private int time;
	protected ViewModifier vm;

	public DataLoader(ViewModifier vm, int time) {
		this.vm = vm;
		this.time = time;
		setMaxVal(Double.MIN_VALUE);
		setMinVal(Double.MAX_VALUE);
	}

	@Override
	protected Void doInBackground() throws Exception {
		// Parameters.setFileAppendix("2");

		setProgress(0);
		vm.addLogMessage("Start reading files", Color.BLACK);
		setProgress(100);
		return null;
	}

	/**
	 * @return the maxVal
	 */
	public double getMaxVal() {
		return maxVal;
	}

	/**
	 * @return the minVal
	 */
	public double getMinVal() {
		return minVal;
	}

	/**
	 * @return the particles
	 */
	@Override
	public double[][][] getParticles() {
		if (!isLoaded()) {
			loadData();
		}
		return particles;
	}

	@Override
	public double[][][] getRandomData() {
		if (!isLoaded()) {
			defaultParticles();
			Random rn = new Random();
			setMaxVal(Double.MIN_VALUE);
			setMinVal(Double.MAX_VALUE);
			int cubeSize = vm.getCubeSize();
			for (int i = 0; i < cubeSize; ++i) {
				for (int j = 0; j < cubeSize; ++j) {
					for (int k = 0; k < cubeSize; ++k) {
						double val = rn.nextDouble();
						minMax(val);
						particles[i][j][k] = val;
					}
				}
			}
			setLoaded(true);
		}
		return particles;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub

	}
	// TODO TUTAJ PORPAWIC, chyba! sprawdzic wspolrzedne!
	protected void defaultParticles() {
		int perLev = vm.getProcLevels();// how many levels

		int perCol = vm.getProcRows(); // how many are they in the column, so we
		// need how many rows
		int perRow = vm.getProcColumns(); // how many are they in the row, so we
		// need how many cols
		int maxLoc = vm.getMaxlocal();

		int size = vm.getCubeSize();
		if (perLev * maxLoc > size || perCol * maxLoc > size
				|| perRow * maxLoc > size) {
			vm.addLogMessage("Invalid sizes, check config", Color.RED);
			vm.addLogMessage("col: " + perRow + " row: " + perCol + " Lev: "
					+ perLev + "maxLoc: " + maxLoc + " size: " + perLev,
					Color.RED);
		}
		particles = new double[maxLoc * perLev][maxLoc * perCol][maxLoc
				* perRow];
		for (double[][] a : particles) {
			for (double[] b : a) {
				Arrays.fill(b, Double.MIN_VALUE);
			}
		}
		setLoaded(false);
	}

	@Override
	public void minMax(double val) {
		if (val > maxVal)
			maxVal = val;
		if (val < minVal)
			minVal = val;
	}

	protected void readFile(int procNo, int dataType, int maxLoc) {

	}

	/**
	 * @param loaded
	 *            the loaded to set
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public void setLoadedData(int d) {
		loadedData = d;
	}

	@Override
	public void setMaxVal(double max) {
		this.maxVal = max;
	}

	@Override
	public void setMinVal(double min) {
		this.minVal = min;
	}
}
