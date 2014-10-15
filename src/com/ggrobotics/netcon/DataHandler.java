/* FRC NetConsole Port for Mac.  
 * 
 * Written By Matthew Gee
 * 2014, Team 4413 GGRobotics
 * 
 */

package com.ggrobotics.netcon;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.io.*;

import javax.swing.JOptionPane;

//Handles Packet Listening and Sending  

//Default Listening Port is 6666;
//Default Sending Port is 6668
public class DataHandler {
	
	//Data for packet sending and retreiving
	private DatagramSocket serverSocket;
	private byte[] receiveData;
	private ListenThread thread;
	private DatagramPacket packet;
	private InetAddress address;

	
	//Constructor, will be used for user input settings
	public DataHandler(int port, int receiveLength, int sendLength) {

		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			
			//Exit Program is another instance is running, it throws error when trying to take a socket
			JOptionPane.showMessageDialog(null, "Another instance is already running");
			System.exit(1);
		}
		receiveData = new byte[receiveLength];
		thread = new ListenThread(this);
		
	}
	
	//Default Constructor
	public DataHandler() {
		
		try {
			serverSocket = new DatagramSocket(6666);
		} catch (SocketException e) {
			e.printStackTrace();
			//Exit Program is another instance is running, it throws error when trying to take a socket

			JOptionPane.showMessageDialog(null, "Another instance is already running");
			System.exit(1);
		}
		receiveData = new byte[5192];
		thread = new ListenThread(this);
		
	}
	
	//Receive a data Packet
	public void Listen() {
		try {
			
			//reconstruct byte array for receiving
			receiveData = new byte[5192];
			
			//receive a packet!
			serverSocket.receive(packet = new DatagramPacket(receiveData, receiveData.length));
			
			//translate into a string and print to console
			String output = new String(packet.getData());
			//address = serverSocket.getInetAddress();
			
			
			NetConsole.window.addText(output, false);
			
			//deconstruct array so remnants of last commands don't get shown.  
			receiveData = null;
			packet = null;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
		
	}
	
	//Send a Packet to the Robot  
	public boolean Send(String message) {
		
		//Add a new line so it gets entered into the robot console 
		message = message.concat("\n");
		
		//Construct ip address of Robot by team number  
		address = this.numberToAddress(NetConsole.window.getTeam());
		
		//deconstruct String into byte array  
		byte[] sendData = message.getBytes();
		
		//deconstruct packet for cleansing
		DatagramPacket packet = null;
		
		//reconstruct packet with default port  
		packet = new DatagramPacket(sendData, sendData.length, address, 6668);
		try {
			//Send all the Packets!
			serverSocket.send(packet);
		} catch (IOException e) {
			NetConsole.window.error("Error Sending Packets");
			e.printStackTrace();
			
			//Just in case we want to know if the proccess was successful
			return false;
		}
		return true;
	
	}
	
	//Set the Address
	public void setAddress(InetAddress addr) {
		this.address = addr;
	}
	
	//Get Socket
	public DatagramSocket getSocket() {
		return serverSocket;
	}
	
	//Get InetAddress
	public InetAddress getAddress() {
		return address;
	}

	//start the Listening Thread
	public void Start() {
		thread.run();
	}
	
	//stop the Listening Thread.  I should probably fix this
	public void Stop() {
		
		thread = null;
	}
	
	//Constructs a team number (ie. 1419) to Ip Address (ie. 10.14.19.2)
	public InetAddress numberToAddress(String string) {
		string = string.trim();
		
		if (string.length() > 4) {
			NetConsole.window.error(" Team Number Invalid");
			return null;
		}
		
		Integer teamNum = Integer.parseInt(string);
		String addr = "10." + String.valueOf((int)teamNum/100) + "." + String.valueOf((int)teamNum%100) + ".2";
		System.out.println(addr);
		try {
			
			return InetAddress.getByName(addr);
		} catch (UnknownHostException e) {
			NetConsole.window.error("Team Number Invalid");
			e.printStackTrace();
			return null;
		} 
		
	}
	
}
