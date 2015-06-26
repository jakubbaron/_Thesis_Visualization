package baron.jakub.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import baron.jakub.controller.ViewModifier;

public class Settings extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6748089453221225018L;
	private ViewModifier vm;
	private JTextArea filePrefixTA;
	private JTextArea fileAppendixTA;
	private JTextArea fileExtensionTA;
	private JTextArea pathToFilesTA;
	private JLabel fileExample;
	private JTextArea procNoTA;
	private JTextArea maxLocalTA;
	private JTextArea timeTA;
	private JButton saveBtn;
	private JButton cancelBtn;
	private DocumentListener dcl = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			updateExampleFile();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			updateExampleFile();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			updateExampleFile();
		}
	};

	private JTextArea getNewTextArea(String text, String title, boolean edit) {
		JTextArea ta = new JTextArea(text);
		ta.setOpaque(true);
		ta.setBorder(BorderFactory.createTitledBorder(title));
		ta.setBackground(this.getBackground());
		ta.getDocument().addDocumentListener(dcl);
		ta.setEditable(edit);
		return ta;
	}

	private void addButtonsPanel() {
		ButtonListener btnListener = new ButtonListener();
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(1, 0));
		saveBtn = new JButton("Save");
		saveBtn.addActionListener(btnListener);
		pan.add(saveBtn);

		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(btnListener);
		pan.add(cancelBtn);
		this.add(pan);
	}

	private void updateExampleFile() {
		String path = pathToFilesTA.getText()+timeTA.getText();
		int proc = 63;
		try {
			proc = Integer.parseInt(procNoTA.getText()) - 1;
		} catch (Exception e) {
			vm.addLogMessage("Error " + e.getMessage(), Color.RED);
		}
		String text = filePrefixTA.getText().concat(
				String.format("%03d", proc).concat(timeTA.getText())
						.concat(fileAppendixTA.getText())
						.concat(fileExtensionTA.getText()));

		fileExample.setText("<html>" + path + "<font color=\"green\"><b>"
				+ text + "</b></font></html>");
	}

	public Settings(ViewModifier _vm) {
		this.vm = _vm;

		this.setLayout(new GridLayout(0, 1));

		fileExample = new JLabel();
		fileExample.setOpaque(true);
		fileExample.setBorder(BorderFactory.createTitledBorder("File example"));
		fileExample.setBackground(this.getBackground());
		this.add(fileExample);

		filePrefixTA = getNewTextArea(vm.getFilePrefix(), "File prefix", true);
		this.add(filePrefixTA);

		fileAppendixTA = getNewTextArea(vm.getFileAppendix(), "File appendix",
				true);
		this.add(fileAppendixTA);

		fileExtensionTA = getNewTextArea(vm.getFileExtension(),
				"File extension", true);
		this.add(fileExtensionTA);

		pathToFilesTA = getNewTextArea(vm.getPathToFiles(), "Path", true);
		this.add(pathToFilesTA);

		procNoTA = getNewTextArea(Integer.toString(vm.getProcNo()),
				"Processors No", true);
		this.add(procNoTA);

		maxLocalTA = getNewTextArea(Integer.toString(vm.getMaxlocal()),
				"Max local", true);
		this.add(maxLocalTA);

		timeTA = getNewTextArea((vm.getSelectedTime()), "Series", true);
		this.add(timeTA);

		updateExampleFile();
		addButtonsPanel();
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			if (source == saveBtn) {
				vm.addLogMessage("Parameters are being updated..", Color.BLACK);
				vm.updateAll(maxLocalTA.getText(), procNoTA.getText(), null,
						timeTA.getText(), filePrefixTA.getText(),
						fileAppendixTA.getText(), null, pathToFilesTA.getText());
			} else if (source == cancelBtn) {
				Settings.this.dispose();
				Settings.this.setVisible(false);
			}
		}

	}
}
