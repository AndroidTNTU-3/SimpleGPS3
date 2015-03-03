package com.example.simplegpstracker;

import java.security.Provider;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.simplegpstracker.GetPoliLine.PoliLoaderCallBack;
import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.db.KalmanInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.kalman.KalmanManager;
import com.google.android.gms.maps.model.CameraPosition;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/////////////////////////////////////
//Service that call LocationLoader class
//and stored a location to database
////////////////////////////////////

public class TrackService extends Service {
	
	public static interface UnregisterCallBack{
		public void Unregister();
	}
	
	UnregisterCallBack unregisterCallBack;
	
	private SharedPreferences preferences;
	
	private int refreshTime;
	private String providers; 
	
	//The Kalman filter on/off
	private String kalmanFilter;
	
    //run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    
    // timer handling
    private Timer mTimer = null;
    
    //count of satellite
    int count = 0;
    
    private boolean providerReady = false;
    
    GPSInfoHelper helper;
    KalmanInfoHelper kalmanHelper;
    LocationLoader locationLoader;
    private LocationManager locationManager;
    SensorScanner sensor;
    Context context;

    private final IBinder mBinder = new LocalBinder();
    
	KalmanManager km;
	
	List<GPSInfo> list;
	
	CameraPosition cp = null;
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    /**
     * Returns the instance of this service for a client to make method calls on it.
     * @return the instance of this service.
     */
    
    public class LocalBinder extends Binder {
    	TrackService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TrackService.this;
        }
    }
 
    @Override
    public void onCreate() {
        // cancel if already existed
    	preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	refreshTime = Integer.parseInt(preferences.getString("refreshTime", "5"))  * 1000;
    	providers = preferences.getString("providers", "Network");
    	
        context = getApplicationContext();
        
        list = new ArrayList<GPSInfo>();
        
    	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);	
    	
    	kalmanFilter = preferences.getString("kalman", "off");
    	Log.i("DEBUG", " Time:" + refreshTime);
        helper = new GPSInfoHelper(getApplicationContext());
        //helper.cleanOldRecords();
        kalmanHelper = new KalmanInfoHelper(context);
        kalmanHelper.cleanOldRecords();

        
	    km = new KalmanManager(context);
        
        locationLoader = new LocationLoader(context, this);
        sensor = new SensorScanner(this);
        
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        
        ///// schedule task
        //check if any provider is enabled
        
        if(locationLoader.IsProviderEnable()) mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, refreshTime);
        //else service will be stopped
        else {
        	Log.i("DEBUG", "Provider disabled");
        	this.stopSelf();
        }
        //mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, refreshTime);
    }
    
   
    
    public int onStartCommand(Intent intent, int flags, int startId) {  

        return START_STICKY;
      }
 
    class TimeDisplayTimerTask extends TimerTask {
 
        @Override
        public void run() {
            // run on another thread
        	//if(locationLoader.IsProviderEnable()) TrackService.this.stopSelf();
            mHandler.post(new Runnable() {
 
                @Override
                public void run() {

            		Location location = locationLoader.getLocation();
            		GPSInfo info = new GPSInfo();
            		GPSInfo kalmanInfo = new GPSInfo();
            		info = sensor.GetSensorValue(context);
            		//Get count of satellite
            		
       			 	if(isProviderReady()){
            		
	            		if ((location != null)){
	            			//if(location.getAccuracy() < 5){
	            			 
		            			/*if(kalmanFilter.equals("on")){
			            			km.setParam(location);
			                		km.getKalmanLocation();
			                		Location kalmanLocation = new Location(KalmanManager.KALMAN_PROVIDER);
			                		kalmanLocation = km.getKalmanLocation();
	
			                		kalmanInfo.setId(1);
			                		kalmanInfo.setLongitude(kalmanLocation.getLongitude());
			                		kalmanInfo.setLatitude(kalmanLocation.getLatitude());
			                		kalmanInfo.setAccuracy(kalmanLocation.getAccuracy());
			                		//kalmanInfo.setAcceleration(kalmanLocation.getAcceleration());
			                		kalmanInfo.setTitle("Track1");
			                		kalmanInfo.setTime(System.currentTimeMillis());
			                		kalmanHelper.insert(kalmanInfo);
		            			}*/
	            			
		            		info.setId(1);
		            		info.setLongitude(location.getLongitude());
		            		info.setLatitude(location.getLatitude());
		            		info.setAccuracy(location.getAccuracy());
		            		info.setAcceleration(info.getAcceleration());
		            		info.setSpeed(location.getSpeed());
		            		info.setName("Track1");
		            		info.setTime(System.currentTimeMillis());
		            		
		            		synchronized (list) {
		            			list.add(info);
		            		}
		            		//helper.insert(info);
		            		
		            		count = locationLoader.getSatelliteCount();
		            		
		        		 	sendResult(count, location.getLatitude(), location.getLongitude());
		            		Log.i("DEBUG", "Inserted");
	            			}
	            		}
	            		
       			 	}
                //}
 
            });
        }
 
        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }
        
        //If selected provider is "GPS" we check if count of satellite more then 4
        private boolean isProviderReady(){
        	if(providers.equals("Network") && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) return true;
        	else if(providers.equals("GPS") && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        		count = locationLoader.getSatelliteCount();
    			Log.d("DEBUG", "satellite in serv" + count);
    			if (count > 3) return true;
        	}
        	return false;
        }
        
        public void sendResult(int message, double lat, double lng) {
            /*Intent intent = new Intent(MainActivity.SATELLITE_COUNT);
            //if(message != 0)
                intent.putExtra("count", message);
            broadcastSatelliteCount.sendBroadcast(intent);*/
        	Intent local = new Intent();
        	local.putExtra("count", message);
        	local.putExtra("lat", lat);
        	local.putExtra("lng", lng);
        	local.setAction(Contract.SATELLITE_COUNT);

        	sendBroadcast(local);
        }
    }
    
    //get a obtained data(the rout)
    public List<GPSInfo> getList(){
    	return list;
    }
    
    public void setCameraPosition(CameraPosition cameraPosition){
    	cp = cameraPosition;
    }
    
    public CameraPosition getCameraPosition(){
    	return cp;
    }
    
    public void stop(){
        locationLoader.Unregister();
        sensor.Unregister();
    	mTimer.cancel();
    }
    
  //registering callback
  	public void setCallBack(UnregisterCallBack unregisterCallBack) {
  		this.unregisterCallBack = unregisterCallBack;
  	}
  	
    
    public void onDestroy() {
        super.onDestroy();
        
        helper.closeDB();
        
        //prevention of memory leak
        //mHandler.removeCallbacksAndMessages(null);
        
		Toast toast_stop = Toast.makeText(context, context.getResources().getString(R.string.service_stop), Toast.LENGTH_SHORT);
		toast_stop.show();
        Log.d("DEBUG", "MyService onDestroy");
      }
}
