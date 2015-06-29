package baron.jakub.model;

public class Filter {

	private static double[] values = new double[] { Double.MIN_VALUE,
			Double.MAX_VALUE };
	private static int[] xCoords = null;
	private static int[] yCoords = null;
	private static int[] zCoords = null;

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
		if (xCoords == null) {
			xCoords = Parameters.getProcNo() != 0 ? new int[] {
					0,
					Parameters.getMaxlocal()
							* Parameters.getProcessorsPerColumn() - 1 }
					: bigCoords();
		}
		return xCoords;
	}

	/**
	 * @return the yCoords
	 */
	public static int[] getyCoords() {
		if (yCoords == null) {
			yCoords = Parameters.getProcNo() != 0 ? new int[] {
					0,
					Parameters.getMaxlocal() * Parameters.getProcessorsPerRow()
							- 1 } : bigCoords();
		}
		return yCoords;
	}

	private static int[] bigCoords() {
		return new int[] { 0, Parameters.getCubeSize() - 1 };
	}

	/**
	 * @return the zCoords
	 */
	public static int[] getzCoords() {
		if (zCoords == null) {
			zCoords = Parameters.getProcNo() != 0 ? new int[] {
					0,
					Parameters.getMaxlocal() * Parameters.getProcessorsLevels()
							- 1 } : bigCoords();
		}
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
