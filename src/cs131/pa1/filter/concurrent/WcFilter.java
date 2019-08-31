package cs131.pa1.filter.concurrent;

public class WcFilter extends ConcurrentFilter {
	private int linecount;
	private int wordcount;
	private int charcount;
	
	public WcFilter() {
		super();
	}
	
	public void run() {
		//when the last command is not done, counts out the words/chars/lines in the input strings
			while (!isDone()){
				String line;
				//keep counting when the last command is still putting in new input
				try {
					line = input.take();
					linecount++;
					String[] wct = line.split(" ");
					wordcount += wct.length;
					String[] cct = line.split("|");
					charcount += cct.length;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}		
			}
			//after counting out all input, add the result + poison pill to its output queue
			try {
				output.add(linecount + " " + wordcount + " " + charcount);
				output.put(poison_pill);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
	}

	@Override
	protected String processLine(String line) {
		return null;
	}
}
