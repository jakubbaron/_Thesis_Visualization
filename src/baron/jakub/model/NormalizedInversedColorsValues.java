package baron.jakub.model;

public class NormalizedInversedColorsValues implements IColorValues {

	@Override
	public double getValue(double val, double min, double max) {
		return 1.0 - ((val - min) / (max - min));
	}
}
