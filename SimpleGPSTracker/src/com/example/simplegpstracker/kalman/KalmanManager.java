package com.example.simplegpstracker.kalman;

import com.example.simplegpstracker.entity.GPSInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class KalmanManager implements KManager{
	
    /**
     * Provider string assigned to predicted Location objects.
     */
    public static final String KALMAN_PROVIDER = "kalman";
	
	// Static constant
    private static final int THREAD_PRIORITY = 5;

    private static final double DEG_TO_METER = 111225.0;
    private static final double METER_TO_DEG = 1.0 / DEG_TO_METER;

    private static final double TIME_STEP = 5.0;
    private static final double COORDINATE_NOISE = 3.0 * METER_TO_DEG;
    private static final double ALTITUDE_NOISE = 10.0;
    
    /**
     * Three 1-dimension trackers, since the dimensions are independent and can avoid using matrices.
     */
    private Tracker1D mLatitudeTracker, mLongitudeTracker, mAltitudeTracker;
	
    private Location location;
	private Location mLastLocation;
	
	private double timeStepShared;
	private Context context;
	private SharedPreferences preferences;
	public KalmanManager(Context context){
		this.context = context;
	}
	
	public void setParam(Location location, GPSInfo info){
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		//how to show the route on a map
		timeStepShared = (double)Integer.parseInt(preferences.getString("refreshTime", "5"));
		
		// Reusable
		this.location = location;
        final double accuracy = location.getAccuracy();
        double position, noise;

        // Latitude
        position = location.getLatitude();
        noise = accuracy * METER_TO_DEG;
        
        
        if (mLatitudeTracker == null) {

        	//mLatitudeTracker = new Tracker1D(TIME_STEP, COORDINATE_NOISE);
        	mLatitudeTracker = new Tracker1D(timeStepShared, COORDINATE_NOISE);
            mLatitudeTracker.setState(position, 0.0, noise);
        }
        //else{
        mLatitudeTracker.predict(info.getAcceleration());
        mLatitudeTracker.update(position, noise);
        //}

        // Longitude
        position = location.getLongitude();
        noise = accuracy * Math.cos(Math.toRadians(location.getLatitude())) * METER_TO_DEG ;

        if (mLongitudeTracker == null) {

            mLongitudeTracker = new Tracker1D(timeStepShared, COORDINATE_NOISE);
            mLongitudeTracker.setState(position, 0.0, noise);
        }
        //else{
        mLongitudeTracker.predict(info.getAcceleration());
        mLongitudeTracker.update(position, noise);
        //}
        
        // Update last location
        if (mLastLocation == null ) {

            mLastLocation = new Location(location);
        }
	}
	
	public Location getKalmanLocation(){
		// Calculate prediction
        //mLongitudeTracker.predict(0.0);
		//mLongitudeTracker.predict(location.getSpeed());
        /*if (mLastLocation.hasAltitude())
            mAltitudeTracker.predict(0.0);*/

        // Prepare location
        final Location location = new Location(KALMAN_PROVIDER);

        // Latitude
        //mLatitudeTracker.predict(0.0);
        location.setLatitude(mLatitudeTracker.getPosition());

        // Longitude
        //mLongitudeTracker.predict(0.0);
        location.setLongitude(mLongitudeTracker.getPosition());
        
	    
	    Log.i("DEBUG", " lon In kalman:" + Double.toString(location.getLatitude()));
		
		Log.i("DEBUG", " lon In kalman:" + Double.toString(location.getLongitude()));

        // Altitude
        /*if (mLastLocation.hasAltitude()) {

            mAltitudeTracker.predict(0.0);
            location.setAltitude(mAltitudeTracker.getPosition());
        }*/

        // Speed
        if (mLastLocation.hasSpeed())
            location.setSpeed(mLastLocation.getSpeed());

        /*// Bearing
        if (mLastLocation.hasBearing())
            location.setBearing(mLastLocation.getBearing());*/

        // Accuracy (always has)
        location.setAccuracy((float) (mLatitudeTracker.getAccuracy() * DEG_TO_METER));

        // Set times
        location.setTime(System.currentTimeMillis());

       /* if (Build.VERSION.SDK_INT >= 17)
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());*/
        
        return location;
	}

}
