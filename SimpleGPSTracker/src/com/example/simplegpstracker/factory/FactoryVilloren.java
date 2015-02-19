package com.example.simplegpstracker.factory;


import android.location.Location;
import android.util.Log;
import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.kalman.KManager;
import com.example.simplegpstracker.kalman.KalmanManager;


public class FactoryVilloren extends FactoryKalmanBuilder {
	
	public FactoryVilloren(KManager manager) {
		//super(manager);
		km = manager;
	}
			
	public void compute(){
		kalmanInfo = new GPSInfo();

		for(GPSInfo info: list){
			//if(info.getAccuracy() < 5){
				Log.i("DEBUG", "accuracy:" + info.getAccuracy());
				fromKalmanManager(info);							
			//}
		}	
	}
		
	private void fromKalmanManager(GPSInfo info) {
		
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
 		kalmanInfo.setAccuracy(kalmanLocation.getAccuracy());
 		//kalmanInfo.setAcceleration(kalmanLocation.getAcceleration());
 		kalmanInfo.setName("Track1");
 		kalmanInfo.setTime(System.currentTimeMillis());
 		kalmanHelper.insert(kalmanInfo);
		
	}

}
