package com.example.simplegpstracker.kalman;

import com.example.simplegpstracker.entity.GPSInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

public class KalmanManagerC implements KManager{
	
	private Context context;
	private Location location;
	
    public static final String KALMAN_PROVIDER = "kalman";
	
	// Static constant
    private static final int THREAD_PRIORITY = 5;

    private static final double DEG_TO_METER = 111225.0;
    private static final double METER_TO_DEG = 1.0 / DEG_TO_METER;

    private static final double TIME_STEP = 5.0;
    private static final double COORDINATE_NOISE = 3.0 * METER_TO_DEG;
    private static final double ALTITUDE_NOISE = 10.0;
    
    private Kalman2D3 mLatitudeTracker, mLongitudeTracker;
    
	private double timeStepShared;
	private SharedPreferences preferences;
    
    public KalmanManagerC(Context context){
		this.context = context;
	}
    
    public void setParam(Location location, GPSInfo info){    	
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		//how to show the route on a map
		timeStepShared = (double)Integer.parseInt(preferences.getString("refreshTime", "5"));
		
    	this.location = location;
        final double accuracy = location.getAccuracy();
        double velocity = location.getSpeed()* METER_TO_DEG;
        //double velocity = 0.0;
        double position, noise;

        // Latitude
        position = location.getLatitude();
        noise = accuracy * METER_TO_DEG;
        //noise = accuracy;
        
        if (mLatitudeTracker == null) {

        	//mLatitudeTracker = new Tracker1D(TIME_STEP, COORDINATE_NOISE);
        	mLatitudeTracker = new Kalman2D3(position, velocity, timeStepShared, COORDINATE_NOISE);
            //mLatitudeTracker.setState(position, location.getSpeed(), noise);
        }
        else{
        //mLatitudeTracker.predict(0.0);
        mLatitudeTracker.Update(position, velocity, noise);
        }

        // Longitude
        position = location.getLongitude();
        noise = accuracy * Math.cos(Math.toRadians(location.getLatitude())) * METER_TO_DEG ;
        //noise = accuracy;

        if (mLongitudeTracker == null) {

            mLongitudeTracker = new Kalman2D3(position, velocity, timeStepShared, COORDINATE_NOISE);
            //mLongitudeTracker.setState(position, location.getSpeed(), noise);
        }
        else{
        //mLongitudeTracker.predict(0.0);
        mLongitudeTracker.Update(position, velocity, noise);
        }       
        
    }
    
    public Location getKalmanLocation(){
    	final Location location = new Location(KALMAN_PROVIDER);

        // Latitude
        //mLatitudeTracker.predict(0.0);
        location.setLatitude(mLatitudeTracker.getPosition());

        // Longitude
        //mLongitudeTracker.predict(0.0);
        location.setLongitude(mLongitudeTracker.getPosition());
        location.setSpeed((float) (mLongitudeTracker.getVelocity()*DEG_TO_METER));
        Log.i("DEBUG:", "speed:" + String.valueOf(mLongitudeTracker.getVelocity()*DEG_TO_METER));
        return location;

        
    }
}
