package baron.jakub.model;

public class Filter {

	private static double[] values = new double[] { Double.MIN_VALUE,
			Double.MAX_VALUE };
	private static int[] xCoords = new int[] { 0,
			Parameters.getMaxlocal() * Parameters.getProcessorsPerColumn() - 1 };
	private static int[] yCoords = new int[] { 0,
			Parameters.getMaxlocal() * Parameters.getProcessorsPerRow() - 1 };
	private static int[] zCoords = new int[] { 0,
			Parameters.getMaxlocal() * Parameters.getProcessorsLevels() - 1 };

	/**
	 * @return the values
	 */
	public static double[] getValues() {
		return values;
	}

	/**
	 * @return the xCoords
	 */
	public static int[] getxCoords() {
		return xCoords;
	}

	/**
	 * @return the yCoords
	 */
	public static int[] getyCoords() {
		return yCoords;
	}

	/**
	 * @return the zCoords
	 */
	public static int[] getzCoords() {
		return zCoords;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public static void setValues(double[] values) {
		Filter.values = values;
	}

	/**
	 * @param xCoords
	 *            the xCoords to set
	 */
	public static void setxCoords(int[] xCoords) {
		Filter.xCoords = xCoords;
	}

	/**
	 * @param yCoords
	 *            the yCoords to set
	 */
	public static void setyCoords(int[] yCoords) {
		Filter.yCoords = yCoords;
	}

	/**
	 * @param zCoords
	 *            the zCoords to set
	 */
	public static void setzCoords(int[] zCoords) {
		Filter.zCoords = zCoords;
	}
}
