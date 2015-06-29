package baron.jakub.timetesting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import baron.jakub.model.Parameters;

public class FileReader3ForVs1For {
public static void main(String[] args){
		Parameters.loadProperties("config2015-06-18.properties");
		String line;
		String v = Parameters.getValueSpacer();
		long start = System.nanoTime();
		try (BufferedReader br = new BufferedReader(new FileReader(
				"C:\\time10\\proc001102.res"))) {
			for (int i = 0; i < 96; ++i) {
				for (int j = 0; j < 96; ++j) {
					for (int k = 0; k < 96; ++k) {
						if ((line = br.readLine()) != null) {
							String[] values = line.split(v);
							double e = Double.parseDouble(values[1]);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double time = (System.nanoTime() - start) / 1000000000.0;
		System.out.println("3x for: " + time + "s");
		start = System.nanoTime();
		try (BufferedReader br = new BufferedReader(new FileReader(
				"C:\\time10\\proc003102.res"))) {

			for (int i = 0; i < 96 * 96 * 96; ++i) {
				if ((line = br.readLine()) != null) {
					String[] values = line.split(v);
					double e = Double.parseDouble(values[1]);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		time = (System.nanoTime() - start) / 1000000000.0;
		System.out.println("1x for: " + time + "s");
}
}
