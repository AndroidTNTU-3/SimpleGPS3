package com.example.simplegpstracker.factory;

import android.location.Location;
import android.util.Log;

import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.kalman.KManager;
import com.example.simplegpstracker.kalman.KalmanManager;

public class FactoryGeo extends FactoryKalmanBuilder{
	
	public FactoryGeo(KManager manager) {
		//super(manager);
		km = manager;
	}
	
	@Override
	public void compute() {
		
		kalmanInfo = new GPSInfo();

		for(GPSInfo info: list){
			//if(info.getAccuracy() < 5){
				Log.i("DEBUG", "accuracy:" + info.getAccuracy());
				fromGeoTrack(info);					
			//}
		}
	}
	
	private void fromGeoTrack(GPSInfo info){
		Location kalmanLocation = new Location(KalmanManager.KALMAN_PROVIDER);
		location.setLatitude(info.getLatitude());
		location.setLongitude(info.getLongitude());
		location.setAccuracy(info.getAccuracy());
		location.setSpeed(info.getSpeed());
		km.setParam(location, info);
 		kalmanLocation = km.getKalmanLocation();		
		
		kalmanInfo.setId(1);
 		kalmanInfo.setLongitude(kalmanLocation.getLongitude());
 		kalmanInfo.setLatitude(kalmanLocation.getLatitude());
 		kalmanInfo.setName("Track1");
 		kalmanInfo.setTime(System.currentTimeMillis());
 		kalmanHelper.insert(kalmanInfo);
	}

}
