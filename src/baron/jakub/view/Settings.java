package baron.jakub.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import baron.jakub.controller.ViewModifier;
import baron.jakub.model.ProcessorFile;

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
	private JButton saveBtn;
	private JButton cancelBtn;
	private JTabbedPane tabbedPane;
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

	private void addButtonsPanel(JPanel frame) {
		ButtonListener btnListener = new ButtonListener();
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(1, 0));
		saveBtn = new JButton("Save");
		saveBtn.addActionListener(btnListener);
		pan.add(saveBtn);

		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(btnListener);
		pan.add(cancelBtn);
		frame.add(pan);
	}

	private void updateExampleFile() {
		String path = pathToFilesTA.getText() + vm.getSelectedTime()
				+ vm.isUnix();
		int proc = 63;
		try {
			if (!procNoTA.getText().equals(""))
				proc = Integer.parseInt(procNoTA.getText()) - 1;
		} catch (Exception e) {
			vm.addLogMessage("Error " + e.getMessage(), Color.RED);
		}
		String extension = fileExtensionTA.getText();
		if (!extension.equals("") && !extension.contains("."))
			extension = "." + extension;
		String text = filePrefixTA.getText().concat(
				String.format("%03d", proc).concat(vm.getSelectedTime())
						.concat(fileAppendixTA.getText())
						.concat(extension));

		fileExample.setText("<html>" + path + "<font color=\"green\"><b>"
				+ text + "</b></font></html>");
	}

	private JPanel addSettingOfTheFilePane() {
		JPanel frame = new JPanel();
		frame.setLayout(new GridLayout(0, 1));

		fileExample = new JLabel();
		fileExample.setOpaque(true);
		fileExample.setBorder(BorderFactory.createTitledBorder("File example"));
		fileExample.setBackground(this.getBackground());
		frame.add(fileExample);

		filePrefixTA = getNewTextArea(vm.getFilePrefix(), "File prefix", true);
		frame.add(filePrefixTA);

		fileAppendixTA = getNewTextArea(vm.getFileAppendix(), "File appendix",
				true);
		frame.add(fileAppendixTA);

		fileExtensionTA = getNewTextArea(vm.getFileExtension(),
				"File extension", true);
		frame.add(fileExtensionTA);

		pathToFilesTA = getNewTextArea(vm.getPathToFiles(), "Path", true);
		frame.add(pathToFilesTA);

		procNoTA = getNewTextArea(Integer.toString(vm.getProcNo()),
				"Processors No", true);
		frame.add(procNoTA);

		maxLocalTA = getNewTextArea(Integer.toString(vm.getMaxlocal()),
				"Max local", true);
		frame.add(maxLocalTA);

		updateExampleFile();
		addButtonsPanel(frame);
		return frame;
	}

	private JScrollPane listOfFiles(String s) {
		String text = "";
		for (ProcessorFile p : vm.getListOfFiles(s)) {
			text += p.number + " - " + p.filename + "\n";
		}
		JTextArea ta = new JTextArea(text);
		ta.setEditable(false);
		JScrollPane scrollLogs = new JScrollPane(ta,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return scrollLogs;
	}

	private void addTabsWithListOfFiles() {
		for (String s : vm.getAvailableSeries()) {
			tabbedPane.add(s, listOfFiles(s));
		}
	}

	private void addTabedPane() {
		tabbedPane = new JTabbedPane();
		if (vm.getProcNo() > 0)
			tabbedPane.add("File example", addSettingOfTheFilePane());
		addTabsWithListOfFiles();

		this.add(tabbedPane);
	}

	public Settings(ViewModifier _vm) {
		this.vm = _vm;
		addTabedPane();
		this.setPreferredSize(new Dimension(vm.getWidth() / 2,
				vm.getHeight() / 2));
	}

	private void updateTabs() {
		for (int i = tabbedPane.getComponentCount() - 1; i >= 1; --i)
			tabbedPane.remove(i);
		addTabsWithListOfFiles();
		tabbedPane.repaint();
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			if (source == saveBtn) {
				vm.addLogMessage("Parameters are being updated..", Color.BLACK);
				vm.updateAll(maxLocalTA.getText(), procNoTA.getText(), null,
						fileExtensionTA.getText(), filePrefixTA.getText(),
						fileAppendixTA.getText(), null, pathToFilesTA.getText());
				Settings.this.updateTabs();
			} else if (source == cancelBtn) {
				Settings.this.dispose();
				Settings.this.setVisible(false);
			}
		}

	}
}
