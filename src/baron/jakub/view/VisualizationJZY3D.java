package baron.jakub.view;

import java.util.Hashtable;
import java.util.List;

import java.util.Vector;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.colors.Color;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.TicToc;

import org.jzy3d.plot3d.primitives.AbstractDrawable;

import org.jzy3d.plot3d.primitives.Point;

import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Graph;

import baron.jakub.controller.ViewModifier;
import baron.jakub.controller.Loaders.IDataLoader;
import baron.jakub.model.ColorValues;
import baron.jakub.model.Filter;
import baron.jakub.model.IColorValues;
import baron.jakub.model.InversedColorValues;
import baron.jakub.model.NormalizedColorValues;
import baron.jakub.model.NormalizedInversedColorsValues;
import baron.jakub.model.Parameters;

public class VisualizationJZY3D implements IVisualization {

	protected String canvasType = "awt";
	protected Chart chart;
	final List<AbstractDrawable> memory = new Vector<AbstractDrawable>();

	public static int MAX_DRAWABLE = 1000;

	private IDataLoader dl;
	private IColorValues fun;
	private ViewModifier vm;
	private Thread t;
	private double scale;
	private double max;
	private double min;

	private double[][][] particles;
	private Coord3d[] points;
	private Color[] colors;
	private Hashtable<Integer, Point> apoints;

	public VisualizationJZY3D(IDataLoader _dl, ViewModifier vm) {
		this.dl = _dl;
		this.vm = vm;
		setMinMaxScale();
		init();
	}

	private int xyzTo1D(int x, int y, int z, int level) {
		return x + y * level + z * level * level;
	}

	private void setMinMaxScale() {
		setMax(dl.getMaxVal());
		setMin(dl.getMinVal());
		setScale(Math.abs(getMax() - getMin()));
	}

	@Override
	public void changeDL(IDataLoader dl) {
		// TODO Auto-generated method stub
		this.dl = dl;
	}

	public void dispose() {
		if (t != null)
			t.interrupt();
		points = null;
		colors = null;
		chart = null;
		apoints = null;
		System.gc();

	}

	public String getCanvasType() {
		return canvasType;
	}

	public Chart getChart() {
		return chart;
	}

	private IColorValues getColorValuesFun() {
		IColorValues fun = null;
		if (Parameters.isNormalized() && Parameters.isInversed())
			fun = new NormalizedInversedColorsValues();
		else if (Parameters.isNormalized())
			fun = new NormalizedColorValues();
		else if (Parameters.isInversed())
			fun = new InversedColorValues();
		else
			fun = new ColorValues();
		return fun;
	}

	@Override
	public double getMax() {
		return max;
	}

	@Override
	public double getMin() {
		return min;
	}

	public String getPitch() {
		return "";
	}

	@Override
	public double getScale() {
		return scale;
	}

	@Override
	public void init() {
		points = new Coord3d[] { new Coord3d(1, 1, 1) };
		colors = new Color[] { new Color(0, 1, 0, .5f) };
		apoints = new Hashtable<Integer, Point>();

		// long startTime = System.nanoTime();
		particles = dl.getParticles();
		chart = new Chart(Quality.Fastest, "awt");

		chart.addController(new CameraMouseController());
		for (int i = 0; i <= 384; i = i + 96)
			chart.getScene().getGraph()
					.add(new Point(new Coord3d(i, i, i), Color.BLACK));
		startRefresher();
		// Scatter scatter = new Scatter(
		// points.toArray(new Coord3d[points.size()]),
		// colors.toArray(new Color[colors.size()]));

		// chart.getScene().getGraph().add(scatter);

		// System.out.println("Time elapsed: "
		// + Double.toString((System.nanoTime() - startTime) / 1000000.0)
		// + " s");
	}

	public boolean isInitialized() {
		return chart != null;
	}

	@Override
	public void setMax(double max) {
		this.max = max;
	}

	@Override
	public void setMin(double min) {
		this.min = min;
	}

	@Override
	public void setScale(double scale) {
		this.scale = scale;

	}

	private void alterDataToPlot(TicToc tt) {
		int cubeSize = vm.getCubeSize();
		Graph graph = chart.getScene().getGraph();
		Scatter scatter = new Scatter(new Coord3d[] { new Coord3d(1, 1, 1) },
				new Color[] { new Color(0, 0, 1, 1) });
		graph.add(scatter);
		while (true) {

			fun = getColorValuesFun();
			particles = dl.getParticles();
			setMinMaxScale();
			vm.updateMinMax();
			double[] valueLimits = Filter.getValues();
			boolean changed = false;
			tt.tic();
			for (int z = Filter.getzCoords()[0]; z <= Filter.getzCoords()[1]; ++z) {
				for (int y = Filter.getyCoords()[0]; y <= Filter.getyCoords()[1]; ++y) {
					for (int x = Filter.getxCoords()[0]; x <= Filter
							.getxCoords()[1]; ++x) {
						double par = particles[z][y][x];
						if (par != Double.MIN_VALUE) {
							int coords = xyzTo1D(x, y, z, cubeSize);
							if (par >= valueLimits[0] && par <= valueLimits[1]) {
								if (!apoints.containsKey(coords)) {
									float val = (float) fun.getValue(par,
											getMin(), getMax());
									Point p = new Point(new Coord3d(x, y, z),
											new Color(val, 0, 1f - val, 1.0f));
									apoints.put(coords, p);
									changed = true;
								}
							} else {
								if (apoints.containsKey(coords)) {
									apoints.remove(coords);
									changed = true;
								}
							}
						}
					}
				}
			}
			for (int z = 0; z < cubeSize; ++z) {
				for (int y = 0; y < cubeSize; ++y) {
					for (int x = 0; x < Filter.getxCoords()[0]; ++x) {
						int coords = xyzTo1D(x, y, z, cubeSize);
						if (apoints.containsKey(coords)) {
							// System.out.println("removing " + coords);
							apoints.remove(coords);
							changed = true;
							// System.out.println("x1");
						}
					}
					for (int x = Filter.getxCoords()[1] + 1; x < cubeSize; ++x) {
						int coords = xyzTo1D(x, y, z, cubeSize);
						if (apoints.containsKey(coords)) {
							// System.out.println("removing " + coords);
							apoints.remove(coords);
							changed = true;
							// System.out.println("x2 " + coords);
						}
					}
					for (int x = 0; x < Filter.getyCoords()[0]; ++x) {
						int coords = xyzTo1D(y, x, z, cubeSize);
						if (apoints.containsKey(coords)) {
							// System.out.println("removing " + coords);
							apoints.remove(coords);
							changed = true;
							// System.out.println("y1");
						}
					}
					for (int x = Filter.getyCoords()[1] + 1; x < cubeSize; ++x) {
						int coords = xyzTo1D(y, x, z, cubeSize);
						if (apoints.containsKey(coords)) {
							// System.out.println("removing " + coords);
							apoints.remove(coords);
							changed = true;
							// System.out.println("y2" + coords);
						}
					}
					for (int x = 0; x < Filter.getzCoords()[0]; ++x) {
						int coords = xyzTo1D(y, z, x, cubeSize);
						if (apoints.containsKey(coords)) {
							// System.out.println("removing " + coords);
							apoints.remove(coords);
							changed = true;
							// System.out.println("z1");
						}
					}
					for (int x = Filter.getzCoords()[1] + 1; x < cubeSize; ++x) {
						int coords = xyzTo1D(y, z, x, cubeSize);
						if (apoints.containsKey(coords)) {
							// System.out.println("removing " + coords);
							apoints.remove(coords);
							changed = true;
							// System.out.println("z2" + coords);
						}
					}
				}
			}
			if (changed) {
				graph.remove(scatter);
				points = new Coord3d[apoints.size()];
				colors = new Color[apoints.size()];
				int i = 0;
				for (Point p : apoints.values()) {
					points[i] = p.xyz;
					colors[i++] = p.rgb;
				}
				scatter = new Scatter(points, colors);
				graph.add(scatter);
				changed = false;
				tt.toc();
				vm.addLogMessage("Graph updated in: " + tt.elapsedSecond()
						+ "s", java.awt.Color.BLACK);
				vm.updateFPS(1 / tt.elapsedSecond());
			} else {
				tt.toc();
				vm.updateFPS(1 / tt.elapsedSecond());
			}

		}
	}

	private void startRefresher() {
		t = new Thread() {
			TicToc tt = new TicToc();

			@Override
			public void run() {
				alterDataToPlot(tt);
			}
		};
		t.start();
	}
}
