package baron.jakub.controller;

import java.awt.Color;
import java.security.InvalidParameterException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import baron.jakub.model.Filter;
import baron.jakub.model.Parameters;
import baron.jakub.model.ProcessorFile;
import baron.jakub.view.MainView;

public class ViewModifier {

	public static enum AXIS {
		X, Y, Z
	}

	public ProcessorFile[] getListOfFiles(String time) {
		ProcessorFile[] list;
		if (Parameters.getListOfPathsToFiles() != null)
			return Parameters.getListOfPathsToFiles().get(time);
		list = new ProcessorFile[getProcNo()];
		for (int i = 0; i < list.length; ++i) {
			list[i] = new ProcessorFile(i, getPathToFiles()
					.concat(time + isUnix()).concat(getFilePrefix())
					.concat(String.format("%03d", i)).concat(time)
					.concat(this.getFileAppendix())
					.concat(this.getFileExtension()));
		}
		return list;
	}

	public static void main(String[] args) {
		try {
			Parameters.loadProperties("config2015-06-18.properties");

			ViewModifier vm = new ViewModifier();
			vm.start();
		} catch (InvalidParameterException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
		}
		Parameters.saveProperties(null);
	}

	private MainView mv;
	private int prevDatType;

	private Parameters.plotEnum prevType;

	private ExecutorService taskExecutor;

	public ViewModifier() {
		taskExecutor = Executors.newFixedThreadPool(1);

		// prevType = Parameters.plotEnum.values()[0];

	}

	public void createMainView() {
		mv = new MainView(this);
	}

	public void start() {
		if (mv == null)
			mv = new MainView(this);
		mv.setVisible(true);
	}

	public void addLogMessage(String msg, Color col) {
		mv.addLog(msg, col);
	}

	public void addLogTime(long startTime) {
		mv.addLog(
				"Time elapsed: "
						+ Double.toString((System.nanoTime() - startTime) / 1000000000.0)
						+ " s", Color.BLACK);
	}

	public void changeGraph(Parameters.plotEnum type, int datType) {
		if (prevType != type || prevDatType != datType) {
			Parameters.setDataType(datType);
			taskExecutor.execute(new Runnable() {

				@Override
				public void run() {
					long startTime = System.nanoTime();
					mv.addVisObject(prevType, type);
					addLogTime(startTime);
					prevType = type;
				}
			});
		}

	}

	public String[] getAvailableSeries() {
		// TODO Auto-generated method stub
		return Parameters.getAvailableSeries();
	}

	public String getFileAppendix() {
		return Parameters.getFileAppendix();
	}

	public String getFileExtension() {
		return Parameters.getFileExtension();
	};

	public String getFilePrefix() {
		return Parameters.getFilePrefix();
	}

	public int getMaxlocal() {
		return Parameters.getMaxlocal();
	}

	public int getMaxThreads() {
		// TODO Auto-generated method stub
		return Parameters.getMaxThreads();
	}

	public String getPathToFiles() {
		return Parameters.getPathToFiles();
	}

	public int getProcNo() {
		return Parameters.getProcNo();
	}

	public String getSelectedTime() {
		// TODO Auto-generated method stub
		return Parameters.getSelectedTime();
	}

	public String getValueSpacer() {
		return Parameters.getValueSpacer();
	}

	public void saveLastSettings(String filename) {
		Parameters.saveProperties(filename);
	}

	public void setAllUnloaded() {
		// TODO Auto-generated method stub
		mv.setAllUnloaded();
	}

	public void setSelectedTime(int i) {
		String ti = Parameters.getAvailableSeries()[i];
		Parameters.setSelectedTime(ti);
		mv.changeTime(ti);

	}

	public void updateAll(String maxLocal, String procNo, String dataType,
			String time, String filePrefix, String fileAppendix,
			String valueSpacer, String pathToFiles) {
		boolean success = Parameters.updateAll(maxLocal, procNo, dataType,
				time, filePrefix, fileAppendix, valueSpacer, pathToFiles);
		if (success) {
			mv.changeTime(getSelectedTime());
			addLogMessage("Parameters updated", Color.BLACK);
		} else {
			addLogMessage("Error while updating", Color.RED);
		}
	}

	public void updateFilter(int[] values, ViewModifier.AXIS a) {
		switch (a) {
		case X:
			Filter.setxCoords(values);
			break;
		case Y:
			Filter.setyCoords(values);
			break;
		case Z:
			Filter.setzCoords(values);
			break;
		default:
			break;
		}
	}

	public void updateFPS(double fps) {
		mv.updateFPS(fps);
	}

	public void updateMinMax() {
		mv.updateMinMax();
	}

	public int getTensTicksFrequency() {
		// TODO Auto-generated method stub
		return Parameters.getTensTicksFrequency();
	}

	public int getCubeSize() {
		// TODO Auto-generated method stub
		return Parameters.getCubeSize();
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return Parameters.getHeight();
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return Parameters.getHeight();
	}

	public int getVisualizationWidth() {
		// TODO Auto-generated method stub
		return Parameters.getVisualizationWidth();
	}

	public int getVisualizationHeight() {
		// TODO Auto-generated method stub
		return Parameters.getVisualizationHeight();
	}

	public int getProcColumns() {
		// TODO Auto-generated method stub
		return Parameters.getProcessorsPerColumn();
	}

	public int getProcRows() {
		// TODO Auto-generated method stub
		return Parameters.getProcessorsPerRow();
	}

	public int getProcLevels() {
		// TODO Auto-generated method stub
		return Parameters.getProcessorsLevels();
	}

	public String isUnix() {
		return Parameters.isUnix() ? "/" : "\\";
	}
}
