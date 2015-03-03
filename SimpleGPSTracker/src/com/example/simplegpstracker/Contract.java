package com.example.simplegpstracker;

public class Contract {
	
	public static enum DrawingMode {MODE_NONE, MODE_DB, MODE_REAL}
		
	/*
	 * Mode of drawing a route on a map
	 * 0: default
	 * 1: route drawing is RealTime
	 * 2: route drawing obtained from dataBase 
	 */
	static final public int DRAWING_MODE_NONE = 0;
	static final public int DRAWING_MODE_REAL = 1;
	static final public int DRAWING_MODE_DB = 2;
	
	//Receiver
	static final public String SATELLITE_COUNT= "com.example.simplegpstracker.satellitecount";
	

	/*
	 * Send obtained data to server
	 * If checkBox in DialogSaveRoute enable
	 */
	static final public int SEND_TO_SERVER_OFF = 0;
	static final public int SEND_TO_SERVER_ON = 1;
}
