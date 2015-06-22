package baron.jakub.view;

import baron.jakub.controller.Loaders.IDataLoader;

public interface IVisualization {

	public void setMax(double max);

	public void setMin(double min);

	public void setScale(double scale);

	public double getMax();

	public double getMin();

	public double getScale();

	public void changeDL(IDataLoader dl);

	void init();

}
