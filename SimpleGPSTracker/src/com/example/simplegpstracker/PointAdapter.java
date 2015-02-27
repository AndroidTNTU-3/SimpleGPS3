package com.example.simplegpstracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.simplegpstracker.GetPoliLine.PoliLoaderCallBack;
import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.db.KalmanInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class PointAdapter implements PoliLoaderCallBack{
	
	private SharedPreferences preferences;
	private String travelMode;
	private String kalmanFilter;
	private Context context;
	private GPSInfoHelper helper;
	private KalmanInfoHelper kalmanHelper;
	private List<GPSInfo> list;
	PointAdapterCallBack pointAdapterCallBack;
	int partListCount = 0;
	int iter = 0;
	
	public static interface PointAdapterCallBack{
		public void drawPoli(ArrayList<LatLng> points);
		public void isLoadFinished();
	}
	
	PointAdapter(Context context){
		this.context = context;
		getPreference();
		list = new ArrayList<GPSInfo>();
	}
	/*
	//check if dataBase not empty;
	public boolean isDataAccess(){
		
		//get data from database
		if(!getDataDB()) return false;
		return true;
	}*/

	private void getPreference() {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		//get traveling mode
		travelMode = preferences.getString("travelMode", "walking");
		kalmanFilter = preferences.getString("kalman", "off");
	}
	
	public void startCompute(List<GPSInfo> list){
		this.list = list;
		
		//because a parameter MAX_WAYPOINTS must be less 8 divide a array into parts
		ArrayList<GPSInfo> list8 = new ArrayList<GPSInfo>();
			
			for(int i=0; i<list.size() ; i = i+7){
	            list8 = new ArrayList<GPSInfo>();
	            for(int j=0; j<8; j++){
	                if ((j + i) < list.size()) list8.add(list.get(j + i));
	                else break;                
	            }
	            
	            //get computed point from the google server	if point`s count more then 2 
	            if(list8.size() > 2) {
	            	getTrack(list8);
	            	partListCount++;
	            }
	        }
			
	}
	
	private void getTrack(ArrayList<GPSInfo> list8){
		
		//1. Get URL for multiple waypoints
		String url = getMapsApiDirectionsUrl(list8);
			
		//2. Get array of points from google directions
		GetPoliLine getPoly = new GetPoliLine();
	
		//3. Set a callback. After parsing we will get a new points here in setPoli();
		getPoly.setLoaderCallBack(this);
		getPoly.start(url);
				 
	}
	
	private String getMapsApiDirectionsUrl(ArrayList<GPSInfo> list8) {
		
		StringBuilder waypoints = null;
		int listSize = list8.size();
		
		waypoints = new StringBuilder();
		waypoints.append("waypoints=optimize:true|");
		
		//formation request for google server

		//set origin parameter
		String origin = String.valueOf(list8.get(0).getLatitude()) + "," + String.valueOf(list8.get(0).getLongitude());
		//set destination parameter
		String destination = String.valueOf(list8.get(listSize-1).getLatitude()) + "," + String.valueOf(list8.get(listSize-1).getLongitude());
		
		list8.remove(listSize-1);
		list8.remove(0);
		for(GPSInfo info: list8 ){
			Log.i("DEBUG", " Thislat:" + Double.toString(info.getLongitude()));
			waypoints.append(String.valueOf(info.getLatitude()));
			waypoints.append(",");
			waypoints.append(String.valueOf(info.getLongitude()));
			waypoints.append("|");
		}
				
		waypoints.setLength(waypoints.length() - 1);
		waypoints.append("&");
		waypoints.append("sensor=true&mode=");
		waypoints.append(travelMode);
		
		String params = waypoints.toString();
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + "origin=" + origin + "&" + "destination=" + destination + "&" + params;
		 Log.i("DEBUG", " lat:" + url);
		return url;
		
	}
	
	//callback: driving track on map after get response from google server 
	@Override
	public synchronized void setPoli(List<List<HashMap<String, String>>> routes) {
		ArrayList<LatLng> points = null;
		// traversing through routes
		
		double templat = 0;
		double templng = 0;
		points = new ArrayList<LatLng>();
		if(routes != null && routes.size() != 0){
		for (int i = 0; i < routes.size(); i++) {

			List<HashMap<String, String>> path = routes.get(i);
			Log.d("DEBUG:", "path: " + i + "-----------------------");
			for (int j = 0; j < path.size(); j++) {

				HashMap<String, String> point = path.get(j);
				
				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				
				/*
				 * remove duplicated points,
				 * since polylines data is decoded as <latLng1, latLng2>, <latLng2, latLng3>, <latLng3, latLng4>...
				 */

				if((templat != lat) | (templng != lng)){
					LatLng position = new LatLng(lat, lng);
					points.add(position);
					Log.d("DEBUG:", "lat: " + lat + " lng: " + lng);
				}

				templat = lat;
				templng = lng;

			}
				
		}
		pointAdapterCallBack.drawPoli(points);
		}
		iter++;
		if(iter == partListCount) pointAdapterCallBack.isLoadFinished();
	}
	
	//registering callback
		public void setPointAdapterCallBack(PointAdapterCallBack pointAdapterCallBack) {
			this.pointAdapterCallBack = pointAdapterCallBack;
		}
	

}
