package com.example.simplegpstracker.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

public class Utils {
	
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
	 
	 @param PreferenceValue in String.
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
	
	
}
