package baron.jakub.arraytesting;

import java.util.Random;

public class ArraysWriteReadRandom extends ArrayTests {

	private double preventOpt = 0;

	public ArraysWriteReadRandom(int _size, int times) {
		super(_size, times);
	}

	@SuppressWarnings("unused")
	@Override
	public void writing() {
		System.out.println("Writing");
		Random rn = new Random();
		double time;
		long startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < oneDSize; ++i) {
				int idx1 = rn.nextInt(oneDSize);
				int idx2 = rn.nextInt(oneDSize);
				int idx3 = rn.nextInt(oneDSize);
				oneDimensional[idx3] = rn.nextDouble();
			}
		}
		time = (System.nanoTime() - startTime) / 1000000000.0
				/ (howManyTimes + 0.0);
		System.out.println("1D: (" + oneDSize + ") " + time);

		startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					for (int k = 0; k < size; ++k) {
						int idx1 = rn.nextInt(size);
						int idx2 = rn.nextInt(size);
						int idx3 = rn.nextInt(size);
						threeDimensional[idx1][idx2][idx3] = rn.nextDouble();
					}
				}
			}
		}
		time = (System.nanoTime() - startTime) / 1000000000.0
				/ (howManyTimes + 0.0);
		System.out.println("3D: (" + size + ";" + size + ";" + size + ") "
				+ time);

	}

	@SuppressWarnings("unused")
	@Override
	public void reading() {
		System.out.println("Reading");
		Random rn = new Random();
		long startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < oneDSize; ++i) {
				int idx1 = rn.nextInt(oneDSize);
				int idx2 = rn.nextInt(oneDSize);
				int idx3 = rn.nextInt(oneDSize);
				preventOpt += oneDimensional[idx1];
			}
		}
		double time;
		time = (System.nanoTime() - startTime) / 1000000000.0
				/ (howManyTimes + 0.0);
		System.out.println("1D: (" + oneDSize + ") " + time);
		System.out.println(preventOpt);
		startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					for (int k = 0; k < size; ++k) {
						int idx1 = rn.nextInt(size);
						int idx2 = rn.nextInt(size);
						int idx3 = rn.nextInt(size);
						preventOpt += threeDimensional[idx1][idx2][idx3];
					}
				}
			}
		}
		time = (System.nanoTime() - startTime) / 1000000000.0
				/ (howManyTimes + 0.0);
		System.out.println("3D: (" + size + ";" + size + ";" + size + ") "
				+ time);
		System.out.println(preventOpt);
	}

	@Override
	public void runTests() {
		System.out.println("\nArraysWriteReadRandom size: " + size);
		super.runTests();
	}

}
