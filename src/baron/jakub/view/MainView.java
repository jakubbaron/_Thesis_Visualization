package baron.jakub.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jzy3d.global.Settings;

import baron.jakub.controller.ViewModifier;
import baron.jakub.controller.Loaders.*;
import baron.jakub.model.Filter;
import baron.jakub.model.Parameters;

public class MainView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1484649289204501945L;
	private ControlPanelView cpv;
	private JTextPane logs;
	private JScrollPane scrollLogs;
	private IVisualization VisObject;
	private ViewModifier vm;
	private Hashtable<String, IDataLoader> hdl;
	private IDataLoader dl;
	private PropertyChangeListener dlPCL;
	private PrintWriter writer;
	private long lastTime;

	private void addLogsWindow() {
		logs = new JTextPane();
		logs.setEditable(false);
		DefaultCaret caret = (DefaultCaret) logs.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		scrollLogs = new JScrollPane(logs,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollLogs.setBorder(BorderFactory.createTitledBorder("Logs"));
		scrollLogs.setMinimumSize(new Dimension(vm.getWidth(), vm.getHeight()
				- vm.getVisualizationHeight()));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weighty = 1.;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 5);
		this.add(scrollLogs, gbc);
	}

	private void addControlViewPanel() {
		cpv = new ControlPanelView(vm, dl);
		cpv.setMinimumSize(new Dimension(vm.getWidth()
				- vm.getVisualizationWidth(), vm.getVisualizationHeight()));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 0.15;
		gbc.weighty = 0.8;
		this.add(cpv, gbc);

		cpv.updateMinMax();
	}

	@SuppressWarnings("unchecked")
	public MainView(ViewModifier vm) {

		setTitle("Massively Parallel Data Visualization");

		Container container = this.getContentPane();
		container.setLayout(new GridBagLayout());
		this.vm = vm;

		addLogsWindow();

		this.hdl = new Hashtable<String, IDataLoader>();

		for (String i : this.vm.getAvailableSeries()) {
			hdl.put(i, new OptimizedDataLoader(vm, i));
		}
		this.dl = hdl.get(vm.getSelectedTime());

		dlPCL = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				switch (evt.getPropertyName()) {
				case "message":
					addLog((String) evt.getNewValue(), Color.BLACK);
					break;
				}

			}
		};

		((SwingWorker<String, Void>) dl).addPropertyChangeListener(dlPCL);

		addControlViewPanel();

		setLocation(10, 10);

		setSize(new Dimension(vm.getWidth(), vm.getHeight()));
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				System.exit(0);
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
				MainView.this.dispose();

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		openFileToSaveLogs();
		lastTime = System.currentTimeMillis();
	}

	private void openFileToSaveLogs() {
		DateFormat df = new SimpleDateFormat("yyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String filePath = new File("").getAbsolutePath();
		String filename = filePath + "\\logs\\logs" + df.format(c.getTime())
				+ ".logs";
		File file = new File(filename);
		try {
			if (!file.exists())
				file.createNewFile();
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file,
					true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addLogToFile(String log) {
		if (writer != null) {
			writer.println(log);
			writer.println(getResourcesUsage());
		}
	}

	private String getResourcesUsage() {
		Runtime runtime = Runtime.getRuntime();

		NumberFormat format = NumberFormat.getInstance();

		StringBuilder sb = new StringBuilder();
		long allocatedMemory = runtime.totalMemory();
		sb.append("allocated memory: " + format.format(allocatedMemory / 1024)
				+ "\t");
		return sb.toString();
	}

	private GridBagConstraints getGraphGBC() {
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		// gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 0.85;
		gbc.weighty = 0.8;
		return gbc;
	}

	/**
	 * @return Some standard GL capabilities (with alpha).
	 */
	private GLCapabilities createGLCapabilities() {
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setRedBits(8);
		capabilities.setBlueBits(8);
		capabilities.setGreenBits(8);
		capabilities.setAlphaBits(8);
		return capabilities;
	}

	private void addOpenGL(GridBagConstraints gbc) {

		GLCapabilities capabilities = createGLCapabilities();
		VisObject = new VisualizationOpenGL(dl, capabilities, vm);

		this.add((GLCanvas) VisObject, gbc);
	}

	private void delOpenGL() {
		((GLCanvas) VisObject)
				.removeGLEventListener((GLEventListener) VisObject);
		this.remove((GLCanvas) VisObject);
		VisObject = null;
		System.gc();
	}

	private void addJZY3D(GridBagConstraints gbc) {
		Settings.getInstance().setHardwareAccelerated(true);
		VisObject = new VisualizationJZY3D(dl, vm);
		((Component) ((VisualizationJZY3D) VisObject).getChart().getCanvas())
				.setSize(new Dimension(vm.getVisualizationWidth(), vm
						.getVisualizationHeight()));

		this.add((Component) ((VisualizationJZY3D) VisObject).getChart()
				.getCanvas(), gbc);
	}

	private void delJZY3D() {
		Settings.getInstance().setHardwareAccelerated(false);
		Component c = (Component) ((VisualizationJZY3D) VisObject).getChart()
				.getCanvas();
		this.remove(c);
		c = null;
		((VisualizationJZY3D) VisObject).dispose();
		VisObject = null;
		System.gc();
	}

	private void addImprovedOpenGL(GridBagConstraints gbc) {
		GLCapabilities capabilities = createGLCapabilities();
		VisObject = new VisualizationOpenGLImproved(dl, capabilities, vm);
		this.add((GLCanvas) VisObject, gbc);
	}

	private void delImprovedOpenGL() {
		((GLCanvas) VisObject)
				.removeGLEventListener((GLEventListener) VisObject);
		this.remove((GLCanvas) VisObject);
		VisObject = null;
		System.gc();
	}

	public void addVisObject(Parameters.plotEnum prevType,
			Parameters.plotEnum newType) {
		// if (newType == null || prevType == null) {
		// return;
		// }
		if (VisObject != null) {
			switch (prevType) {
			case ImprovedOpenGL:
				delImprovedOpenGL();
				break;
			case OpenGL:
				delOpenGL();
				break;
			case jzy3D:
				delJZY3D();
				break;
			default:
				break;
			}
		}

		GridBagConstraints gbc = getGraphGBC();
		if (newType != null) {
			switch (newType) {
			/******************** First try with OpenGL ***********************/
			case OpenGL:
				addOpenGL(gbc);
				break;
			case jzy3D:
				/******************** Try with a JZY3D Demos ***********************/
				addJZY3D(gbc);
				break;
			case ImprovedOpenGL:
				addImprovedOpenGL(gbc);
				break;
			default:
				break;

			}
		}
		this.revalidate();
		this.repaint();
	}

	public void addLog(String string, Color color) {
		Date date = new Date();
		String text = Parameters.getDateFormat().format(date) + ": " + string;
		text = logs.getText().equals("") ? text : "\n" + text;
		addText(text, color);
		addLogToFile(text);
	}

	private void addText(String text, Color color) {
		StyledDocument doc = logs.getStyledDocument();
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, color);
		Color clr = color == Color.RED ? Color.YELLOW : Color.WHITE;
		StyleConstants.setBackground(keyWord, clr);
		try {
			doc.insertString(doc.getLength(), text, keyWord);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void updateFPS(double fps) {
		cpv.updateFPS(fps);
		this.logFPS(fps);

	}

	private void logFPS(double fps) {
		if (Math.abs(lastTime - System.currentTimeMillis()) >= 1000) {
			double[] vals = Filter.getValues();
			if (vals[0] == Double.MIN_VALUE) {
				vals = null;
			}
			if (vals == null)
				addLogToFile(String.format("FPS - %.3f", fps));
			else
				addLogToFile(String.format("FPS - %.3f [%.3f , %.3f]", fps,
						vals[0], vals[1]));
			lastTime = System.currentTimeMillis();
		}

	}

	@SuppressWarnings("unchecked")
	public void changeTime(String ti) {
		((SwingWorker<String, Void>) dl).removePropertyChangeListener(dlPCL);
		dl = hdl.get(ti);
		if (VisObject != null)
			VisObject.changeDL(dl);
		cpv.changeDL(dl);
		((SwingWorker<String, Void>) dl).addPropertyChangeListener(dlPCL);
	}

	public void setAllUnloaded() {
		// TODO Auto-generated method stub
		for (IDataLoader v : hdl.values()) {
			v.setLoaded(false);
		}
		dl.setLoaded(false);

	}

	public void updateMinMax() {
		// TODO Auto-generated method stub
		cpv.updateMinMax();

	}

}
