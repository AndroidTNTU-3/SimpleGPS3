package com.example.simplegpstracker.kalman;

import com.example.simplegpstracker.entity.GPSInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

public class KalmanManagerGeo implements KManager {
	
	public static final String KALMAN_PROVIDER = "kalman";
	private static final double DEG_TO_METER = 111225.0;
	private static final double METER_TO_DEG = 1.0 / DEG_TO_METER;
    private static final double COORDINATE_NOISE = 3.0 * METER_TO_DEG;
	
	private double timeStepShared;
	private Context context;
	private SharedPreferences preferences;
	KalmanGeoTracker geoTrackFilter = null;
	private double[] lat_lon;
	
	public KalmanManagerGeo(Context context){
		this.context = context;
		lat_lon = new double[]{0.0, 0.0};
	}

	@Override
	public void setParam(Location location, GPSInfo info) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		//how to show the route on a map
		timeStepShared = (double)Integer.parseInt(preferences.getString("refreshTime", "5"));
		if(geoTrackFilter == null) geoTrackFilter = new KalmanGeoTracker(COORDINATE_NOISE, timeStepShared);
		geoTrackFilter.update_velocity2d(location.getLatitude(), location.getLongitude(), timeStepShared);
	}

	@Override
	public Location getKalmanLocation() {
        // Prepare location
        final Location location = new Location(KALMAN_PROVIDER);
        lat_lon = geoTrackFilter.get_lat_long();
        // Latitude
        //mLatitudeTracker.predict(0.0);
        location.setLatitude(lat_lon[0]);

        // Longitude
        //mLongitudeTracker.predict(0.0);
        location.setLongitude(lat_lon[1]);
        
        return location;
	}

}
