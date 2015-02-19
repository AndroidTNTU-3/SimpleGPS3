package com.example.simplegpstracker.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class UtilsNet {
	
	public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
	
	//check if service is running
	
	public static boolean IsServiceRunning(Context context) {
		 
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
 
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.simplegpstracker.TrackService".equals(service.service.getClassName())) {
                return true;
            }
        }
 
        return false;
    }
	
	//check if activity is background
	public static boolean isApplicationSentToBackground(final Context context) {
	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
	    if (!tasks.isEmpty()) {
	      ComponentName topActivity = tasks.get(0).topActivity;
	      if (!topActivity.getPackageName().equals(context.getPackageName())) {
	        return true;
	      }
	    }

	    return false;
	  }
	
	//check if activity is running
	public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;    
           
        }
    	Log.i("DEBUG", "Activity is closed");
        return false;
    }
	

}
