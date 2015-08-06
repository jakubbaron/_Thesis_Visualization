package baron.jakub.arraytesting;

import java.util.Arrays;

public class ArrayTests implements IArrayTests {
	protected int size;
	protected int oneDSize;
	protected int howManyTimes;

	protected double[] oneDimensional;
	protected double[][][] threeDimensional;

	public ArrayTests(int _size, int times) {
		this.size = _size;
		this.howManyTimes = times;
		this.oneDSize = size * size * size;
		oneDimensional = new double[size * size * size];
		threeDimensional = new double[size][size][size];
		Arrays.fill(oneDimensional, 0);
		for (double[][] a : threeDimensional) {
			for (double[] b : a) {
				Arrays.fill(b, 0);
			}
		}
	}

	@Override
	public void runTests() {
		// TODO Auto-generated method stub
		writing();
		reading();
	}

	public void reading() {
		// TODO Auto-generated method stub

	}

	public void writing() {
		// TODO Auto-generated method stub

	}

}
