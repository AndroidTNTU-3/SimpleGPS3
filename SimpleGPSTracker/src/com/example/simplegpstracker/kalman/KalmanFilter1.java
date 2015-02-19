package com.example.simplegpstracker.kalman;

import android.location.Location;

public class KalmanFilter1 {
	
	private long timeStamp; // millis
    private double latitude; // degree
    private double longitude; // degree
    private float variance; // P matrix. Initial estimate of error
    private float variance1;
    Location location;
    public static final String KALMAN_PROVIDER = "kalman";
    
    public KalmanFilter1() {
        variance = -1;
    }
	
	public void setState(double latitude, double longitude, long timeStamp, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
        this.variance = accuracy * accuracy;
    }
	
	public Location process(float newSpeed, double newLatitude, double newLongitude, long newTimeStamp, float newAccuracy) {
        // Uncomment this, if you are receiving accuracy from your gps
        // if (newAccuracy < Constants.MIN_ACCURACY) {
        //      newAccuracy = Constants.MIN_ACCURACY;
        // }
		
		
        if (variance < 0) {
            // if variance < 0, object is unitialised, so initialise with current values
            setState(newLatitude, newLongitude, newTimeStamp, newAccuracy);
        } else {
        	location = new Location(KALMAN_PROVIDER);
            // else apply Kalman filter
            long duration = newTimeStamp - this.timeStamp;
            duration = 5*1000;
            if (duration > 0) {
                // time has moved on, so the uncertainty in the current position increases
                variance += duration * newSpeed * newSpeed / 1000;
                timeStamp = newTimeStamp;
            }

            // Kalman gain matrix 'k' = Covariance * Inverse(Covariance + MeasurementVariance)
            // because 'k' is dimensionless,
            // it doesn't matter that variance has different units to latitude and longitude
            float k = variance / (variance + newAccuracy * newAccuracy);
            // apply 'k'
            latitude += k * (newLatitude - latitude);
            longitude += k * (newLongitude - longitude);
            // new Covariance matrix is (IdentityMatrix - k) * Covariance
            variance = (1 - k) * variance;
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            // Export new point
        }
        return location;

    }

}
