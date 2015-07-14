package baron.jakub.timetesting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import baron.jakub.model.Parameters;

public class TestRunner {

	private static int howManyFiles = 54; //5040 //504
	private static int howManyTimesExperiment = 3;//5

	public static void main(String[] args) {
		Parameters.loadProperties("config2015-06-18.properties");
		PrintStream out;
		DateFormat df = new SimpleDateFormat("yyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String filePath = new File("").getAbsolutePath();
		String filename = filePath + "\\tests\\tests" + df.format(c.getTime()) + ".tests";
		File file = new File(filename);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new PrintStream(new FileOutputStream(file,true));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println(c.getTimeZone() + "Files: " + howManyFiles + " experiments: " + howManyTimesExperiment);
		
		
		ArrayList<IFileTester> testers = new ArrayList<IFileTester>();
//		testers.add(new FileReader1DArray(howManyFiles, howManyTimesExperiment)); done 
//		testers.add(new FileReader3ForVs1For(howManyFiles,
//				howManyTimesExperiment));
//		testers.add(new FileReader1DArrayMultiThread(howManyFiles,
//				howManyTimesExperiment));
		testers.add(new FileReader3ForVs1ForMultiThread(howManyFiles,
				howManyTimesExperiment));
		for (IFileTester ft : testers) {
			ft.runTests();
		}
System.out.println("The end");
	}

}
