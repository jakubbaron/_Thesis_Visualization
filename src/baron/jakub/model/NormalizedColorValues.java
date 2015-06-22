package baron.jakub.model;

public class NormalizedColorValues implements IColorValues {

	@Override
	public double getValue(double val, double min, double max) {
		return (val - min) / (max-min);
	}

}
