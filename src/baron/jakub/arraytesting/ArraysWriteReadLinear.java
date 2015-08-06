package baron.jakub.arraytesting;

import java.util.Random;

public class ArraysWriteReadLinear extends ArrayTests {

	private double preventOpt = 0;
	public ArraysWriteReadLinear(int _size, int times) {
		super(_size, times);
		
	}

	@Override
	public void writing() {
		System.out.println("Writing");
		Random rn = new Random();
		double time;
		long startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < oneDSize; ++i) {
				oneDimensional[i] = rn.nextDouble();
			}
		}
		time = (System.nanoTime() - startTime)/ 1000000000.0/ (howManyTimes + 0.0);
		System.out.println("1D: ("+oneDSize+") "
				+ time);
//		System.out.println("Per one: " + time/oneDSize);

		startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					for (int k = 0; k < size; ++k) {
						threeDimensional[i][j][k] = rn.nextDouble();
					}
				}
			}
		}
		time = (System.nanoTime() - startTime)/ 1000000000.0/ (howManyTimes + 0.0);
		System.out.println("3D: ("+size+";"+size+";"+size+") "
				+ time);
//		System.out.println("Per one: " + time/oneDSize);

	}

	// @SuppressWarnings("unused")
	@Override
	public void reading() {
		System.out.println("Reading");
		double time;
		System.out.println(oneDimensional.length);
		long startTime = System.nanoTime();
		
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < oneDSize; ++i) {
//			for(int i=oneDSize-1; i>=0;--i){
				preventOpt += oneDimensional[i];
//				e += e;
			}

		}
		time = (System.nanoTime() - startTime)/ 1000000000.0/ (howManyTimes + 0.0);
		System.out.println("1D: ("+oneDSize+") "
				+ time);
//		System.out.println("Per one: " + time/oneDSize);
		System.out.println(preventOpt);
		preventOpt =0;
		startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					for (int k = 0; k < size; ++k) {
						preventOpt += threeDimensional[i][j][k];
					}
				}
			}
		}
		time = (System.nanoTime() - startTime)/ 1000000000.0/ (howManyTimes + 0.0);
		System.out.println("3D: ("+size+";"+size+";"+size+") "
				+ time);
		System.out.println(preventOpt);
//		System.out.println("Per one: " + time/oneDSize);
	}

	@Override
	public void runTests() {
		System.out.println("\nArraysWriteReadLinear size: " + size);
		// super.runTests();
		writing();
		reading();
	}

}
