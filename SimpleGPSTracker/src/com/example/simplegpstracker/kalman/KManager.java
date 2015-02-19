package com.example.simplegpstracker.kalman;

import com.example.simplegpstracker.entity.GPSInfo;

import android.location.Location;


public interface KManager {
	
	public void setParam(Location location, GPSInfo info);
	
	public Location getKalmanLocation();
}
