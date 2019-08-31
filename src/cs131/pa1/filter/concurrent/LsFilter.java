package cs131.pa1.filter.concurrent;
import java.io.File;

public class LsFilter extends ConcurrentFilter{
	int counter;
	File folder;
	File[] flist;
	
	public LsFilter() {
		super();
		counter = 0;
		folder = new File(ConcurrentREPL.currentWorkingDirectory);
		flist = folder.listFiles();
	}
	
	@Override
	public void run() {
		while(counter < flist.length) {
			//put the name of each files in the output queue
			try {
				output.put(processLine(""));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		//after done with all files in the directory, put the poison pill in the output 
		try {
			output.put(poison_pill);
		} catch (InterruptedException e) {
		}
	}
	
	@Override
	public String processLine(String line) {
		return flist[counter++].getName();
	}
}
