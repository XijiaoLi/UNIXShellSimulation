package cs131.pa1.filter.concurrent;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Filter;


public abstract class ConcurrentFilter extends Filter implements Runnable {
	
	protected LinkedBlockingQueue<String> input;
	protected LinkedBlockingQueue<String> output;
	
	//the special string that will signal "isDone"
	protected final String poison_pill = "POISON_PILL"; 	
	
	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}
	
	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter){
			ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
			this.next = sequentialNext;
			sequentialNext.prev = this;
			if (this.output == null){
				//initialize a thread safe data structure to contain the output/input of two adjacent commands
				this.output = new LinkedBlockingQueue<String>();
			}
			sequentialNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}
	
	public Filter getNext() {
		return next;
	}
	
	public void run(){
		//it is not necessary to synchronize here, but just to be safe
		synchronized (input) {
			synchronized (output) {
				//when the previous command is not done, processing all the strings in the input
				while (!isDone()) {
					String line;
					try {
						line = input.take();
						String processedLine = processLine(line);
						//LinkedBlockingQueue does not accept null objects
						if (processedLine != null){
							output.put(processedLine);
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}		
				}
				//after putting each processed string in the output, put the poison pill in 
				try {
					output.put(poison_pill);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			
		}
		
	}
	
	@Override
	public boolean isDone()  {
		//if the input queue is empty, keep checking until a new object shows up
		while (input.isEmpty()){
		}
		//if the front object in the queue(input) is a poison pill, then the last command is done
		if(!input.isEmpty() && input.peek().equals(poison_pill)){
			//delete that poison pill from the queue since it's not needed anymore
			input.poll();
			return true;
		}
		//else, the last command is not done
		return false;
	}
	
	protected abstract String processLine(String line);
	
}
