package baron.jakub.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SwingWorker;

import baron.jakub.controller.ViewModifier;
import baron.jakub.controller.Loaders.IDataLoader;
import baron.jakub.model.Filter;
import baron.jakub.model.Parameters;
import baron.jakub.view.Slider.FilterSlider;
import baron.jakub.view.Slider.RangeSlider;

public class ControlPanelView extends JPanel {

	private class ButtonsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton source = (JButton) e.getSource();
			if (source == settingBtn) {
				Settings set = new Settings(vm);
				set.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				set.setMinimumSize(new Dimension(500, 150));
				set.setLocation(400, 400);
				set.pack();
				set.setVisible(true);
			} else if (source == loadBtn) {
				vm.setAllUnloaded();
				vm.changeGraph(Parameters.plotEnum.values()[plotTypes
						.getSelectedIndex()], dataTypes.getSelectedIndex());
				// updateMinMax();
			} else if (source == filterBtn) {
				String minText = min.getText();
				String maxText = max.getText();
				try {
					Double minD = Double.parseDouble(minText);
					Double maxD = Double.parseDouble(maxText);
					if (minD > maxD) {
						throw new Exception("Invalid values");
					}
					Filter.setValues(new double[] { minD, maxD });
				} catch (Exception ex) {
					vm.addLogMessage(ex.toString(), Color.RED);
					updateMinMax();
				}
			}
		}

	}

	private class CheckBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JCheckBox source = (JCheckBox) e.getSource();
			if (source == invBox) {
				Parameters.setInversed(invBox.isSelected());
				// vm.changeDataType();
			} else if (source == normBox) {
				Parameters.setNormalized(normBox.isSelected());
				// vm.changeDataType();
			} else if (source == ticksBox) {
				Parameters.setTicks(ticksBox.isSelected());
			} else if (source == tenTicksBox) {
				Parameters.setTensTicks(tenTicksBox.isSelected());
			}
		}

	}

	private class SliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			RangeSlider source = (RangeSlider) e.getSource();
			if (source == sliders[0].getSlider()) {
				vm.updateFilter(sliders[0].getValues(), ViewModifier.AXIS.X);
			} else if (source == sliders[1].getSlider()) {
				vm.updateFilter(sliders[1].getValues(), ViewModifier.AXIS.Y);
			} else if (source == sliders[2].getSlider()) {
				vm.updateFilter(sliders[2].getValues(), ViewModifier.AXIS.Z);
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7244657659975934092L;
	private JComboBox<String> dataTypes;
	private IDataLoader dl;
	private JButton filterBtn;
	private JLabel fpsCounterText;
	private JCheckBox invBox;
	private JButton loadBtn;
	private JTextArea max;
	private JLabel maxValue;
	private JTextArea min;
	private JLabel minValue;
	private JCheckBox normBox;
	private JComboBox<String> plotTypes;
	private JProgressBar progressBar;
	private JButton settingBtn;
	private FilterSlider[] sliders;
	private JCheckBox tenTicksBox;

	private JCheckBox ticksBox;

	private JSlider timeSlider;

	private ViewModifier vm;

	public ControlPanelView(ViewModifier _vm, IDataLoader dl) {
		this.vm = _vm;
		changeDL(dl);
		this.setLayout(new GridLayout(0, 1));
		this.setOpaque(true);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK,
						Color.DARK_GRAY), "Parameters"));

		addComboBoxes();

		addCheckBoxesPanel();

		addSliders();

		addValuesDisplays();

		addTimeSlider();

		ButtonsListener bl = new ButtonsListener();
		addMinMaxPanel(bl);
		addButtons(bl);

		addProgressBar();

		this.add(progressBar);

	}

	private void addButtons(ButtonsListener bl) {

		settingBtn = new JButton("Settings");
		settingBtn.addActionListener(bl);
		this.add(settingBtn);

		loadBtn = new JButton("Load data");
		loadBtn.addActionListener(bl);
		this.add(loadBtn);
	}

	private void addCheckBoxesPanel() {
		JPanel invNormPan = new JPanel();
		invNormPan.setLayout(new GridLayout(2, 3));
		invNormPan.setOpaque(true);
		invNormPan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		CheckBoxListener cbl = new CheckBoxListener();

		normBox = new JCheckBox();
		normBox.setText("Normalized?");
		normBox.setSelected(Parameters.isNormalized());
		normBox.addActionListener(cbl);

		invBox = new JCheckBox();
		invBox.setText("Inversed?");
		invBox.setSelected(Parameters.isInversed());
		invBox.addActionListener(cbl);

		ticksBox = new JCheckBox();
		ticksBox.setText("Ticks");
		ticksBox.setSelected(Parameters.isTicks());
		ticksBox.addActionListener(cbl);

		tenTicksBox = new JCheckBox();
		tenTicksBox.setText("Tens ticks");
		tenTicksBox.setSelected(Parameters.isTensTicks());
		tenTicksBox.addActionListener(cbl);

		fpsCounterText = new JLabel();
		updateFPS(0);

		invNormPan.add(normBox);
		invNormPan.add(invBox);
		invNormPan.add(fpsCounterText);
		invNormPan.add(ticksBox);
		invNormPan.add(tenTicksBox);
		this.add(invNormPan);
	}

	private void addComboBoxes() {
		plotTypes = new JComboBox<String>(Parameters.getPlotTypes());
		plotTypes.setOpaque(true);
		plotTypes.setBorder(BorderFactory.createTitledBorder("Plot type"));
		// plotTypes.addActionListener(new PlotTypeChangeListener());
		this.add(plotTypes);

		dataTypes = new JComboBox<String>(Parameters.getDataValues());
		dataTypes.setOpaque(true);
		dataTypes.setBorder(BorderFactory.createTitledBorder("Value of"));
		// dataTypes.addActionListener(new DataTypeChangeListener());
		this.add(dataTypes);
	}

	private void addMinMaxPanel(ButtonsListener bl) {
		JPanel minMaxPan = new JPanel();
		minMaxPan.setLayout(new GridLayout(1, 0));

		min = new JTextArea(Double.toString(0.0));
		min.setOpaque(true);
		min.setBorder(BorderFactory.createTitledBorder("Min"));
		min.setBackground(this.getBackground());

		max = new JTextArea(Double.toString(1.0));
		max.setOpaque(true);
		max.setBorder(BorderFactory.createTitledBorder("Max"));
		max.setBackground(this.getBackground());

		filterBtn = new JButton("Apply");
		filterBtn.addActionListener(bl);

		minMaxPan.add(min);
		minMaxPan.add(max);
		minMaxPan.add(filterBtn);
		this.add(minMaxPan);
	}

	private void addProgressBar() {
		// TODO Auto-generated method stub
		progressBar = new JProgressBar(0, vm.getProcNo());
		progressBar.setVisible(true);
		progressBar.setStringPainted(true);
	}

	private void addSliders() {
		SliderListener sliderLisnter = new SliderListener();
		sliders = new FilterSlider[3];
		sliders[0] = new FilterSlider("X", Filter.getxCoords()[0],
				Filter.getxCoords()[1]);
		sliders[0].addListener(sliderLisnter);
		this.add(sliders[0].getPanel());

		sliders[1] = new FilterSlider("Y", Filter.getyCoords()[0],
				Filter.getyCoords()[1]);
		sliders[1].addListener(sliderLisnter);
		this.add(sliders[1].getPanel());

		sliders[2] = new FilterSlider("Z", Filter.getzCoords()[0],
				Filter.getzCoords()[1]);
		sliders[2].addListener(sliderLisnter);
		this.add(sliders[2].getPanel());
	}

	private void addTimeSlider() {
		String[] values = vm.getAvailableSeries();
		timeSlider = new JSlider(0, values.length - 1, 0);
		Hashtable<Integer, JLabel> Labels = new Hashtable<>();
		for (int i = 0; i < values.length; ++i)
			Labels.put(i, new JLabel(values[i]));
		timeSlider.setLabelTable(Labels);
		timeSlider.setPaintTicks(true);
		timeSlider.setPaintLabels(true);
		timeSlider.setSnapToTicks(true);
		timeSlider.setMajorTickSpacing(1);
		// timeSlider.setValue(0);
		timeSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				vm.setSelectedTime(timeSlider.getValue());
			}

		});
		this.add(timeSlider);
	}

	private void addValuesDisplays() {
		JPanel valueDisplayer = new JPanel();
		valueDisplayer.setLayout(new GridLayout(0, 1));
		minValue = new JLabel();
		maxValue = new JLabel();
		valueDisplayer.add(minValue);
		valueDisplayer.add(maxValue);
		this.add(valueDisplayer);
	}

	@SuppressWarnings("unchecked")
	public void changeDL(IDataLoader dl2) {
		// TODO Auto-generated method stub
		this.dl = dl2;
		((SwingWorker<String, Void>) dl)
				.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						switch (evt.getPropertyName()) {
						case "progress":
							progressBar.setIndeterminate(false);
							progressBar.setValue((Integer) evt.getNewValue());
							break;
						}
					}
				});
	}

	public void updateFPS(double fps) {
		this.fpsCounterText.setText(String.format("%02.2f FPS", fps));
	}

	public void updateMinMax() {
		minValue.setText("Min: " + Double.toString(dl.getMinVal()));
		maxValue.setText("Max: " + Double.toString(dl.getMaxVal()));
	}
}
