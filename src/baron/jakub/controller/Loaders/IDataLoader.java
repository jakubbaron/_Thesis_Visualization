package baron.jakub.controller.Loaders;

public interface IDataLoader {

	/**
	 * @return the maxVal
	 */
	public double getMaxVal();

	/**
	 * @return the minVal
	 */
	public double getMinVal();

	/**
	 * @return the particles
	 */
	public double[][][] getParticles();

	public double[][][] getRandomData();

	public String getSeries();

	/**
	 * @return the loaded
	 */
	public boolean isLoaded();

	public void loadData();

	public void minMax(double val);

	/**
	 * @param loaded
	 *            the loaded to set
	 */
	public void setLoaded(boolean loaded);

	public void setLoadedData(int d);

	public void setMaxVal(double max);

	public void setMinVal(double min);
}
