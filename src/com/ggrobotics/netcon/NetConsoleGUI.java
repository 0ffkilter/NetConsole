/* FRC NetConsole Port for Mac.  
 * 
 * Written By Matthew Gee
 * 2014, Team 4413 GGRobotics
 * 
 */

package com.ggrobotics.netcon;


import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.BorderFactory;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;



import javax.swing.JFrame;

public class NetConsoleGUI extends JFrame implements ActionListener, KeyListener {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	//ArrayList holding past console entries
	private ArrayList<String> history;	
	private int index;
	private String currentIn;

	//Content Panel for all subcomponents
	private JPanel contentPanel;

	//Main Text Field
	private JTextArea output;
	


	//Components for top bar  
	private JCheckBox status;
	private JTextField team;
	private JLabel ipLabel;
	private JButton clear;

	//Console Input Bar
	private JTextField inputBar;

	//Constructor.  Nothing happening here
	public NetConsoleGUI() {
		super();	
	}

	//Init - Sets up the GUI and shows it.  Adds all components
	public void init() {
		super.setMinimumSize(new Dimension(1200,600));

		//Main content Panel
		contentPanel = new JPanel(new BorderLayout());

		//Top Bar Components and Listeners
		JPanel top = new JPanel();
		top.add(status = new JCheckBox("Ignore Output"));
		status.addActionListener(this);
		top.add(new JLabel("Team Number:"));
		top.add(team = new JTextField("4413", 4));
		team.addActionListener(this);
		top.add(clear = new JButton("Clear Log"));
		clear.addActionListener(this);
		top.add(ipLabel = new JLabel("/10.44.13.2"));
		contentPanel.add(top, BorderLayout.NORTH);

		//Add main content panel and text field
		contentPanel.add(output = new JTextArea(20, 20));
		output.setEditable(false);
		output.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JScrollPane outputPane = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPanel.add(outputPane);

		//Add console bar on bottom with Listeners
		contentPanel.add(inputBar = new JTextField(20), BorderLayout.SOUTH);
		inputBar.addActionListener(this);
		inputBar.addKeyListener(this);

		//Add content Panel to frame
		this.add(contentPanel);

		//Set Up
		super.pack();
		super.setVisible(true);
		super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		history = new ArrayList<String>();
	}

	//Add text to the Log, with an option for newline to be inserted.
	public void addText(String text, boolean newLine) {
		if (!status.isSelected()){
	
			if (output.getText().length() >= 192000) {
				status.setSelected(true);
				output.setText(output.getText().substring(32000));
				status.setSelected(false);
			}
			output.insert(text, output.getText().length());

			if(newLine)
				output.insert("\n", output.getText().length());
			
			output.setCaretPosition(output.getText().length());
			//if (output.getText().length() > 32768)
			//output.setText(output.getText().substring(0, 32768));



		}
	}

	//Throw an error message to the log.  
	public void error(String string) {
		this.addText("[!!!]" + string, true);
	}

	//Return the current ip the console is sending to
	public String getIp() {
		return ipLabel.getText();
	}

	//Return the current set Team Number
	public String getTeam() {
		return team.getText();
	}

	//Change the Ip Label to reflect a new Ip
	public void changeIPLabel(String ip) {
		this.ipLabel.setText(ip);
	}
	
	private void clearText() {
		boolean ignore = status.isSelected();
		try {
			status.setSelected(true);
				this.output.setText("");
			status.setSelected(false);
		} catch (IllegalArgumentException f) {
			f.printStackTrace();
		}
		status.setSelected(ignore);
	}

	//Action Listener Method
	@Override
	public void actionPerformed(ActionEvent e) {

		//Handle Input Bar action (User pressed Enter)
		if (e.getSource() == inputBar) {

			//Log
			System.out.println(NetConsole.handler.getAddress());

			//Add the command to the history if the command is not ""
			if (inputBar.getText().length() != 0)
				if (history.size() != 0) {
					if (!inputBar.getText().equals(history.get(history.size() -1)))
						history.add(inputBar.getText());
				} else {
					history.add(inputBar.getText());
				}

			//Set the Index for scrolling through past commands  
			index = history.size() - 1;

			//Attempt to send the command, and add to console.  
			NetConsole.handler.Send(inputBar.getText());
			this.addText("\n==>" + inputBar.getText(), true);
			inputBar.setText("");


		}

		//If user pressed enter while in the team textfield
		if (e.getSource() == team) {

			//Set new Address to send too
			NetConsole.handler.setAddress(NetConsole.handler.numberToAddress(team.getText()));
			this.addText("Target Ip now set to: " + NetConsole.handler.getAddress().toString(), true);
			this.ipLabel.setText(NetConsole.handler.getAddress().toString());
		}

		//Clear Log
		if (e.getSource() == clear) {
			this.clearText();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub


	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

		// If a key was pressed from the console Bar
		if (e.getSource() == inputBar) {
			System.out.println(index + " : " + history.size());

			//If it was from the up arrow
			if (e.getKeyCode() == KeyEvent.VK_UP) {

				//Iterate through previous commands  
				if (index >= 0) {
					if (index == history.size()-1) {
						if(inputBar.getText().length() != 0)
							currentIn = inputBar.getText();
					}

					//Handle non entered command (user started typing, then hit up)
					if (index == history.size()) {
						inputBar.setText(currentIn);
					} else {
						inputBar.setText(history.get(index));
					}
					index --;
				}

				if (index < 0) index = 0;


			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
		
			//Iterate through previous commands, going down in the list
			if (index <= history.size()-1) {

				inputBar.setText(history.get(index));
				index ++;
			
			//Set the text box to show a command that someone has not entered yet but started typing
			} else if (index == history.size()) {
				inputBar.setText(currentIn);
			}

		}
			
		//Down Arrow pressed event
		} 
	}			


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}



}
