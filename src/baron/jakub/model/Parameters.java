package baron.jakub.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Properties;

public final class Parameters {
	public static enum plotEnum {
		OpenGL, jzy3D, ImprovedOpenGL
	}

	private static String[] availableTimes;// = { "10", "40", "80" };
	private static int cubeSize;// = 384;
	private static int dataType;// = 1;
	private static String[] dataValues;// = new String[] { "Density", "Energy",
										// "C Variable", "Pressure", "u", "v",
										// "w" };
	private static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private static String fileAppendix;// = "2";
	private static String fileExtension;// = ".res";

	private static String filePrefix;// = "proc";
	private static boolean inversed;// = false;
	private static int maxLocalCubeSize;// = 96;
	private static int maxThreads;// = 4;
	private static boolean normalized;// = true;

	private static String pathToFiles;// = "C:\\time";

	private static String[] plotTypes;// = new String[] { "OpenGL", "jzy3D",
										// "ImprovedOpenGL" };
	private static int procNo;// = 64;
	private static String selectedTime;
	private static boolean tensTicks;// = true;
	private static boolean ticks;// = true;

	private static String valueSpacer;// = " +";
	private static int tensTicksFrequency;// = 10;
	private static int visualizationWidth;// = 800;
	private static int visualizationHeight;// = 800;
	private static int width;// = 1024;
	private static int height;// = 1000;
	private static int processorsLevels;// = 4;
	private static int processorsPerRow;// = 4;
	private static int processorsPerColumn;// = 4;
	private static boolean unix;// = false;
	private static Hashtable<String, ProcessorFile[]> listOfPathsToFiles;
	private static boolean skipping = true;

	/**
	 * @return the avalilableTimes
	 */
	public static String[] getAvailableSeries() {
		return availableTimes;
	}

	public static int getCubeSize() {
		return cubeSize;
	}

	/**
	 * @return the dataType
	 */
	public static int getDataType() {
		return dataType;
	}

	/**
	 * @return the dataValues
	 */
	public static String[] getDataValues() {
		return dataValues;
	}

	/**
	 * @return the dateFormat
	 */
	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @return the fileAppendix
	 */
	public static String getFileAppendix() {
		return fileAppendix;
	}

	/**
	 * @return the fileExtension
	 */
	public static String getFileExtension() {
		if (fileExtension.contains(".")) {
			fileExtension = fileExtension.replaceAll("\\.", "");
		}
		return fileExtension.equals("") || fileExtension == null ? "" : "."
				.concat(fileExtension);
	}

	/**
	 * @return the filePrefix
	 */
	public static String getFilePrefix() {
		return filePrefix;
	}

	/**
	 * @return the maxlocal
	 */
	public static int getMaxlocal() {
		return maxLocalCubeSize;
	}

	public static int getMaxThreads() {
		return Parameters.maxThreads;
	}

	/**
	 * @return the pathToFiles
	 */
	public static String getPathToFiles() {
		return pathToFiles;
	}

	/**
	 * @return the plotTypes
	 */
	public static String[] getPlotTypes() {
		return plotTypes;
	}

	/**
	 * @return the procNo
	 */
	public static int getProcNo() {
		return procNo;
	}

	public static String getSelectedTime() {
		return Parameters.selectedTime;
	}

	/**
	 * @return the valueSpacer
	 */
	public static String getValueSpacer() {
		return valueSpacer;
	}

	/**
	 * @return the inversed
	 */
	public static boolean isInversed() {
		return inversed;
	}

	/**
	 * @return the normalized
	 */
	public static boolean isNormalized() {
		return normalized;
	}

	/**
	 * @return the tensTicks
	 */
	public static boolean isTensTicks() {
		return tensTicks;
	}

	/**
	 * @return the ticks
	 */
	public static boolean isTicks() {
		return ticks;
	}

	private static ProcessorFile[] parseProcessorFiles(String[] arg) {
		ProcessorFile[] result = new ProcessorFile[arg.length];
		try {
			for (int i = 0; i < result.length; ++i) {
				String[] proc = arg[i].split("-");
				result[i] = new ProcessorFile(Integer.parseInt(proc[0]),
						proc[1]);
			}
		} catch (Exception e) {
			throw new InvalidParameterException("Cannot parse for: "
					+ e.getMessage() + "\nPlease check listOfPathsToFiles.");
		}
		return result;
	}

	private static Hashtable<String, ProcessorFile[]> parseReadStringToHashTable(
			String arg) {
		Hashtable<String, ProcessorFile[]> result = new Hashtable<String, ProcessorFile[]>();
		String[] processing = arg.split(";");
		String[] labels = parseReadStringArray(processing[0]);
		if (processing.length <= labels.length) {
			throw new InvalidParameterException(
					"Please check the path to files property");
		}
		for (int i = 1; i <= labels.length; ++i) {
			result.put(labels[i - 1],
					parseProcessorFiles(parseReadStringArray(processing[i])));
		}
		availableTimes = labels;
		selectedTime = availableTimes[0];
		return result;
	}

	public static void loadProperties(String filename)
			throws InvalidParameterException {
		if (filename == null)
			filename = "default.properties";
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(filename);
			prop.load(input);

			Parameters.cubeSize = s2i(prop.getProperty("cubeSize"));
			Parameters.maxLocalCubeSize = s2i(prop
					.getProperty("maxLocalCubeSize"));
			Parameters.procNo = s2i(prop.getProperty("numberOfProcessors"));
			Parameters.normalized = s2b(prop.getProperty("normalized"));
			Parameters.inversed = s2b(prop.getProperty("inversed"));
			Parameters.ticks = s2b(prop.getProperty("ticks"));
			Parameters.tensTicks = s2b(prop.getProperty("tensTicks"));
			Parameters.valueSpacer = prop.getProperty("valueSpacer");
			Parameters.maxThreads = s2i(prop.getProperty("maxThreads"));
			Parameters.dataValues = parseReadStringArray(prop
					.getProperty("dataValues"));
			Parameters.plotTypes = parseReadStringArray(prop
					.getProperty("plotTypes"));
			Parameters.tensTicksFrequency = s2i(prop
					.getProperty("tensTicksFrequency"));
			Parameters.visualizationWidth = s2i(prop
					.getProperty("visualizationWidth"));
			Parameters.visualizationHeight = s2i(prop
					.getProperty("visualizationHeight"));
			Parameters.width = s2i(prop.getProperty("width"));
			Parameters.height = s2i(prop.getProperty("height"));
			Parameters.processorsLevels = s2i(prop
					.getProperty("processorsLevels"));
			Parameters.processorsPerRow = s2i(prop
					.getProperty("processorsPerRow"));
			Parameters.processorsPerColumn = s2i(prop
					.getProperty("processorsPerColumn"));
			Parameters.unix = s2b(prop.getProperty("unix"));
			if (procNo == 0) {
				Parameters.listOfPathsToFiles = parseReadStringToHashTable(prop
						.getProperty("listOfPathsToFiles"));
			} else {
				Parameters.availableTimes = parseReadStringArray(prop
						.getProperty("availableTimes"));
				selectedTime = availableTimes[0];
				Parameters.filePrefix = prop.getProperty("filePrefix");
				Parameters.fileAppendix = prop.getProperty("fileAppendix");
				Parameters.fileExtension = prop.getProperty("fileExtension");
				Parameters.pathToFiles = prop.getProperty("pathToFiles");
				Parameters.listOfPathsToFiles = feedListOfPathsToFiles();
			}

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static String[] parseReadStringArray(String readArray) {
		String[] parsingArray = readArray.replaceAll("\\[|\\]|\\s", "").split(
				"\\,");
		return parsingArray;
	}

	private static boolean s2b(String a) {
		return Boolean.parseBoolean(a);
	}

	private static int s2i(String a) {
		return Integer.parseInt(a);
	}

	private static Hashtable<String, ProcessorFile[]> feedListOfPathsToFiles() {
		Hashtable<String, ProcessorFile[]> result = new Hashtable<String, ProcessorFile[]>();
		for (String s : availableTimes) {
			ProcessorFile[] list = new ProcessorFile[Parameters.getProcNo()];
			for (int i = 0; i < list.length; ++i) {
				list[i] = new ProcessorFile(i, getPathToFiles()
						.concat(s + isUnix()).concat(getFilePrefix())
						.concat(String.format("%03d", i)).concat(s)
						.concat(Parameters.getFileAppendix())
						.concat(Parameters.getFileExtension()));
			}
			result.put(s, list);
		}
		return result;
		// if (Parameters.getListOfPathsToFiles() != null)
		// return Parameters.getListOfPathsToFiles().get(time);
		// list = new ProcessorFile[getProcNo()];
		// for (int i = 0; i < list.length; ++i) {

	}

	public static void saveProperties(String filename) {
		if (filename == null) {
			DateFormat df = new SimpleDateFormat("yyy-MM-dd");
			Calendar c = Calendar.getInstance();
			filename = "config" + df.format(c.getTime()) + ".properties";
		}
		Properties prop = new Properties();
		OutputStream output = null;
		String comment = "An example of the configuration:"
				+ "\ninversed=false"
				+ "\nplotTypes=[OpenGL, jzy3D, ImprovedOpenGL]"
				+ "\nvalueSpacer=\\ +"
				+ "\nfilePrefix=proc"
				+ "\nfileExtension=.res"
				+ "\ncubeSize=384"
				+ "\nmaxLocalCubeSize=96"
				+ "\npathToFiles=C\\:\\time"
				+ "\nmaxThreads=4"
				+ "\ntensTicksFrequency=10"
				+ "\ntensTicks=true"
				+ "\nfileAppendix=2"
				+ "\nticks=true"
				+ "\nnumberOfProcessors=64 if this parameters is zero, then please provide a list of files"
				+ " which are suppoed to be loaded,\nseparated with comas,"
				+ " e.g. C:\\\\time10\\proc000102.res, C:\\\\time10\\\\proc001102.res etc. in the property listOfPathsToFiles"
				+ "\nif it is set to zero, then the rest of parameters like pathToFiles, filePrefix etc. have no effect matter"
				+ "\nplease note that double \\ are needed in case of \\t or \\n"
				+ "\nvisualizationWidth=800"
				+ "\nvisualizationHeight=800"
				+ "\nwidth=1024"
				+ "\nheight=1000"
				+ "\ndataValues=[Density, Energy, C Variable, Pressure, u, v, w]"
				+ "\navailableTimes=[10, 40, 80]"
				+ "\nnormalized=true"
				+ "\nunix=false -> if it's true, then the paths will be using / instead of \\ for the Windows";
		try {
			output = new FileOutputStream(filename);
			prop.setProperty("listOfPathsToFiles", saveListOfFiles());
			prop.setProperty("cubeSize", Integer.toString(cubeSize));
			prop.setProperty("maxLocalCubeSize",
					Integer.toString(maxLocalCubeSize));
			prop.setProperty("numberOfProcessors", Integer.toString(procNo));
			prop.setProperty("availableTimes", Arrays.toString(availableTimes));

			prop.setProperty("normalized", Boolean.toString(normalized));
			prop.setProperty("inversed", Boolean.toString(inversed));
			prop.setProperty("ticks", Boolean.toString(ticks));
			prop.setProperty("tensTicks", Boolean.toString(tensTicks));
			if (procNo != 0) {
				prop.setProperty("filePrefix", filePrefix);
				prop.setProperty("fileAppendix", fileAppendix);
				prop.setProperty("fileExtension", fileExtension);
				prop.setProperty("pathToFiles", pathToFiles);
			}
			prop.setProperty("valueSpacer", valueSpacer);
			prop.setProperty("dataValues", Arrays.toString(dataValues));
			prop.setProperty("plotTypes", Arrays.toString(plotTypes));
			prop.setProperty("maxThreads", Integer.toString(maxThreads));
			prop.setProperty("tensTicksFrequency",
					Integer.toString(tensTicksFrequency));

			prop.setProperty("visualizationWidth",
					Integer.toString(getVisualizationWidth()));
			prop.setProperty("visualizationHeight",
					Integer.toString(getVisualizationHeight()));

			prop.setProperty("width", Integer.toString(width));
			prop.setProperty("height", Integer.toString(height));

			prop.setProperty("processorsLevels",
					Integer.toString(processorsLevels));
			prop.setProperty("processorsPerRow",
					Integer.toString(processorsPerRow));
			prop.setProperty("processorsPerColumn",
					Integer.toString(processorsPerColumn));
			prop.setProperty("unix", Boolean.toString(unix));

			prop.store(output, comment);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static String saveListOfFiles() {
		// TODO Auto-generated method stub
		String result = Arrays.toString(availableTimes);
		for (String s : availableTimes) {
			result += ";[";
			for (ProcessorFile p : getListOfPathsToFiles(s)) {
				result += p + ", ";
			}
			result += "]";
		}
		return result;
	}

	public static void setDataType(int _dataType) {
		Parameters.dataType = _dataType + 1;
	}

	/**
	 * @param fileAppendix
	 *            the fileAppendix to set
	 */
	public static void setFileAppendix(String fileAppendix) {
		Parameters.fileAppendix = fileAppendix;
	}

	/**
	 * @param fileExtension
	 *            the fileExtension to set
	 */
	public static void setFileExtension(String fileExtension) {
		if (fileExtension.contains("."))
			fileExtension = fileExtension.replaceAll("\\.", "");
		Parameters.fileExtension = fileExtension;
	}

	/**
	 * @param filePrefix
	 *            the filePrefix to set
	 */
	public static void setFilePrefix(String filePrefix) {
		Parameters.filePrefix = filePrefix;
	}

	/**
	 * @param inversed
	 *            the inversed to set
	 */
	public static void setInversed(boolean inversed) {
		Parameters.inversed = inversed;
	}

	/**
	 * 
	 * @param maxLocal
	 */
	public static void setMaxLocal(int maxLocal) {
		Parameters.maxLocalCubeSize = maxLocal;
	}

	/**
	 * @param normalized
	 *            the normalized to set
	 */
	public static void setNormalized(boolean normalized) {
		Parameters.normalized = normalized;
	}

	/**
	 * @param pathToFiles
	 *            the pathToFiles to set
	 */
	public static void setPathToFiles(String pathToFiles) {
		Parameters.pathToFiles = pathToFiles;
	}

	/**
	 * @param procNo
	 *            the procNo to set
	 */
	public static void setProcNo(int procNo) {
		Parameters.procNo = procNo;
	}

	public static void setSelectedTime(String t) {
		Parameters.selectedTime = t;
	}

	/**
	 * @param tensTicks
	 *            the tensTicks to set
	 */
	public static void setTensTicks(boolean tensTicks) {
		Parameters.tensTicks = tensTicks;
	}

	/**
	 * @param ticks
	 *            the ticks to set
	 */
	public static void setTicks(boolean ticks) {
		Parameters.ticks = ticks;
	}

	/**
	 * @param valueSpacer
	 *            the valueSpacer to set
	 */
	public static void setValueSpacer(String valueSpacer) {
		Parameters.valueSpacer = valueSpacer;
	}

	public static boolean updateAll(String maxLocal, String procNo,
			String dataType, String fileExtension, String filePrefix,
			String fileAppendix, String valueSpacer, String pathToFiles) {
		try {
			Parameters.maxLocalCubeSize = maxLocal != null ? Integer
					.parseInt(maxLocal) : Parameters.maxLocalCubeSize;
			Parameters.procNo = procNo != null ? Integer.parseInt(procNo)
					: Parameters.procNo;
			Parameters.dataType = dataType != null ? Integer.parseInt(dataType) + 1
					: Parameters.dataType;
			// Parameters.setTime(time != null ? Integer.parseInt(time)
			// : Parameters.getTime());
			Parameters.setFileExtension(fileExtension != null ? (fileExtension)
					: Parameters.getFileExtension());
			Parameters.filePrefix = filePrefix != null ? filePrefix
					: Parameters.filePrefix;
			Parameters.fileAppendix = fileAppendix != null ? fileAppendix
					: Parameters.fileAppendix;
			Parameters.valueSpacer = valueSpacer != null ? valueSpacer
					: Parameters.valueSpacer;
			Parameters.pathToFiles = pathToFiles != null ? pathToFiles
					: Parameters.pathToFiles;
			listOfPathsToFiles = feedListOfPathsToFiles();
			return true;
		} catch (Exception e) {
			System.out.println("Parsing error " + e.getMessage());
			return false;
		}
	}

	public static int getTensTicksFrequency() {
		return Parameters.tensTicksFrequency;
	}

	/**
	 * @return the visualizationWidth
	 */
	public static int getVisualizationWidth() {
		return visualizationWidth;
	}

	/**
	 * @return the visualizationHeight
	 */
	public static int getVisualizationHeight() {
		return visualizationHeight;
	}

	/**
	 * @return the height
	 */
	public static int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public static int getWidth() {
		return width;
	}

	/**
	 * @return the processorsLevel
	 */
	public static int getProcessorsLevels() {
		return processorsLevels;
	}

	/**
	 * @param processorsLevel
	 *            the processorsLevel to set
	 */
	public static void setProcessorsLevel(int processorsLevel) {
		Parameters.processorsLevels = processorsLevel;
	}

	/**
	 * @return the processorsPerRow
	 */
	public static int getProcessorsPerRow() {
		return processorsPerRow;
	}

	/**
	 * @param processorsPerRow
	 *            the processorsPerRow to set
	 */
	public static void setProcessorsPerRow(int processorsPerRow) {
		Parameters.processorsPerRow = processorsPerRow;
	}

	/**
	 * @return the processorsPerColumn
	 */
	public static int getProcessorsPerColumn() {
		return processorsPerColumn;
	}

	/**
	 * @param processorsPerColumn
	 *            the processorsPerColumn to set
	 */
	public static void setProcessorsPerColumn(int processorsPerColumn) {
		Parameters.processorsPerColumn = processorsPerColumn;
	}

	/**
	 * @return the unix
	 */
	public static String isUnix() {
		return Parameters.unix ? "/" : "\\";
	}

	public static ProcessorFile[] getListOfPathsToFiles(String time) {
		return Parameters.listOfPathsToFiles.get(time);
	}

	public static boolean isSkipping() {
		// TODO Auto-generated method stub
		return Parameters.skipping;
	}

	public static void setSkipping(boolean b) {
		Parameters.skipping = b;
	}
}
