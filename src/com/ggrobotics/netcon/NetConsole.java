/* FRC NetConsole Port for Mac.  
 * 
 * Written By Matthew Gee
 * 2014, Team 4413 GGRobotics
 * 
 */


package com.ggrobotics.netcon;

public class NetConsole  {

	//GUI Object.  Handles all GUI related things.
	protected static NetConsoleGUI window;
	
	//Data Handler. Responsible for Sending and Receiving Packets
	protected static DataHandler handler;
	
	//Entry point for program
	public static void main(String args[]) {
		
		//instantiate window object
		window = new NetConsoleGUI();

		//call init method, which sets up and displays the GUI
		window.init();
		
		//instantiate dataHandler object
		handler = new DataHandler();
		
		//start listening thread (separate from main)
		handler.Start();
		
	}
}
