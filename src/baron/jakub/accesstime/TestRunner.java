package baron.jakub.accesstime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class TestRunner {

	public static void main(String[] args) {
		PrintStream out;
		DateFormat df = new SimpleDateFormat("yyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String filePath = new File("").getAbsolutePath();
		String filename = filePath + "\\tests\\tests" + df.format(c.getTime())
				+ ".arrayAccessTests";
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new PrintStream(new FileOutputStream(file, true));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		int[] sizes = { 384 };
		int noExp = 10;
		IAccessTest t = null;
		System.out.println("\n\n" + c.getTime() + "\nSizes: "
				+ Arrays.toString(sizes) + "Experiments: " + noExp);
		for (int size : sizes) {
			for (int i = 0; i < 4; ++i) {
				t = new OneForAccess(size, noExp);
				t.runTests();
				t = new ThreeForAccess(size, noExp);
				t.runTests();
			}
		}

		System.out.println("The end");
	}

}
