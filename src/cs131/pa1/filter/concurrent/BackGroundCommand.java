package cs131.pa1.filter.concurrent;

import java.util.LinkedList;

public class BackGroundCommand {
	boolean alive;
	String command;
	LinkedList<Thread> threadList;
	
	public BackGroundCommand(int index, String c){
		alive = true;
		command = "\t" + index + ". " + c;
		threadList = new LinkedList<Thread>();
	}
		
	// to add the certain thread to the thread list of the background command
	public void add(Thread t){
		threadList.add(t);
	}
	
	// check whether the process is ended or not
	public boolean check(){
		if (alive) {
			for (Thread t: threadList){
				if (t.isAlive()) {
					return true;
				}
			}
		}
		return false;
	}
	
	// to get the command of the background process
	public String getCommand(){
		return command;
	}

	// to kill the background process
	public void kill(){
		alive = false;
		for (Thread t: threadList){
			t.interrupt();
		}
	}
	
	// to get the thread list of the background command
	public LinkedList<Thread> getThreadList() {
		return threadList;
	}
}
