package baron.jakub.view.Slider;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FilterSlider extends RangeSlider {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3380449303403554285L;
	private JTextArea text;
	private RangeSlider slider;
	private JPanel panel;

	public JPanel getPanel() {
		return panel;
	}

	public FilterSlider(String name, int min, int max) {
		panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.setBorder(BorderFactory.createTitledBorder(name));
		panel.setBackground(this.getBackground());

		text = new JTextArea();
		text.setBackground(this.getBackground());
		text.setEditable(false);

		slider = new RangeSlider(min, max);
		slider.addChangeListener(new SliderListener());
		slider.setValue(min);
		slider.setUpperValue(max);
		slider.setEnabled(true);

		// text = new JTextArea("("+Integer.toString:384)");

		panel.add(text);
		panel.add(slider);
	}

	private class SliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			String t = "(" + (slider.getValue()+1) + ":" + (slider.getUpperValue() +1)
					+ ")";
			text.setText(t);
		}

	}

	public void addListener(ChangeListener cl) {
		slider.addChangeListener(cl);
	}

	public RangeSlider getSlider() {
		return slider;
	}

	public int[] getValues() {
		return new int[] { slider.getValue(), slider.getUpperValue() };
	}
}
