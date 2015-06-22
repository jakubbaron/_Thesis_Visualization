package baron.jakub.controller.Loaders;

import java.io.DataInputStream;
import java.io.FileInputStream;

import baron.jakub.controller.ViewModifier;
import baron.jakub.model.Parameters;

public class BinaryDataLoader extends DataLoader {
	public BinaryDataLoader(ViewModifier vm, int time) {
		super(vm, time);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadData() {
		int maxLoc = Parameters.getMaxlocal();
		int procNo = Parameters.getProcNo();
		int type = Parameters.getDataType();
		setMaxVal(Double.MIN_VALUE);
		setMinVal(Double.MAX_VALUE);
		defaultParticles();
		// MainView.addLog("Data loading started", Color.BLACK);
		long startTime = System.nanoTime();
		Parameters.setFileAppendix("bin");
		for (int i = 0; i < procNo; ++i) {
			String filename = Parameters.getFilePrefix()
					.concat(String.format("%03d", i))
					.concat(String.format("%02d", this.getTime()))
					.concat(Parameters.getFileAppendix())
					.concat(Parameters.getFileExtension());

			System.out.println(filename);
			int level = i / 16; // z coords
			int col = i % 4; // x coords
			int row = (i % 16) / 4; // y coords
			System.out.println(level + " " + col + " " + row);

			try (DataInputStream dis = new DataInputStream(new FileInputStream(
					Parameters.getPathToFiles().concat(filename)))) {

				for (int z = 0; z < maxLoc; ++z) {
					int zz = level * maxLoc + z;

					for (int y = 0; y < maxLoc; ++y) {
						int yy = row * maxLoc + y;

						for (int x = 0; x < maxLoc; ++x) {
							int xx = col * maxLoc + x;
							double val = 0;
							int d = 0;
							for (d = 0; d < type; ++d)
								val = dis.readDouble();
							for (d = type; d < 7; ++d) {
								dis.readDouble();
							}
							minMax(val);
							particles[zz][yy][xx] = val;
						}
					}
				}

				setLoaded(true);
			} catch (Exception e) {
				System.out.println(e.getMessage() + " "
						+ e.getLocalizedMessage());

			}
			System.gc();
		}
		System.out.println("Time elapsed: "
				+ Double.toString((System.nanoTime() - startTime) / 1000000.0)
				+ " s");
	}

}
