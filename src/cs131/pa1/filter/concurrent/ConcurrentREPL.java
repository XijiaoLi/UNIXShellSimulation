package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class ConcurrentREPL {

	static String currentWorkingDirectory;
	static LinkedList<BackGroundCommand> backgroundtask;
	
	public static void main(String[] args){
		currentWorkingDirectory = System.getProperty("user.dir");
		backgroundtask = new LinkedList<BackGroundCommand>();
		Scanner s = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		String command;
		while (true) {
			//obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine().trim();
			
			if (command.equals("exit")) { //the case of exiting the shell
				break;
			} else if (command.equals("repl_jobs")) { //the case of repl jobs
				repl_jobs();
			} else if (command.startsWith("kill")){ //the case of kill
				kill(command);
			} else if (!command.trim().equals("")){ //the case of general command
				
				//test whether the command will run in backgound
				boolean background = false;
				if (command.trim().endsWith("&")){
					background = true;
					
					//add a new object to the background command list
					backgroundtask.add(new BackGroundCommand(backgroundtask.size()+1, command));
					command = command.substring(0, command.length()-1);
				}
				
				//phrasing command and build filters
				ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
				LinkedList<Thread> threadList = new LinkedList<Thread>();
				
				//create a new thread for every filter and start it
				while (filterlist != null) {
					Thread thr1 = new Thread(filterlist);
					threadList.add(thr1);
					thr1.start();
					filterlist = (ConcurrentFilter) filterlist.getNext();
				}
				
				for (Thread t: threadList){
	
					//if the command runs in background, add all the thread to the thread list of the command
					if (background){
						backgroundtask.getLast().add(t);
					} else {
						
						//if the command does not run in background, wait until all the filters finish
						try{
							t.join();
						} catch (InterruptedException e) {}
					}
					
				}
			}
		}
		s.close();
		System.out.print(Message.GOODBYE);
	}
	
	
	public static void repl_jobs(){
		int index = 0;
		for (BackGroundCommand bgc: backgroundtask){
			
			//check the still-alive background command and print them out
			if (bgc.check()){
				System.out.println(bgc.getCommand());
			}
		}
	}
	
	public static void kill(String command){
		
		//check the validation of parameter
		String[] sub = command.trim().split(" ");
		if (sub.length == 1){
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), command);
		} else if (sub.length > 2){
			System.out.printf(Message.INVALID_PARAMETER.toString(), command);
		} else {
			//get the index of the background command to kill; check its validation
			int index = 0;
			try {
				index = Integer.parseInt(sub[1]);
			} catch (NumberFormatException e){
				System.out.printf(Message.INVALID_PARAMETER.toString(), command);
				return;
			}
			if (index < 1 || index > backgroundtask.size()){
				System.out.printf(Message.INVALID_PARAMETER.toString(), command);
			} else {
				//kill the chosen background command
				backgroundtask.get(index-1).kill();
			}
		}
	}
	

}
