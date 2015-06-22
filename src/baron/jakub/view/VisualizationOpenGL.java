package baron.jakub.view;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.FPSAnimator;

import com.jogamp.opengl.util.awt.TextRenderer;

import baron.jakub.controller.ViewModifier;
import baron.jakub.controller.Loaders.IDataLoader;
import baron.jakub.model.*;

public class VisualizationOpenGL extends GLCanvas implements GLEventListener,
		IVisualization {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1603873519147926836L;
	/** Angle to rotate the cube */
	private float angles[];
	/** The OpenGL animator. */
	private FPSAnimator animator;
	private float base = -1f;
	/** Angle to rotate the cube */
	private float distance = 30f;
	private float divider;
	private IDataLoader dl;
	private float fontRatio = 0.01f;
	/** The frames per second setting. */
	private int fps = 30;
	private int freq;
	private IColorValues fun;
	/** The GL unit (helper class). */
	private GLU glu;

	private double max;
	private double min;

	private MouseCapturer mouseCapturer;
	private double[][][] particles;
	private float positions[];
	private double scale;

	private float tickLength = -0.015f;
	private ViewModifier vm;

	public VisualizationOpenGL(IDataLoader dl, GLCapabilities capabilities,
			ViewModifier vm) {
		super(capabilities);
		this.vm = vm;
		freq = vm.getTensTicksFrequency();
		divider = vm.getCubeSize() / 2f;

		mouseCapturer = new MouseCapturer();
		mouseCapturer.setBasicZoom(5);
		mouseCapturer.setZoomIncreasing(0.2f);
		mouseCapturer.setActive(false);
		addGLEventListener(this);
		addMouseListener(mouseCapturer);
		addMouseMotionListener(mouseCapturer);
		addMouseWheelListener(mouseCapturer);
		setSize(vm.getVisualizationWidth(), vm.getVisualizationHeight());

		this.dl = dl;

		setMinMaxScale();

	}

	private void addData(GL2 gl) {
		particles = (dl).getParticles();
		double[] valueLimits = Filter.getValues();
		for (int z = Filter.getzCoords()[0]; z <= Filter.getzCoords()[1]; ++z) {
			for (int y = Filter.getyCoords()[0]; y <= Filter.getyCoords()[1]; ++y) {
				for (int x = Filter.getxCoords()[0]; x <= Filter.getxCoords()[1]; ++x) {
					double par = particles[z][y][x];
					if (par != Double.MIN_VALUE && par >= valueLimits[0]
							&& par <= valueLimits[1]) {
						double val = fun.getValue(particles[z][y][x], getMin(),
								getMax());
						gl.glColor3d(val, 0, 1.0 - val);
						gl.glVertex3d((base + (float) x / divider),
								(base + (z + 0.0) / divider), (base + (y + 0.0)
										/ divider));
					}
				}
			}
		}
	}

	@Override
	public void changeDL(IDataLoader dl) {

		this.dl = dl;

	}

	public void display(GLAutoDrawable drawable) {
		if (!animator.isAnimating()) {
			return;
		}

		long startTime = System.nanoTime();
		setMinMaxScale();
		vm.updateMinMax();

		final GL2 gl = drawable.getGL().getGL2();
		// Clear screen
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		fun = getColorValuesFun();

		angles = mouseCapturer.getAngles();
		positions = mouseCapturer.getPositions();
		distance = mouseCapturer.getZoom();
		rotate(gl);
		translate(gl);

		gl.glBegin(GL2.GL_POINTS);// static field

		addData(gl);

		gl.glEnd();
		gl.glFlush();
		gl.glPushMatrix();

		drawAxis(gl);

		gl.glLoadIdentity();
		setCamera(gl, glu, distance);

		vm.updateFPS(1.0 / ((System.nanoTime() - startTime) / 1000000000.0));
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		animator.stop();

	}

	private void drawAxis(GL2 gl2) {
		gl2.glBegin(GL2.GL_LINES);

		float b = base * 1.1f;

		gl2.glColor3d(0, 0, 0);
		// x axis
		gl2.glVertex3d(b, b, b);
		gl2.glVertex3d(-b, b, b);

		// z axis
		gl2.glVertex3d(b, b, b);
		gl2.glVertex3d(b, -b, b);

		// y axis
		gl2.glVertex3d(b, b, b);
		gl2.glVertex3d(b, b, -b);

		if (Parameters.isTicks()) {
			boolean tens = Parameters.isTensTicks();
			drawXTicks(gl2, b, tens);
			drawYTicks(gl2, b, tens);
			drawZTicks(gl2, b, tens);
		}

		gl2.glEnd();
		gl2.glFlush();

		if (Parameters.isTicks()) {
			Font font = new Font("SansSerif", Font.BOLD, 12);
			TextRenderer tr = new TextRenderer(font);

			drawXLabels(gl2, b, tr);
			drawYLabels(gl2, b, tr);
			drawZLabels(gl2, b, tr);

		}
	}

	private void drawXLabels(GL2 gl2, float b, TextRenderer tr) {
		int i;
		for (i = Filter.getxCoords()[0]; i <= Filter.getxCoords()[1]; i += Parameters
				.getMaxlocal()) {
			float tickAnchor = base + (i + 0.0f) / divider;
			tr.begin3DRendering();
			tr.setColor(Color.GREEN);
			tr.draw3D(Integer.toString(i + 1), tickAnchor, b, b, fontRatio);
			tr.end3DRendering();
		}
		i = Filter.getxCoords()[1] + 1;
		float tickAnchor = base + (i + 0.0f) / divider;
		tr.begin3DRendering();
		tr.setColor(Color.GREEN);
		tr.draw3D(Integer.toString(i), tickAnchor, b, b, fontRatio);
		tr.end3DRendering();
	}

	private void drawXTicks(GL2 gl2, float b, boolean tens) {
		int counter = 0;
		for (int i = Filter.getxCoords()[0]; i <= Filter.getxCoords()[1]; i += freq) {
			float tickAnchor = base + (i + 0.0f) / divider;
			gl2.glVertex3d(tickAnchor, b, b);
			if (tens) {
				if (counter % freq != 0)
					gl2.glVertex3d(tickAnchor, b, b - tickLength);
				else
					gl2.glVertex3d(tickAnchor, b, -b);
				++counter;
			} else
				gl2.glVertex3d(tickAnchor, b, b - tickLength);
		}
		float tickAnchor = base + (Filter.getxCoords()[1] + 0.0f) / divider;
		gl2.glVertex3d(tickAnchor, b, b);
		gl2.glVertex3d(tickAnchor, b, -b);

	}

	private void drawYLabels(GL2 gl2, float b, TextRenderer tr) {
		int i;
		for (i = Filter.getyCoords()[0]; i <= Filter.getyCoords()[1]; i += Parameters
				.getMaxlocal()) {
			float tickAnchor = base + (i + 0.0f) / divider;
			tr.begin3DRendering();
			tr.setColor(Color.GREEN);
			tr.draw3D(Integer.toString(i + 1), b, b, tickAnchor, fontRatio);
			tr.end3DRendering();
		}
		i = Filter.getyCoords()[1] + 1;
		float tickAnchor = base + (i + 0.0f) / divider;
		tr.begin3DRendering();
		tr.setColor(Color.GREEN);
		tr.draw3D(Integer.toString(i), b, b, tickAnchor, fontRatio);
		tr.end3DRendering();

	}

	private void drawYTicks(GL2 gl2, float b, boolean tens) {
		int counter = 0;
		for (int i = Filter.getyCoords()[0]; i <= Filter.getyCoords()[1]; i += freq) {
			float tickAnchor = base + (i + 0.0f) / divider;
			gl2.glVertex3d(b, b, tickAnchor);
			if (tens) {
				if (counter % freq != 0)
					gl2.glVertex3d(b - tickLength, b, tickAnchor);
				else
					gl2.glVertex3d(-b, b, tickAnchor);
				++counter;
			} else
				gl2.glVertex3d(b - tickLength, b, tickAnchor);
		}
		float tickAnchor = base + (Filter.getyCoords()[1] + 0.0f) / divider;
		gl2.glVertex3d(b, b, tickAnchor);
		gl2.glVertex3d(-b, b, tickAnchor);

	}

	private void drawZLabels(GL2 gl2, float b, TextRenderer tr) {
		int i;
		for (i = Filter.getzCoords()[0]; i <= Filter.getzCoords()[1]; i += Parameters
				.getMaxlocal()) {
			float tickAnchor = base + (i + 0.0f) / divider;
			tr.begin3DRendering();
			tr.setColor(Color.GREEN);
			tr.draw3D(Integer.toString(i + 1), b, tickAnchor, b, fontRatio);
			tr.end3DRendering();
		}
		i = Filter.getzCoords()[1] + 1;
		float tickAnchor = base + (i + 0.0f) / divider;
		tr.begin3DRendering();
		tr.setColor(Color.GREEN);
		tr.draw3D(Integer.toString(i), b, tickAnchor, b, fontRatio);
		tr.end3DRendering();
	}

	private void drawZTicks(GL2 gl2, float b, boolean tens) {
		int counter = 0;
		for (int i = Filter.getzCoords()[0]; i <= Filter.getzCoords()[1]; i += freq) {
			float tickAnchor = base + (i + 0.0f) / divider;
			gl2.glVertex3d(b, tickAnchor, b);
			if (tens) {
				if (counter % freq != 0)
					gl2.glVertex3d(b - tickLength, tickAnchor, b);
				else
					gl2.glVertex3d(-b, tickAnchor, b);
				++counter;
			} else
				gl2.glVertex3d(b - tickLength, tickAnchor, b);
		}
		float tickAnchor = base + (Filter.getzCoords()[1] + 0.0f) / divider;
		gl2.glVertex3d(b, tickAnchor, b);
		gl2.glVertex3d(-b, tickAnchor, b);

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

	@Override
	public double getScale() {
		return scale;
	}

	@Override
	public void init() {
		//

	}

	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		// GLProfile profile = GLProfile.get(GLProfile.GL2);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		;

		gl.glShadeModel(GL2.GL_SMOOTH);

		gl.glClearColor(0f, 0f, 0f, 0f);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		glu = new GLU();
		animator = new FPSAnimator(this, fps);
		animator.start();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(0, 0, width, height);
	}

	private void rotate(GL2 gl2) {
		gl2.glLoadIdentity();
		gl2.glRotatef(angles[0], 0f, 1f, 0f);
		gl2.glRotatef(angles[1], 1f, 0f, 0f);
		gl2.glRotatef(angles[2], 0f, 0f, 1f);
	}

	private void setCamera(GL2 gl, GLU glu, float distance) {
		// // Change to projection matrix.
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// Perspective.
		float widthHeightRatio = (float) getWidth() / (float) getHeight();
		glu.gluPerspective(45, widthHeightRatio, 1, 1000);
		glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

		// Change back to model view matrix.
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void setMax(double max) {
		this.max = max;
	}

	@Override
	public void setMin(double min) {
		this.min = min;
	}

	private void setMinMaxScale() {
		setMax(dl.getMaxVal());
		setMin(dl.getMinVal());
		setScale(Math.abs(getMax() - getMin()));
	}

	@Override
	public void setScale(double scale) {
		this.scale = scale;

	}

	private void translate(GL2 gl2) {
		// gl2.glLoadIdentity();
		gl2.glTranslatef(positions[0], positions[1], positions[2]);
	}

}
