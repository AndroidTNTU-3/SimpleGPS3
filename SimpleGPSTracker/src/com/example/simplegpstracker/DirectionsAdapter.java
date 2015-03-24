package com.example.simplegpstracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.simplegpstracker.PointAdapter.PointAdapterCallBack;
import com.example.simplegpstracker.db.ProcessedInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.utils.UtilsGeometry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DirectionsAdapter implements PointAdapterCallBack{
	
	private GoogleMap map;
	private Marker marker;	
	private Context context;
	
	private ArrayList<LatLng> realPoints = null;
	private ArrayList<LatLng> allPoints = null;
	private List<GPSInfo> wayPoints = null;
	private List<GPSInfo> processedWayPoints = null;
	
	private ProcessedInfoHelper processedHelper;
	private GPSInfo infoProcessed;
	private PointAdapter pointAdapter;
	
	private String viewRouteParameter;
	private int lineWidth;
	
	LinearLayout progressLayout;

	ExecutorService exec;

	
	DirectionsAdapter(GoogleMap map, Context context, SharedPreferences preferences, LinearLayout progressLayout){
		this.map = map;
		this.context = context;
		viewRouteParameter = preferences.getString(context.getString(R.string.view_route_key), "lines");
		lineWidth = Integer.parseInt(preferences.getString("lineWidth", "4"));
		this.progressLayout = progressLayout;
		init();		
	}
	
	private void init(){
		map.clear();
		allPoints = new ArrayList<LatLng>();
		pointAdapter = new PointAdapter(context);
		pointAdapter.setPointAdapterCallBack(this);

		processedWayPoints = new ArrayList<GPSInfo>();
		processedHelper = new ProcessedInfoHelper(context);
		processedHelper.cleanOldRecords();
		
		exec = Executors.newSingleThreadExecutor();
		Log.d("DEBUGA:", "in init: ");
	}
	
	public void compute(List<GPSInfo> wayPoints){
		this.wayPoints = wayPoints;
		//Start computing point by google directions
		pointAdapter.startCompute(wayPoints);
		progressLayout.setVisibility(View.VISIBLE);
	}

	//Get computed points and send to draw on map
	@Override
	public void drawPoli(final ArrayList<LatLng> points) {
		
		exec.execute(new Runnable() {
			
			@Override
			public void run() {
				for(int i = 1; i < points.size(); i++){
					allPoints.add(points.get(i));
					infoProcessed = new GPSInfo();
					infoProcessed.setLatitude(points.get(i).latitude);
					infoProcessed.setLongitude(points.get(i).longitude);
					infoProcessed.setBearing(UtilsGeometry.getBearing(new LatLng(points.get(i).latitude, points.get(i).longitude)));
					processedWayPoints.add(infoProcessed);
					processedHelper.insert(infoProcessed);
				}
				
			}
		});
		
		if(points.size() != 0) drawOnMap(points);
	}
		

	private void drawOnMap(ArrayList<LatLng> points){
		
		LatLng newLatLng = new LatLng(points.get(0).latitude, points.get(0).longitude);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng,15));
		
		if (viewRouteParameter.equals("marker")) routeAsMarkers(points);
		else {
			PolylineOptions polyLineOptions = new PolylineOptions();
			polyLineOptions.addAll(points);
			polyLineOptions.width(lineWidth);
			polyLineOptions.color(Color.RED);
			map.addPolyline(polyLineOptions);
		}
		
	}
	
	///show path as markers
		private void routeAsMarkers(ArrayList<LatLng> points) {
			if((points != null) && (points.size() != 0)){
			for(LatLng info: points ){	
				if (map != null) {
					marker = map.addMarker(new MarkerOptions().position(info)
							.title("First Point"));
				}
			}
			}
		}
	@Override
	public void isLoadFinished() {
		progressLayout.setVisibility(View.INVISIBLE);
		exec.shutdown();
	}
	
	public List<GPSInfo> getProcessedWayPoints(){
		return processedWayPoints;
	}
		

}
