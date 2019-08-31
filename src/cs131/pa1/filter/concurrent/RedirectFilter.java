package cs131.pa1.filter.concurrent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cs131.pa1.filter.Filter;
import cs131.pa1.filter.Message;

public class RedirectFilter extends ConcurrentFilter {
	private FileWriter fw;
	
	public RedirectFilter(String line) throws Exception {
		super();
		String[] param = line.split(">");
		if(param.length > 1) {
			if(param[1].trim().equals("")) {
				System.out.printf(Message.REQUIRES_PARAMETER.toString(), line.trim());
				throw new Exception();
			}
			try {
				fw = new FileWriter(new File(ConcurrentREPL.currentWorkingDirectory + Filter.FILE_SEPARATOR + param[1].trim()));
			} catch (IOException e) {
				System.out.printf(Message.FILE_NOT_FOUND.toString(), line);	//shouldn't really happen but just in case
				throw new Exception();
			}
		} else {
			System.out.printf(Message.REQUIRES_INPUT.toString(), line);
			throw new Exception();
		}
	}
	
	public void run() {
		//when the previous command is not done, write out all input string in the file
		while(!isDone()) {
			try {
				String result = processLine(input.take());
				if (result!=null && result.equals(poison_pill)) {
					break;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		//since redirect filter will be the last command, there's no need to add poison pill to its output
	}
	
	public String processLine(String line) {
		try {
			fw.append(line + "\n");
			if(isDone()) {
				fw.flush();
				fw.close();
				return poison_pill;
			}
		} catch (IOException e) {
			System.out.printf(Message.FILE_NOT_FOUND.toString(), line);
		}
		return null;
	}
}
