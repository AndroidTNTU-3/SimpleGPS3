package com.example.simplegpstracker;

import java.util.Timer;
import java.util.TimerTask;

import com.example.simplegpstracker.TrackService.UnregisterCallBack;
import com.example.simplegpstracker.entity.GPSInfo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorScanner implements UnregisterCallBack{
	
	SensorManager sensorManager;
	Sensor sensorLinAccel;
	Sensor sensorGyroscope;
	Sensor sensorAccel;
	Sensor sensorMagnet;
	Context context;
	Timer timer;
	TrackService service;
	int rotation;
	private double totalAccel = 0.0;
	private GPSInfo info;
	

	
	SensorScanner(TrackService service){
		this.service =service;
		service.setCallBack(this);
	}
	
	public GPSInfo GetSensorValue(Context context){
		service.setCallBack(this);
		info = new GPSInfo();
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	    sensorLinAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    
	    sensorManager.registerListener(listener, sensorLinAccel,SensorManager.SENSOR_DELAY_NORMAL);
	    sensorManager.registerListener(listener, sensorGyroscope,SensorManager.SENSOR_DELAY_NORMAL);
	    sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
	    sensorManager.registerListener(listener, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);
	    
	    info.setAcceleration(totalAccel);
	    getDeviceOrientation();
	    info.setGyroscopex(valuesResult[0]);
	    info.setGyroscopey(valuesResult[1]);
	    info.setGyroscopez(valuesResult[2]);
	    return info;
	    }	

	  float[] valuesLinAccel = new float[3];
	  float[] valuesAccel = new float[3];
	  float[] valuesMagnet = new float[3];
	  
	  float[] r = new float[9];	  	  	  
	  
	  float[] valuesResult = new float[3];

	
	SensorEventListener listener = new SensorEventListener() {

	    @Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
	
	    @Override
	    public void onSensorChanged(SensorEvent event) {
	      switch (event.sensor.getType()) {
	      case Sensor.TYPE_LINEAR_ACCELERATION:
	        for (int i = 0; i < 3; i++) {
	          valuesLinAccel[i] = event.values[i];
	        }
	        totalAccel = Math.sqrt(valuesLinAccel[0] * valuesLinAccel[0] 
	        		+ valuesLinAccel[1] * valuesLinAccel[1] + valuesLinAccel[2] * valuesLinAccel[2]);
	        break;
	      case Sensor.TYPE_ACCELEROMETER:
	          for (int i=0; i < 3; i++){
	            valuesAccel[i] = event.values[i];
	          }        
	          break;
	        case Sensor.TYPE_MAGNETIC_FIELD:
	          for (int i=0; i < 3; i++){
	            valuesMagnet[i] = event.values[i];
	          }  
	          break;
	
	      }
	
	    }
	
	  };
  
  //get orientation
  void getDeviceOrientation() {
	    SensorManager.getRotationMatrix(r, null, valuesAccel, valuesMagnet);
	    SensorManager.getOrientation(r, valuesResult);

	    valuesResult[0] = (float) Math.toDegrees(valuesResult[0]); 
	    valuesResult[1] = (float) Math.toDegrees(valuesResult[1]);
	    valuesResult[2] = (float) Math.toDegrees(valuesResult[2]);
	    return;
	  }
    
  	//unregistering listener after stop service
	@Override
	public void Unregister() {
		if(sensorManager != null)
		sensorManager.unregisterListener(listener);		
	}

}
