package baron.jakub.accesstime;

import java.util.Random;

public class AccessTest implements IAccessTest {

	protected int size;
	protected int oneDSize;
	protected int howManyTimes;

	protected double[] oneDimensional;
	protected double[][][] threeDimensional;

	
	
	public AccessTest(int _size, int times) {
		this.size = _size;
		this.howManyTimes = times;
		this.oneDSize = size * size * size;
		oneDimensional = new double[size * size * size];
		threeDimensional = new double[size][size][size];
		Random rn = new Random(0);
		for (int i = 0; i < oneDSize; ++i) {
			oneDimensional[i] = rn.nextDouble();
		}
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				for (int k = 0; k < size; ++k) {
					threeDimensional[i][j][k] = rn.nextDouble();
				}
			}
		}
	}

	@Override
	public void runTests() {
		// TODO Auto-generated method stub
		reading();
	}

	public void reading() {
		
	}

}
