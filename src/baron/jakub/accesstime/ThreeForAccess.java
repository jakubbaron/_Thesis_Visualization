package baron.jakub.accesstime;

public class ThreeForAccess extends AccessTest {

	private double preventOpt = 0;

	public ThreeForAccess(int _size, int times) {
		super(_size, times);
		System.out.println("ThreeForAccess");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reading() {
		System.out.println("Reading three fors");
		double time;
		// System.out.println(oneDimensional.length);
		int twoSize = size * size;
		long startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					for (int k = 0; k < size; ++k) {
						preventOpt += oneDimensional[i * twoSize + j * size + k];
					}
				}
			}
		}
		time = (System.nanoTime() - startTime) / 1000000000.0
				/ (howManyTimes + 0.0);
		System.out.println("3for 1D: (" + oneDSize + ") " + time);
		// System.out.println("Per one: " + time/oneDSize);
//		System.out.println(preventOpt);
		preventOpt = 0;

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
		time = (System.nanoTime() - startTime) / 1000000000.0
				/ (howManyTimes + 0.0);
		System.out.println("3for 3D: (" + size + ";" + size + ";" + size + ") "
				+ time);
		System.out.println(preventOpt + " \n");
		// System.out.println("Per one: " + time/oneDSize);
	}

}
