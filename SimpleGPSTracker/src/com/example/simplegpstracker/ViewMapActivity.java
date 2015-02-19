package com.example.simplegpstracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.simplegpstracker.GetPoliLine.PoliLoaderCallBack;
import com.example.simplegpstracker.PointAdapter.PointAdapterCallBack;
import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.db.KalmanInfoHelper;
import com.example.simplegpstracker.db.KalmanInfoHelperT;
import com.example.simplegpstracker.db.ProcessedInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.factory.FactoryBuilder;
import com.example.simplegpstracker.factory.FactoryKalmanBuilder;
import com.example.simplegpstracker.kalman.KalmanManager;
import com.example.simplegpstracker.utils.UtilsGeometry;
import com.example.simplegpstracker.utils.UtilsNet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.maps.android.*;

/////////////////////////////////////
//Get location point and show they on map
////////////////////////////////////

public class ViewMapActivity extends FragmentActivity implements PointAdapterCallBack{
	private LatLng newLatLng;
	private SharedPreferences preferences;
	private String viewRouteParameter; 
	private String travelMode;
	Context context;
	Activity activity;
	
	LinearLayout progressLayout;
	
	PointAdapter pointAdapter;
	KalmanManager km;
	private String kalmanFilter;
	
	FactoryKalmanBuilder fBuilder;
 
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private GPSInfoHelper helper;
	private KalmanInfoHelper kalmanHelper;
	private KalmanInfoHelperT kalmanHelperT;
	private ProcessedInfoHelper processedHelper;
	private List<GPSInfo> list = null;
	
	/////TEST BLOCK
	private List<GPSInfo> list1;
	private ArrayList<LatLng> points1 = null;
	/////TEST BLOCK

	
	private ArrayList<LatLng> realPoints = null;
	private ArrayList<LatLng> allPoints = null;
	
	ArrayList<LatLng> newPoints;
	Marker marker;	
	
	private LatLng destPoint;
	GPSInfo infoMarker;
	GPSInfo infoProcessed;
	
	//info from sensors
	private double accelerate;
	private float gyr_x;
	private float gyr_y;
	private float gyr_z;
	private Menu optionsMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_map);
		context = getApplicationContext();
		activity = this;
		progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
		list = getIntent().getParcelableArrayListExtra("pointsList");
		
		//get parameter how to show the route on a map
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		//how to show the route on a map
		viewRouteParameter = preferences.getString("viewRoute", "marker");
		//get traveling mode
		travelMode = preferences.getString("travelMode", "walking");
		//apply The Kalman filter for smoothing a path  
		kalmanFilter = preferences.getString("kalman", "off");
		
		/////////////////////TEST BLOCK ARRAY for getMapsApiDirectionsUrl()
		newLatLng = new LatLng(49.54965588, 25.59697587);
		list1 = new ArrayList<GPSInfo>();
		list1.add(new GPSInfo(25.59697587, 49.54965588));
		list1.add(new GPSInfo(25.59693394, 49.54960761));
		list1.add(new GPSInfo(25.59685826, 49.54952958));
		list1.add(new GPSInfo(25.59681966, 49.54944831));
		list1.add(new GPSInfo(25.5967799, 49.54936681));
		list1.add(new GPSInfo(25.59672519, 49.54928989));
		list1.add(new GPSInfo(25.59663581, 49.54919522));
		list1.add(new GPSInfo(25.59661421, 49.54913938));
		//list1.add(new GPSInfo(25.59660384, 49.54905421));
		//list1.add(new GPSInfo(25.59657031, 49.54900006));
		//list1.add(new GPSInfo(25.59651712, 49.54895933));
		
		points1 = new ArrayList<LatLng>();
		for(int i=1; i<list1.size(); i++){
			points1.add(new LatLng(list1.get(i).getLatitude(), list1.get(i).getLongitude()));
		}
		///////////////////TEST BLOCK 

		allPoints = new ArrayList<LatLng>();
		pointAdapter = new PointAdapter(context);
		pointAdapter.setPointAdapterCallBack(this);

		//1. check if a dataBase is not empty and get data(list)
		if(getDataDB() == false) {
			Toast toast = Toast.makeText(context, context.getResources().getString(R.string.message_base_empty), Toast.LENGTH_SHORT); 
			toast.show();
			activity.finish();
			return;
		}
		
		//2. Start computing point by google directions

		pointAdapter.startCompute(list);
		
		//3. Get a points array that has been received from GPS  
        getRealPoints();
        	
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		map = mapFragment.getMap();
		
		if (map == null) {
		      finish();
		      return;
		}

		//Show on map way as marker or track			

			map.setInfoWindowAdapter(new InfoWindowAdapter() {

	            // Use default InfoWindow frame
	            @Override
	            public View getInfoWindow(Marker arg0) {
	                return null;
	            }

	            // Defines the contents of the InfoWindow
	            @Override
	            public View getInfoContents(Marker arg0) {

		                // Getting view from the layout file info_window_layout
		                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

		                // Getting the position from the marker
		                LatLng latLng = arg0.getPosition();
	
	
		                // Getting reference to the TextView to set latitude
		                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
	
		                // Getting reference to the TextView to set longitude
		                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
		                
		                // Getting reference to the TextView to set accelerate
		                TextView tvAccel = (TextView) v.findViewById(R.id.tv_accel);
		                
		                // Getting reference to the TextView to set orientation
		                TextView tvGyrX = (TextView) v.findViewById(R.id.tv_gyr_x);
		                TextView tvGyrY = (TextView) v.findViewById(R.id.tv_gyr_y);
		                TextView tvGyrZ = (TextView) v.findViewById(R.id.tv_gyr_z);
				     if(viewRouteParameter.equals("line")){	
		                // Setting the latitude
		                tvLat.setText("Latitude:" + destPoint.latitude);
	
		                // Setting the longitude
		                tvLng.setText("Longitude:"+ destPoint.longitude);
		                
		                // Setting the accelerate
		                DecimalFormat df = new DecimalFormat("#.##");
		                tvAccel.setText(getResources().getString(R.string.accelerate) + ": " + df.format(accelerate) 
		                		+ " " + getResources().getString(R.string.accelerate_value));
		                
		                // Setting the orientation
		                tvGyrX.setText(getResources().getString(R.string.orientation_x) + ": " + df.format(gyr_x));
		                tvGyrY.setText(getResources().getString(R.string.orientation_y) + ": " + df.format(gyr_y));
		                tvGyrZ.setText(getResources().getString(R.string.orientation_z) + ": " + df.format(gyr_z));
		                // Returning the view containing InfoWindow contents
	            	}else{
	            		tvLat.setText("Latitude:" + latLng.latitude);
	            		tvLng.setText("Longitude:"+ latLng.longitude);
	            		DecimalFormat df = new DecimalFormat("#.##");
		                tvAccel.setText(getResources().getString(R.string.accelerate) + ": " + df.format(accelerate) 
		                		+ " " + getResources().getString(R.string.accelerate_value));
	            		
	            	}
	                return v;

	            }
	        });
		
			map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

				@Override
				public void onMapClick(LatLng clickCoords) {
					
					//map.clear();
					
					//get nearest point
					if(viewRouteParameter.equals("line")){
						if (marker != null) {
		                    marker.remove();
		                }
						
						destPoint = PolyUtil.GetTargetPoint(clickCoords, allPoints, false, 5);
						
						if(destPoint != null){
		                
			                //Getting accelerate an other info
							
							LatLng destPointInfo = PolyUtil.GetTargetPoint(clickCoords, realPoints, false, 2);					
							int index = PolyUtil.GetTargetIndex();
							if (index >= list.size()) index = list.size() - 1;
		
							
			                /*infoMarker = new GPSInfo();
			                infoMarker = helper.getPointInfo(index);*/
							
							//get accelerate from db
			                accelerate = list.get(index).getAcceleration();
			                
			                //get orientation from db
			                gyr_x = list.get(index).getGyroscopex();
			                gyr_y = list.get(index).getGyroscopey();
			                gyr_z= list.get(index).getGyroscopez();										                
			                		
							// Creating an instance of MarkerOptions to set position
			                MarkerOptions markerOptions = new MarkerOptions();
		
			                // Setting position on the MarkerOptions
			                markerOptions.position(destPoint);
		
			                // Animating to the currently touched position
			                map.animateCamera(CameraUpdateFactory.newLatLng(destPoint));
		
			                // Adding marker on the GoogleMap
			                marker = map.addMarker(markerOptions);
		
			                // Showing InfoWindow on the GoogleMap
			                marker.showInfoWindow();
						}
					}
					
				}}
			);
		
	}
	
	private boolean getDataDB() {
		helper = new GPSInfoHelper(context);
		
		kalmanHelper = new KalmanInfoHelper(context);
		
		kalmanHelperT = new KalmanInfoHelperT(context);
		
		processedHelper = new ProcessedInfoHelper(context);
		processedHelper.cleanOldRecords();
		infoProcessed = new GPSInfo();
		
		//list = helper.getGPSPoint();
		
        /*if(kalmanHelper.getGPSPoint() != null){
        	//list = new ArrayList<GPSInfo>();
        	if(kalmanFilter.equals("on")) list = kalmanHelper.getGPSPoint();
            else list = helper.getGPSPoint();
        }*/
        
        
        //to start compute we must have 2 points or a database must not be empty
        // && list.size() < 2
        if(list == null) return false;
        else if((list != null) && (list.size() < 2) ) return false;
        else
		return true;
	}
	
	private void getRealPoints() {

		realPoints = new ArrayList<LatLng>();
		
        //get real point LatLng
        for(GPSInfo info: list){
        	realPoints.add(new LatLng(info.getLatitude(), info.getLongitude()));
        }		
	}

	/////////////FOR TEST BLOCK
	
	
	//@params: list - data from database
	//@params: list1 - data from testArray(real points)
	private void addMarkers1() {
		for(GPSInfo info: list1 ){
			newLatLng = new LatLng(info.getLatitude(), info.getLongitude());	
			if (map != null) {
				marker = map.addMarker(new MarkerOptions().position(newLatLng)
						.title("First Point"));				
			}
		}
	}
	/////////////FOR TEST BLOCK
	
	///show path as markers
	private void addMarkers(ArrayList<LatLng> points) {
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_map, menu);
		optionsMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				
		switch (item.getItemId()) {
        case R.id.action_show_marker:
        	map.clear();
        	viewRouteParameter = "marker";
        	/*list = helper.getGPSPoint();
        	processedHelper.cleanOldRecords();
        	pointAdapter.startCompute(list);*/
        	drawOnMap(processedHelper.getLatLngPoint());
        	break;
        case R.id.action_show_lines:
        	map.clear();
        	viewRouteParameter = "line";
        	/*list = helper.getGPSPoint();
        	processedHelper.cleanOldRecords();
        	pointAdapter.startCompute(list);*/
        	drawOnMap(processedHelper.getLatLngPoint());
        	break;
        case R.id.action_show_kalman_t:
        	fBuilder = FactoryBuilder.getFactory(FactoryBuilder.KALMAN_VILLOREN, context);
        	fBuilder.init(helper.getGPSPoint(), context);
        	fBuilder.compute();
        	map.clear();
        	list = kalmanHelperT.getGPSPoint();
        	if(list.size() != 0){
        	pointAdapter.startCompute(list);}
        	break;
        case R.id.action_show_kalman_g:
        	fBuilder = FactoryBuilder.getFactory(FactoryBuilder.KALMAN_GEOTRACK, context);
        	fBuilder.init(helper.getGPSPoint(), context);
        	fBuilder.compute();
        	map.clear();
        	list = kalmanHelperT.getGPSPoint();
        	if(list.size() != 0){
        	processedHelper.cleanOldRecords();
        	pointAdapter.startCompute(list);}
        	break;
        case R.id.action_show_kalman_c:
        	fBuilder = FactoryBuilder.getFactory(FactoryBuilder.KALMAN_PORT_C, context);
        	fBuilder.init(helper.getGPSPoint(), context);
        	fBuilder.compute();
        	map.clear();
        	list = kalmanHelperT.getGPSPoint();
        	if(list.size() != 0){
        	processedHelper.cleanOldRecords();
        	pointAdapter.startCompute(list);}
        	break;
        case R.id.action_show_processed:
        	
        	fBuilder = FactoryBuilder.getFactory(FactoryBuilder.KALMAN_PORT_C, context);
        	fBuilder.init(kalmanHelperT.getGPSPoint(), context);
        	fBuilder.compute();
        	map.clear();
        	list = kalmanHelperT.getGPSPoint();
        	if(list.size() != 0){
        	processedHelper.cleanOldRecords();
        	pointAdapter.startCompute(list);}
        	break;
        default: return super.onOptionsItemSelected(item);
        }
		return true;
	}	

	//4. Get computed points and send to draw on map
	@Override
	public void drawPoli(ArrayList<LatLng> points) {
		
		/*newPoints = new ArrayList<LatLng>();
		for(LatLng p: points){
			newPoints.add(p);	
		}*/
		
		//add parts of points to allPoints array
		/*for(LatLng p: points){
			allPoints.add(p);	
		}*/
		
		for(int i = 1; i < points.size(); i++){
			allPoints.add(points.get(i));
			
			infoProcessed.setLatitude(points.get(i).latitude);
			infoProcessed.setLongitude(points.get(i).longitude);
			infoProcessed.setBearing(UtilsGeometry.getBearing(new LatLng(points.get(i).latitude, points.get(i).longitude)));
			processedHelper.insert(infoProcessed);
		}
		//newPoints = points;
		drawOnMap(points);
	}
	
	private void drawOnMap(ArrayList<LatLng> points){
		
		newLatLng = new LatLng(points.get(0).latitude, points.get(0).longitude);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng,15));
		
		PolylineOptions polyLineOptions = new PolylineOptions();
		polyLineOptions.addAll(points);
		polyLineOptions.width(2);
		polyLineOptions.color(Color.BLUE);
		
		
		if (viewRouteParameter.equals("marker")) addMarkers(points);
		else if(viewRouteParameter.equals("realPoint"))addMarkers(realPoints); 
		else map.addPolyline(polyLineOptions);
		
	}
	
	@Override
	public void isLoadFinished(){
		progressLayout.setVisibility(View.INVISIBLE);
	}
	
	@Override
    public void onDestroy(){
        super.onDestroy();
        helper.closeDB();
    }
	
	
}
