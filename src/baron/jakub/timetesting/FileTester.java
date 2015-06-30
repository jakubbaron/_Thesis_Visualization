package baron.jakub.timetesting;

public abstract class FileTester implements IFileTester {
	protected int howManyFiles;
	protected int howManyTimesExperiment;
	protected double minVal = Double.MAX_VALUE;
	protected double maxVal = Double.MIN_VALUE;

	protected void minMax(double val) {
		if (val > maxVal)
			maxVal = val;
		if (val < minVal)
			minVal = val;
	}

	public FileTester(int files, int experiment) {
		this.howManyFiles = files;
		this.howManyTimesExperiment = experiment;
	}
}