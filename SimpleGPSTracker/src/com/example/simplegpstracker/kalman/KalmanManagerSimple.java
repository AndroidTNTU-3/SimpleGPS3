package com.example.simplegpstracker.kalman;

import com.example.simplegpstracker.entity.GPSInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

public class KalmanManagerSimple implements KManager{
	
	private Context context;
	private Location location;
	
    public static final String KALMAN_PROVIDER = "kalman";
	
	// Static constant
    private static final int THREAD_PRIORITY = 5;

    private static final double DEG_TO_METER = 111225.0;
    private static final double METER_TO_DEG = 1.0 / DEG_TO_METER;

    private static final double TIME_STEP = 5.0;
    //private static final double COORDINATE_NOISE = 3.0 * METER_TO_DEG;
    private static final double COORDINATE_NOISE = 3.0;
    private static final double ALTITUDE_NOISE = 10.0;
    
	private double timeStepShared;
	private SharedPreferences preferences;
	private float variance; 
	
	float newSpeed;
	double newLatitude;
	double newLongitude;
	double newAccuracy;
	
	private double latitude; // degree
    private double longitude;
    
    public KalmanManagerSimple(Context context){
    	this.context = context;
    	variance = -1;
    }
    
    public void setParam(Location location, GPSInfo info){
    	preferences = PreferenceManager.getDefaultSharedPreferences(context);
		//how to show the route on a map
		timeStepShared = (double)Integer.parseInt(preferences.getString("refreshTime", "5"));
		
		// Reusable
		this.location = location;
        float accuracy = location.getAccuracy();
        
        if (variance < 0) {
            // if variance < 0, object is unitialised, so initialise with current values
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            variance = accuracy * accuracy;
            		
        } else {
        	
            // else apply Kalman filter
            //long duration = newTimeStamp - this.timeStamp;
        	long duration = 5*1000;
            if (duration > 0) {
                // time has moved on, so the uncertainty in the current position increases
                variance += duration * location.getSpeed() * location.getSpeed() / 1000;
                //timeStamp = newTimeStamp;
            }

            // Kalman gain matrix 'k' = Covariance * Inverse(Covariance + MeasurementVariance)
            // because 'k' is dimensionless,
            // it doesn't matter that variance has different units to latitude and longitude
            float k = variance / (variance + accuracy * accuracy);
            // apply 'k'
            latitude += k * (location.getLatitude() - latitude);
            longitude += k * (location.getLongitude() - longitude);
            Log.d("DEBUG:", "lat: " + latitude + " longitude: " + longitude);
            // new Covariance matrix is (IdentityMatrix - k) * Covariance
            variance = (1 - k) * variance;
            //location.setLatitude(latitude);
            //location.setLongitude(longitude);

            // Export new point
        }       
        
    }
    
    public Location getKalmanLocation(){
    	Location location = new Location(KALMAN_PROVIDER);

        // Latitude
        //mLatitudeTracker.predict(0.0);
        location.setLatitude(latitude);

        // Longitude
        //mLongitudeTracker.predict(0.0);
        location.setLongitude(longitude);
        return location;
    }

}
