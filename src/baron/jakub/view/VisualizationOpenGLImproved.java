package baron.jakub.view;

import java.awt.Color;
import java.awt.Font;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import baron.jakub.controller.ViewModifier;
import baron.jakub.controller.Loaders.IDataLoader;
import baron.jakub.model.*;

public class VisualizationOpenGLImproved extends GLCanvas implements
		GLEventListener, IVisualization {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1603873519147926836L;

	protected int[] aiVertexBufferIndices = new int[] { -1 };
	/** Angle to rotate the cube */
	private float angles[];
	/** The OpenGL animator. */
	private FPSAnimator animator;

	protected int[] axisVertexBufferIndices = new int[] { -1 };
	private float base = -0.5f;

	private float distance = 30f;
	private float divider = 384f;
	private IDataLoader dl;
	/**
	 * Ratio of world-space units to screen pixels. Increasing this zooms the
	 * display out, decreasing it zooms the display in.
	 */
	// protected float fObjectUnitsPerPixel = 0.01f;

	private float fontRatio = 0.005f;

	/** The frames per second setting. */
	private int fps = 30;
	private int freq;

	private IColorValues fun;
	protected GLContext glcontext;
	private GLU glu;
	private long lastPosition = 0;
	private double max;
	private double min;
	private MouseCapturer mouseCapturer;
	double[][][] particles;
	private float positions[];
	private double scale;
	private float tickLength = -0.015f;
	private ViewModifier vm;

	public VisualizationOpenGLImproved(IDataLoader dl,
			GLCapabilities capabilities, ViewModifier vm) {
		super(capabilities);
		this.vm = vm;
		freq = vm.getTensTicksFrequency();
		divider = vm.getCubeSize();
		mouseCapturer = new MouseCapturer();
		mouseCapturer.setBasicZoom(5);
		mouseCapturer.setZoomIncreasing(0.2f);
		addGLEventListener(this);
		addMouseListener(mouseCapturer);
		addMouseMotionListener(mouseCapturer);
		addMouseWheelListener(mouseCapturer);

		setSize(vm.getVisualizationWidth(), vm.getVisualizationHeight());

		this.dl = dl;
		setMinMaxScale();
	}

	private void addBlack(FloatBuffer floatbuffer) {
		floatbuffer.put(0f);
		floatbuffer.put(0f);
		floatbuffer.put(0f);
	}

	private void addVerticesAndColors(FloatBuffer floatbuffer) {
		int z = 0, y = 0, x = 0;
		double[] valueLimits = Filter.getValues();
		try {
			for (z = Filter.getzCoords()[0]; z <= Filter.getzCoords()[1]; ++z) {
				for (y = Filter.getyCoords()[0]; y <= Filter.getyCoords()[1]; ++y) {
					for (x = Filter.getxCoords()[0]; x <= Filter.getxCoords()[1]; ++x) {
						double par = particles[z][y][x];
						if (par != Double.MIN_VALUE && par >= valueLimits[0]
								&& par <= valueLimits[1]) {
							// if (par >= valueLimits[0] && par <=
							// valueLimits[1]) {
							double val = fun.getValue(par, getMin(), getMax());
							floatbuffer.put((base + (float) x / divider));
							floatbuffer.put((base + (float) z / divider));
							floatbuffer.put((base + (float) y / divider));
							floatbuffer.put((float) val);
							floatbuffer.put(0.0f);
							floatbuffer.put(1.0f - (float) val);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error at X: " + x + " Y: " + y + " Z: " + z);
		}
		if (lastPosition > floatbuffer.position()) {
			long newPos = floatbuffer.position();
			float b = base * 1.1f;
			for (long i = lastPosition; i > newPos; i = i - 6) {
				floatbuffer.put(b);
				floatbuffer.put(b);
				floatbuffer.put(b);
				floatbuffer.put(0);
				floatbuffer.put(0);
				floatbuffer.put(0);
			}
			lastPosition = newPos;
		} else {
			lastPosition = floatbuffer.position();
		}
	}

	@Override
	public void changeDL(IDataLoader dl) {
		this.dl = dl;
	}

	protected int[] createAndFillVertexBuffer(GL2 gl2) {
		particles = (dl).getParticles();
		int x = (Filter.getxCoords()[1] - Filter.getxCoords()[0]) + 1;
		int y = (Filter.getyCoords()[1] - Filter.getyCoords()[0]) + 1;
		int z = (Filter.getzCoords()[1] - Filter.getzCoords()[0]) + 1;
		x = x == 0 ? 1 : x;
		y = y == 0 ? 1 : y;
		z = z == 0 ? 1 : z;

		int[] aiNumOfVertices = new int[] { x * y * z };
		if (aiVertexBufferIndices[0] == -1) {
			// check for VBO support
			if (!gl2.isFunctionAvailable("glGenBuffers")
					|| !gl2.isFunctionAvailable("glBindBuffer")
					|| !gl2.isFunctionAvailable("glBufferData")
					|| !gl2.isFunctionAvailable("glDeleteBuffers")) {
				System.out.println("Error "
						+ "Vertex buffer objects not supported.");
			}

			gl2.glGenBuffers(1, aiVertexBufferIndices, 0);

			// create vertex buffer data store without initial copy
			gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, aiVertexBufferIndices[0]);
			gl2.glBufferData(GL.GL_ARRAY_BUFFER, aiNumOfVertices[0] * 3
					* Buffers.SIZEOF_FLOAT * 2, null, GL2.GL_DYNAMIC_DRAW);
		}

		// map the buffer and write vertex and color data directly into it
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, aiVertexBufferIndices[0]);
		ByteBuffer bytebuffer = gl2.glMapBuffer(GL.GL_ARRAY_BUFFER,
				GL2.GL_WRITE_ONLY);
		FloatBuffer floatbuffer = bytebuffer.order(ByteOrder.nativeOrder())
				.asFloatBuffer();

		addVerticesAndColors(floatbuffer);

		gl2.glUnmapBuffer(GL.GL_ARRAY_BUFFER);

		return (aiNumOfVertices);
	}

	public void display(GLAutoDrawable drawable) {
		if (!animator.isAnimating()) {
			return;
		}

		long startTime = System.nanoTime();

		setMinMaxScale();
		vm.updateMinMax();
		final GL2 gl2 = drawable.getGL().getGL2();
		// Clear screen
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		fun = getColorValuesFun();

		// create vertex buffers if needed, then copy data in
		angles = mouseCapturer.getAngles();
		positions = mouseCapturer.getPositions();
		distance = mouseCapturer.getZoom();
		rotate(gl2);
		translate(gl2);

		int[] aiNumOfVertices = createAndFillVertexBuffer(gl2);
		int[] axisNumOfVertices = drawAxis(gl2);

		// draw all quads in vertex buffer
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, aiVertexBufferIndices[0]);
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl2.glVertexPointer(3, GL.GL_FLOAT, 6 * Buffers.SIZEOF_FLOAT, 0);
		gl2.glColorPointer(3, GL.GL_FLOAT, 6 * Buffers.SIZEOF_FLOAT,
				3 * Buffers.SIZEOF_FLOAT);

		gl2.glDrawArrays(GL2.GL_POINTS, 0, aiNumOfVertices[0]);

		// gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, axisVertexBufferIndices[0]);
		gl2.glVertexPointer(3, GL.GL_FLOAT, 6 * Buffers.SIZEOF_FLOAT, 0);
		gl2.glColorPointer(3, GL.GL_FLOAT, 6 * Buffers.SIZEOF_FLOAT,
				3 * Buffers.SIZEOF_FLOAT);
		// gl2.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);
		gl2.glDrawArrays(GL2.GL_LINES, 0, axisNumOfVertices[0]);

		// disable arrays once we're done
		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		// gl2.glDeleteBuffers(1, axisVertexBufferIndices, 0);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
		// gl2.glLoadIdentity();

		setCamera(gl2, glu, distance);

		vm.updateFPS(1.0 / ((System.nanoTime() - startTime) / 1000000000.0));

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		animator.stop();
		final GL2 gl2 = drawable.getGL().getGL2();
		gl2.glDeleteBuffers(1, aiVertexBufferIndices, 0);
		gl2.glDeleteBuffers(1, axisVertexBufferIndices, 0);
		aiVertexBufferIndices[0] = -1;
	}

	protected void disposeVertexBuffers() {
		// glcontext.makeCurrent();
		GL2 gl2 = glcontext.getGL().getGL2();
		gl2.glDeleteBuffers(1, aiVertexBufferIndices, 0);
		aiVertexBufferIndices[0] = -1;
		glcontext.release();
	}

	protected int[] drawAxis(GL2 gl2) {
		int[] aiNumVertices = new int[] { vm.getCubeSize()*3};//{13 + 38 * 6 + 18 };

		gl2.glGenBuffers(1, axisVertexBufferIndices, 0);

		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, axisVertexBufferIndices[0]);
		gl2.glBufferData(GL.GL_ARRAY_BUFFER, aiNumVertices[0] * 3
				* Buffers.SIZEOF_FLOAT * 2, null, GL2.GL_DYNAMIC_DRAW);

		gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, axisVertexBufferIndices[0]);
		ByteBuffer bytebuffer = gl2.glMapBuffer(GL.GL_ARRAY_BUFFER,
				GL2.GL_WRITE_ONLY);
		FloatBuffer floatbuffer = bytebuffer.order(ByteOrder.nativeOrder())
				.asFloatBuffer();

		setupAxis(floatbuffer);
		gl2.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		if (Parameters.isTicks()) {
			float beginning = base * 1.1f;
			Font font = new Font("SansSerif", Font.BOLD, 12);
			TextRenderer tr = new TextRenderer(font);

			drawXLabels(beginning, tr);
			drawYLabels(beginning, tr);
			drawZLabels(beginning, tr);

		}
		return (aiNumVertices);

		// gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, axisVertexBufferIndices[0]);
	}

	private int drawLongTick(FloatBuffer floatbuffer, Integer counter,
			boolean tens, float beginning, float sign) {
		if (tens) {
			if (counter % freq != 0)
				floatbuffer.put(beginning * sign - tickLength * sign);
			else
				floatbuffer.put(-beginning * sign);
			return ++counter;
		} else {
			floatbuffer.put(beginning * sign - tickLength * sign);
			return -1;
		}
	}

	private void drawXLabels(float b, TextRenderer tr) {
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

	private void drawXTicks(FloatBuffer floatbuffer, float beginning,
			boolean tens) {
		int counter = 0;
		for (int i = Filter.getxCoords()[0]; i <= Filter.getxCoords()[1]; i += 10) {
			float tickAnchor = base + (i + 0.0f) / 384f;
			floatbuffer.put(tickAnchor);
			floatbuffer.put(beginning);
			floatbuffer.put(-beginning);
			addBlack(floatbuffer);

			floatbuffer.put(tickAnchor);
			floatbuffer.put(beginning);
			counter = drawLongTick(floatbuffer, counter, tens, beginning, -1f);
			addBlack(floatbuffer);
		}
		float tickAnchor = base + (Filter.getxCoords()[1] + 0.0f) / 384f;
		floatbuffer.put(tickAnchor);
		floatbuffer.put(beginning);
		floatbuffer.put(-beginning);
		addBlack(floatbuffer);

		floatbuffer.put(tickAnchor);
		floatbuffer.put(beginning);
		drawLongTick(floatbuffer, freq, tens, beginning, -1f);
		addBlack(floatbuffer);
	}

	private void drawYLabels(float b, TextRenderer tr) {
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

	private void drawYTicks(FloatBuffer floatbuffer, float beginning,
			boolean tens) {
		int counter = 0;
		for (int i = Filter.getyCoords()[0]; i <= Filter.getyCoords()[1]; i += 10) {
			float tickAnchor = base + (i + 0.0f) / 384f;
			floatbuffer.put(beginning);
			floatbuffer.put(beginning);
			floatbuffer.put(tickAnchor);
			addBlack(floatbuffer);

			counter = drawLongTick(floatbuffer, counter, tens, beginning, 1f);
			floatbuffer.put(beginning);
			floatbuffer.put(tickAnchor);
			addBlack(floatbuffer);
		}
		float tickAnchor = base + (Filter.getyCoords()[1] + 0.0f) / 384f;
		floatbuffer.put(beginning);
		floatbuffer.put(beginning);
		floatbuffer.put(tickAnchor);
		addBlack(floatbuffer);

		drawLongTick(floatbuffer, freq, tens, beginning, 1f);
		floatbuffer.put(beginning);
		floatbuffer.put(tickAnchor);
		addBlack(floatbuffer);
	}

	private void drawZLabels(float b, TextRenderer tr) {
		int i;
		for (i = Filter.getzCoords()[0]; i <= Filter.getzCoords()[1]; i += Parameters
				.getMaxlocal()) {
			float tickAnchor = base + (i + 0.0f) / divider;
			tr.begin3DRendering();
			tr.setColor(Color.GREEN);
			tr.draw3D(Integer.toString(i + 1), b, tickAnchor, -b, fontRatio);
			tr.end3DRendering();
		}
		i = Filter.getzCoords()[1] + 1;
		float tickAnchor = base + (i + 0.0f) / divider;
		tr.begin3DRendering();
		tr.setColor(Color.GREEN);
		tr.draw3D(Integer.toString(i), b, tickAnchor, -b, fontRatio);
		tr.end3DRendering();
	}

	private void drawZTicks(FloatBuffer floatbuffer, float beginning,
			boolean tens) {
		int counter = 0;
		for (int i = Filter.getzCoords()[0]; i <= Filter.getzCoords()[1]; i += 10) {
			float tickAnchor = base + (i + 0.0f) / 384f;
			floatbuffer.put(beginning);
			floatbuffer.put(tickAnchor);
			floatbuffer.put(-beginning);
			addBlack(floatbuffer);

			counter = drawLongTick(floatbuffer, counter, tens, beginning, 1);
			floatbuffer.put(tickAnchor);
			floatbuffer.put(-beginning);
			addBlack(floatbuffer);

		}
		float tickAnchor = base + (Filter.getzCoords()[1] + 0.0f) / 384f;
		floatbuffer.put(beginning);
		floatbuffer.put(tickAnchor);
		floatbuffer.put(-beginning);
		addBlack(floatbuffer);

		drawLongTick(floatbuffer, freq, tens, beginning, 1);
		floatbuffer.put(tickAnchor);
		floatbuffer.put(-beginning);
		addBlack(floatbuffer);
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

	}

	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

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

	private void setupAxis(FloatBuffer floatbuffer) {
		float beginning = base * 1.1f;
		// x axis
		floatbuffer.put(beginning);
		floatbuffer.put(beginning);
		floatbuffer.put(-beginning);
		addBlack(floatbuffer);

		floatbuffer.put(-beginning);
		floatbuffer.put(beginning);
		floatbuffer.put(-beginning);
		addBlack(floatbuffer);

		// z axis
		floatbuffer.put(beginning);
		floatbuffer.put(beginning);
		floatbuffer.put(-beginning);
		addBlack(floatbuffer);

		floatbuffer.put(beginning);
		floatbuffer.put(-beginning);
		floatbuffer.put(-beginning);
		addBlack(floatbuffer);

		// y axis
		floatbuffer.put(beginning);
		floatbuffer.put(beginning);
		floatbuffer.put(beginning);
		addBlack(floatbuffer);

		floatbuffer.put(beginning);
		floatbuffer.put(beginning);
		floatbuffer.put(-beginning);
		addBlack(floatbuffer);

		if (Parameters.isTicks()) {
			boolean tens = Parameters.isTensTicks();
			drawXTicks(floatbuffer, beginning, tens);
			drawYTicks(floatbuffer, beginning, tens);
			drawZTicks(floatbuffer, beginning, tens);
		}
	}

	private void translate(GL2 gl2) {
		// gl2.glLoadIdentity();
		gl2.glTranslatef(positions[0], positions[1], positions[2]);
	}

}
