package baron.jakub.accesstime;

public class OneForAccess extends AccessTest {
	private double preventOpt = 0;

	public OneForAccess(int _size, int times) {
		super(_size, times);
		// TODO Auto-generated constructor stub
		System.out.println("OneForAccess");
	}

	@Override
	public void reading() {
		System.out.println("Reading one for");
		double time;
		// System.out.println(oneDimensional.length);
		long startTime = System.nanoTime();

		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < oneDSize; ++i) {
				preventOpt += oneDimensional[i];
			}

		}
		time = (System.nanoTime() - startTime) / 1000000000.0
				/ (howManyTimes + 0.0);
		System.out.println("1for 1D: (" + oneDSize + ") " + time);
		// System.out.println("Per one: " + time/oneDSize);
//		System.out.println(preventOpt);
		preventOpt = 0;
		int twoSize = size * size;
		startTime = System.nanoTime();
		for (int o = 0; o < howManyTimes; ++o) {
			for (int i = 0; i < oneDSize; ++i) {
				preventOpt += threeDimensional[i / twoSize][(i / size) % size][i
						% size];
			}
		}
		time = (System.nanoTime() - startTime) / 1000000000.0
				/ (howManyTimes + 0.0);
		System.out.println("1for 3D: (" + size + ";" + size + ";" + size + ") "
				+ time);
		System.out.println(preventOpt + " \n");
		// System.out.println("Per one: " + time/oneDSize);
	}

}
