package baron.jakub.model;

public class ProcessorFile {
	public int number;
	public String filename;

	public ProcessorFile(int number, String file) {
		this.number = number;
		this.filename = file;
	}

	@Override
	public String toString() {
		return number + "-" + filename;
	}
}
