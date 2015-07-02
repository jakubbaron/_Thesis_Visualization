package baron.jakub.timetesting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import baron.jakub.model.Parameters;

public class FileReader1DArray extends FileTester {
	private void withCalculatingCoords() {
		System.out
				.println("With calculating coords, initializing vars outside loop");
		String line;
		String v = Parameters.getValueSpacer();
		Random rnd = new Random(12345);

		int times[] = new int[] { 10, 80 };
		int rows = 4, cols = 4, oneLoc = 96, perLev = rows * cols;

		int x, y, z, xx = 0, yy = 0, zz = 0;
		int twoLoc = oneLoc * oneLoc;
		int threeLoc = twoLoc * oneLoc;
		int cubeSize = 384;
		double[] particles = new double[threeLoc * 64];

		long start;
		for (int oo = 0; oo < howManyTimesExperiment; ++oo) {
			start = System.nanoTime();
			for (int o = 0; o < howManyFiles; ++o) {
				int t = rnd.nextBoolean() ? times[0] : times[1];
				int procNo = rnd.nextInt(64);
				String filename = "C:\\time" + t + "\\proc"
						+ String.format("%03d%02d", procNo, t) + "2.res";
				int level = procNo / perLev; // z coords
				int col = (procNo / rows) % cols;
				int row = procNo % rows;
				try (BufferedReader br = new BufferedReader(new FileReader(
						filename))) {
					for (z = 0; z < oneLoc; ++z) {
						zz = level * oneLoc + z;
						for (y = 0; y < oneLoc; ++y) {
							yy = col * oneLoc + y;
							for (x = 0; x < oneLoc; ++x) {
								xx = row * oneLoc + x;
								if ((line = br.readLine()) != null) {
									String[] values = line.split(v);
									double e = Double.parseDouble(values[1]);
									minMax(e);
									particles[xx + cubeSize
											* (yy + cubeSize * zz)] = e;
								}
							}
						}
					}
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			double time = (System.nanoTime() - start) / 1000000000.0
					/ (howManyFiles + 0.0);
			System.out.println("3x for: " + time + "s");
			
			waitToRunNextTest();
			
			start = System.nanoTime();
			for (int o = 0; o < howManyFiles; ++o) {
				int t = rnd.nextBoolean() ? times[0] : times[1];
				int procNo = rnd.nextInt(64);
				String filename = "C:\\time" + t + "\\proc"
						+ String.format("%03d%02d", procNo, t) + "2.res";
				try (BufferedReader br = new BufferedReader(new FileReader(
						filename))) {

					for (x = procNo * threeLoc; x < (procNo + 1) * threeLoc; ++x) {
						if ((line = br.readLine()) != null) {
							String[] values = line.split(v);
							double e = Double.parseDouble(values[1]);
							minMax(e);
							particles[x] = e;
						}
					}
				} catch (FileNotFoundException e) {
					//
					e.printStackTrace();
				} catch (IOException e) {
					//
					e.printStackTrace();
				}

			}
			time = (System.nanoTime() - start) / 1000000000.0
					/ (howManyFiles + 0.0);
			System.out.println("1x for: " + time + "s");
			waitToRunNextTest();
		}
	}

	private void withLocalCalculatingCoords() {
		System.out
				.println("With calculating coords, vars initialized in loops");
		String line;
		String v = Parameters.getValueSpacer();
		Random rnd = new Random(12345);

		int times[] = new int[] { 10, 80 };
		int rows = 4, cols = 4, oneLoc = 96, perLev = rows * cols;

		// int x, y, z, xx = 0, yy = 0, zz = 0;
		int twoLoc = oneLoc * oneLoc;
		int threeLoc = twoLoc * oneLoc;
		int cubeSize = 384;
		double[] particles = new double[threeLoc];
		long start;
		
		for (int oo = 0; oo < howManyTimesExperiment; ++oo) {
			start = System.nanoTime();
			for (int o = 0; o < howManyFiles; ++o) {
				int t = rnd.nextBoolean() ? times[0] : times[1];
				int procNo = rnd.nextInt(64);
				String filename = "C:\\time" + t + "\\proc"
						+ String.format("%03d%02d", procNo, t) + "2.res";
				int level = procNo / perLev; // z coords
				int col = (procNo / rows) % cols;
				int row = procNo % rows;
				try (BufferedReader br = new BufferedReader(new FileReader(
						filename))) {
					for (int z = 0; z < oneLoc; ++z) {
						int zz = level * oneLoc + z;
						for (int y = 0; y < oneLoc; ++y) {
							int yy = col * oneLoc + y;
							for (int x = 0; x < oneLoc; ++x) {
								int xx = row * oneLoc + x;
								if ((line = br.readLine()) != null) {
									String[] values = line.split(v);
									double e = Double.parseDouble(values[1]);
									minMax(e);
									particles[xx + cubeSize
											* (yy + cubeSize * zz)] = e;
								}
							}
						}
					}
				} catch (FileNotFoundException e) {
					//
					e.printStackTrace();
				} catch (IOException e) {
					//
					e.printStackTrace();
				}
			}
			double time = (System.nanoTime() - start) / 1000000000.0
					/ (howManyFiles + 0.0);
			System.out.println("3x for: " + time + "s");

			waitToRunNextTest();
			
			start = System.nanoTime();
			for (int o = 0; o < howManyFiles; ++o) {
				int t = rnd.nextBoolean() ? times[0] : times[1];
				int procNo = rnd.nextInt(64);
				String filename = "C:\\time" + t + "\\proc"
						+ String.format("%03d%02d", procNo, t) + "2.res";
				try (BufferedReader br = new BufferedReader(new FileReader(
						filename))) {

					for (int x = 0; x < threeLoc; ++x) {
						if ((line = br.readLine()) != null) {
							String[] values = line.split(v);
							double e = Double.parseDouble(values[1]);
							int addr = procNo * threeLoc + x;
							minMax(e);
							particles[addr] = e;
						}
					}
				} catch (FileNotFoundException e) {
					//
					e.printStackTrace();
				} catch (IOException e) {
					//
					e.printStackTrace();
				}

			}
			time = (System.nanoTime() - start) / 1000000000.0
					/ (howManyFiles + 0.0);
			System.out.println("1x for: " + time + "s");
			waitToRunNextTest();
		}
	}

	private void withoutCalculatingCoords() {
		System.out.println("Without calculating coords");
		String line;
		String v = Parameters.getValueSpacer();
		Random rnd = new Random(12345);

		int times[] = new int[] { 10, 80 };
		int oneLoc = 96;
		int x, y, z;
		int twoLoc = oneLoc * oneLoc;
		int threeLoc = twoLoc * oneLoc;
		double[] particles = new double[threeLoc];
		long start;
		for (int oo = 0; oo < howManyTimesExperiment; ++oo) {
			start = System.nanoTime();
			for (int o = 0; o < howManyFiles; ++o) {
				int t = rnd.nextBoolean() ? times[0] : times[1];
				int procNo = rnd.nextInt(64);
				String filename = "C:\\time" + t + "\\proc"
						+ String.format("%03d%02d", procNo, t) + "2.res";
				try (BufferedReader br = new BufferedReader(new FileReader(
						filename))) {
					for (z = 0; z < oneLoc; ++z) {
						for (y = 0; y < oneLoc; ++y) {
							for (x = 0; x < oneLoc; ++x) {
								if ((line = br.readLine()) != null) {
									String[] values = line.split(v);
									double e = Double.parseDouble(values[1]);
									minMax(e);
									particles[0] = e;
								}
							}
						}
					}
				} catch (FileNotFoundException e) {
					//
					e.printStackTrace();
				} catch (IOException e) {
					//
					e.printStackTrace();
				}
			}
			double time = (System.nanoTime() - start) / 1000000000.0
					/ (howManyFiles + 0.0);
			System.out.println("3x for: " + time + "s");

			waitToRunNextTest();
			
			start = System.nanoTime();
			for (int o = 0; o < howManyFiles; ++o) {
				int t = rnd.nextBoolean() ? times[0] : times[1];
				int procNo = rnd.nextInt(64);
				String filename = "C:\\time" + t + "\\proc"
						+ String.format("%03d%02d", procNo, t) + "2.res";
				try (BufferedReader br = new BufferedReader(new FileReader(
						filename))) {

					for (x = 0; x < threeLoc; ++x) {
						if ((line = br.readLine()) != null) {
							String[] values = line.split(v);
							double e = Double.parseDouble(values[1]);
							minMax(e);
							particles[0] = e;
						}
					}
				} catch (FileNotFoundException e) {
					//
					e.printStackTrace();
				} catch (IOException e) {
					//
					e.printStackTrace();
				}

			}
			time = (System.nanoTime() - start) / 1000000000.0
					/ (howManyFiles + 0.0);
			System.out.println("1x for: " + time + "s");
			waitToRunNextTest();
		}
	}

	public FileReader1DArray(int howManyFiles, int howManyTimesExperiment) {
		super(howManyFiles,howManyTimesExperiment);
	}

	public void runTests() {
		System.out.println("FileReader3ForVs1For: 1DArray1D");
		withCalculatingCoords();
		withLocalCalculatingCoords();
		withoutCalculatingCoords();
	}
}
