package com.example.simplegpstracker.utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;


public class UtilsGeometry {
	
	static Location startingLocation = new Location("starting point");
    static Location endingLocation = new Location("ending point");
    static LatLng tempPoint = null;
    static double bearing;
    
	public static double getBearing(LatLng point){
		if(tempPoint == null) {
			tempPoint = point;
			return 0.0;
		}
		else{
		    startingLocation.setLatitude(tempPoint.latitude);
		    startingLocation.setLongitude(tempPoint.longitude);
		    
		    endingLocation.setLatitude(point.latitude);
		    endingLocation.setLongitude(point.longitude);
		    
		    bearing = startingLocation.bearingTo(endingLocation);
		    tempPoint = point;
			//Log.i("DEBUG:", "Bearing:" + String.valueOf(bearing));
			return bearing;

		}

	}
}
