package cs131.pa1.filter.concurrent;

public class PrintFilter extends ConcurrentFilter {
	public PrintFilter() {
		super();
	}
	
	public void run() {
		//when the previous command is not done, print out all the lines in the input
		while(!isDone()) {
			try {
				String line = input.take();
				processLine(line);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			//since print filter will always be the last command, no need to put a poison pill in the output
		}
	}
	
	public String processLine(String line) {
		System.out.println(line);
		return null;
	}
}
