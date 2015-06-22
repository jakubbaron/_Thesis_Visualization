package baron.jakub.model;

public class InversedColorValues implements IColorValues {

	@Override
	public double getValue(double val, double min, double max) {
		// TODO Auto-generated method stub
		return max - (val - min);
	}

}
