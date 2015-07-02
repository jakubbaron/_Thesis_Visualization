package baron.jakub.timetesting;

import java.util.ArrayList;

import baron.jakub.model.Parameters;

public class TestRunner {

	private static int howManyFiles = 504; //5040 //504
	private static int howManyTimesExperiment = 3;//5

	public static void main(String[] args) {
		Parameters.loadProperties("config2015-06-18.properties");
		ArrayList<IFileTester> testers = new ArrayList<IFileTester>();
		testers.add(new FileReader1DArray(howManyFiles, howManyTimesExperiment));
		testers.add(new FileReader3ForVs1For(howManyFiles,
				howManyTimesExperiment));
		testers.add(new FileReader1DArrayMultiThread(howManyFiles,
				howManyTimesExperiment));
		testers.add(new FileReader3ForVs1ForMultiThread(howManyFiles,
				howManyTimesExperiment));
		for (IFileTester ft : testers) {
			ft.runTests();
		}
System.out.println("The end");
	}

}
