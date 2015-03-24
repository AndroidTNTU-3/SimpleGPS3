package com.example.simplegpstracker.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

public class Utils {
	
	private static double koefDistance = 1.0;
	private static double koefSpeed = 1.0;
	
	/** 
	 Get time in format yyyy/MM/dd HH:mm:ss.
	 
	 @param mills Time in milliseconds.
	 @return Time in string.
	
	*/
	public static String getWideTimeFormat(Long mills){
		
		// New date object from mills
		Date date = new Date(mills);
		// formatter 
		SimpleDateFormat df= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.UK);
		// Pass date object
		String formatted = df.format(date );
		
		return formatted;
	}
	
	/** 
	 Get time in format yyyy/MM/dd
	 
	 @param mills Time in milliseconds.
	 @return Time in string.
	
	*/
	public static String getTimeForName(Long mills){
		
		// New date object from mills
		Date date = new Date(mills);
		// formatter 
		SimpleDateFormat df= new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.UK);
		// Pass date object
		String formatted = df.format(date );
		
		return formatted;
	}
	
	/** 
	 Get time in format HH:mm:ss
	 
	 @param mills Time in milliseconds.
	 @return Time in string.
	
	*/
	public static String getShortTimeFormat(Long mills){
		
		// New date object from mills
		Date date = new Date(mills);
		//Set Time zone
		TimeZone destTz = TimeZone.getTimeZone("GMT");
		
		// formatter 
		SimpleDateFormat df= new SimpleDateFormat("HH:mm:ss", Locale.UK);
		df.setTimeZone(destTz);
		
		// Pass date object
		String formatted = df.format(date );
		
		return formatted;
	}
	
	/** 
	 Get entry from xml array
	 
	 @param preferenceValue in String.
	 @param arrayEntry in int.
	 @param arrayValue in int.
	 @return Entry in string.
	
	*/
	public static String getEntry(String preferenceValue, int arrayEntry, int arrayValue, Context context){
		String entry = null;
		CharSequence[] entryes = context.getResources().getTextArray(arrayEntry);
		CharSequence[] values = context.getResources().getTextArray(arrayValue);
		int i = Arrays.asList(values).indexOf(preferenceValue); 
		entry = (String) entryes[i];
		return entry;
	}
	
	
	/** 
	 Get entry from xml array
	 
	 @param distance in float.
	 @param distanceUnitValue in string.
	 @return Entry in string.
	
	*/
	public static String getFormattedDistance(float distance, String distanceUnitValue){
		DecimalFormat dfd;
		if(distanceUnitValue.equals("km")) {
			koefDistance = 0.001;
			dfd = new DecimalFormat("#.###");
		}else dfd = new DecimalFormat("0");
		return String.valueOf(dfd.format(distance*koefDistance));
	}
	
	public static String getFormattedSpeed(double speed, String speedUnitValue){
		
		if(speedUnitValue.equals("kmh")) koefSpeed = 60*60*0.001;
		DecimalFormat df = new DecimalFormat("#.##");
		return String.valueOf(df.format(speed*koefSpeed));
	}
	
	
}
