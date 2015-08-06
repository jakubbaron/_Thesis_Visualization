package baron.jakub.arraytesting;

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
				+ ".arrayTests";
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

		// ArrayList<IArrayTests> tests = new ArrayList<IArrayTests>();
		int[] sizes = { 10, 100, 200, 384, 500 };
		// int[] sizes = { 384 };
		int noExp = 10;

		System.out.println("\n\n" + c.getTime() + "\nSizes: "
				+ Arrays.toString(sizes) + "Experiments: " + noExp);
		IArrayTests t = null;
		for (int size : sizes) {
			// tests.add(new ArraysWriteReadLinear(size, noExp));
			// tests.add(new ArraysWriteReadRandom(size, noExp));
			for (int i = 0; i < 4; ++i) {
//				t = new ArraysWriteReadLinear(size, noExp);
				 t = new ArraysWriteReadRandom(size, noExp);
				t.runTests();
			}
		}

		// for (IArrayTests t : tests) {
		// t.runTests();
		// }
		System.out.println("The end");
	}
}
