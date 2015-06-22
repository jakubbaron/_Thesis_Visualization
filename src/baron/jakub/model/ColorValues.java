package baron.jakub.model;

public class ColorValues implements IColorValues {

	@Override
	public double getValue(double val, double min, double max) {
		return val - min;
	}

}
