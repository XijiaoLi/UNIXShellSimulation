package cs131.pa1.filter.concurrent;

public class PwdFilter extends ConcurrentFilter {
	public PwdFilter() {
		super();
	}
	
	public void run() {
		try {
			output.put(processLine(""));
			//put the poison pill in the output after putting the directory's string
			output.put(poison_pill);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	public String processLine(String line) {
		return ConcurrentREPL.currentWorkingDirectory;
	}
}
