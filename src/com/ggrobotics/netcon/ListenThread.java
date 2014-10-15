/* FRC NetConsole Port for Mac.  
 * 
 * Written By Matthew Gee
 * 2014, Team 4413 GGRobotics
 * 
 */


package com.ggrobotics.netcon;

import javax.activation.DataHandler;

//Thread Class to handle listener
public class ListenThread implements Runnable {

	//Requires a DataHandler to use its settings for the method call
	private DataHandler listener;
	
	//Constructor
	public ListenThread(DataHandler listener) {
		this.listener = listener;  
	}
	
	//Method to start listening
	@Override
	public void run() {
		
		while (true) {
			
			//Wait for Packets
			System.out.println("Listening");
			listener.Listen();
			
		}

	}
	
	

}
