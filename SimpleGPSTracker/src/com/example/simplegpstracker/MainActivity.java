package com.example.simplegpstracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.simplegpstracker.TrackService.LocalBinder;
import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.entity.ListRouteParams;
import com.example.simplegpstracker.preference.PrefActivity;
import com.example.simplegpstracker.preference.PreferenceActivityP;
import com.example.simplegpstracker.utils.Utils;
import com.example.simplegpstracker.utils.UtilsNet;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends FragmentActivity {
			
	/*
	 * Refresh time for timer if sensors not enabled 
	 * (basically when app is starting a first time
	 * in milliseconds
	 */
	private final static int TIMER_TIME_REFRESH = 1000;
	private final static int COLOR_ROUTE = Color.RED;;
					
	private SharedPreferences preferences;
	NotificationManager nm;
	private Editor editor;
	private String provider;
	private String travelMode;
	private int refreshTime;
	private int lineWidth;
	private String status;
	private String routeName;
	
	private GoogleMap map;
	private SupportMapFragment mapFragment;
	private LocationLoader locationLoader;
	private Location location;
	
    private TrackService trackService;
    
	
	private boolean isGPSEnabled;
	private boolean isNetworkEnabled;
	
	private BroadcastReceiver receiverProvider, reciverSatellite;
	
	private LatLng startPoint, endPoint;
	private PolylineOptions polylineOptions;
	
	private MapAdapter mapAdapter;
	
	ImageView ivMap;
	ImageView ivStartStop;
	ImageView ivSend;
	ImageView ivList;
	ImageView ivClose;
	LinearLayout llListRoute;
	private ListView listView;
	private List<ListRouteParams> params;
		
	TextView tvSatelliteCount;
	
	LinearLayout progressLayout;
	RouteAdapter routeAdapter;
	
	private boolean isRouteSaved = false;
	
	//List of point obtained from a sensor
	private List<GPSInfo> list = null;
		
	private List<GPSInfo> listRoute;
	
	//List of point obtained from a dataBase
	private List<GPSInfo> listRoutePoints;
	
	//Temporary list for point getting after screen rotation 
	private List<GPSInfo> temp = null;
	
	private GPSInfoHelper helper = null;
	private LocationManager locationManager;
	
	private Handler h;	
	private Context context;	
	private Timer mTimer;
	
	private Intent iStartService;
	private boolean bound;
	
	private String selectedName;
	
	/*
	 * 0: default
	 * 1: route drawing is RealTime
	 * 2: route drawing obtained from dataBase 
	 */
	private int routeDrawingMode = Contract.DRAWING_MODE_NONE;
		

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext(); 
		startPoint = null; 
		mTimer = new Timer();
		
        list = new ArrayList<GPSInfo>();
        
        ivMap = (ImageView) findViewById(R.id.ivMap);
        ivMap.setOnClickListener(new ClickListener());
        ivStartStop = (ImageView) findViewById(R.id.ivRecord);
        ivStartStop.setOnClickListener(new ClickListener());
        ivSend = (ImageView) findViewById(R.id.ivSend);
        ivSend.setOnClickListener(new ClickListener());
        ivList = (ImageView) findViewById(R.id.ivList);
        ivList.setOnClickListener(new ClickListener());
        ivClose = (ImageView) findViewById(R.id.ivRouteExit);
        ivClose.setOnClickListener(new ClickListener());
        llListRoute = (LinearLayout) findViewById(R.id.llRouteList);
        listView = (ListView) findViewById(R.id.lvMyRoute);
        listView.setOnItemClickListener(new ListListener());
        registerForContextMenu(listView);
        
        //After rotate screen service maybe is a running state
        if(UtilsNet.IsServiceRunning(context)) ivStartStop.setImageResource(R.drawable.stop_selector);

        tvSatelliteCount = (TextView) findViewById(R.id.tvSatelliteCount);
        
        getPreferences();
        
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);	
    	locationLoader = new LocationLoader(context);
    	location = locationLoader.getLocation();
    	
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
		map = mapFragment.getMap();  
		
		/*
		  * We will using this Handler when the app is first running and GPS or Network is not enabled.
		  * Then if a user enable GPS or Network we start TimeDisplayTimerTask to
		  * obtain a current location and refresh a map
		  */
		h = new Handler();	 		
    	
    	getStatus();
    	 if(!isGPSEnabled | !isNetworkEnabled) {
    		   		
    		Double latStored = Double.parseDouble(preferences.getString("lastLocationLat", "0.0"));
    		
    		//Check if last location was obtained and stored
    		if(latStored != 0.0){
    			/*
    			 * check if sensors are available
    			 * if not, set a stored location to a map
    			 */
        		if(location == null){
        			location = new Location("");
    	        	location.setLatitude(Double.parseDouble(preferences.getString("lastLocationLat", "0.0")));
            		location.setLongitude(Double.parseDouble(preferences.getString("lastLocationLng", "0.0")));
        			//location.setLatitude(49.5569729);
        			//location.setLongitude(25.6172429);
    	        }
    		}
    	}else if(isGPSEnabled & isNetworkEnabled){
    		location = locationLoader.getLocation();  

			if(location == null)			 
				mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, TIMER_TIME_REFRESH);
    	}
    	 
		setMapView();
        
    	//This receiver receive provider status for update MainActivity info 
    	receiverProvider = new BroadcastReceiver()
        {   
        @Override
          public void onReceive( Context context, Intent intent )
          {
        	getStatus();
        	Log.i("DEBUG:", "In broadcast");
        	if(isGPSEnabled | isNetworkEnabled){
        		
        		location = locationLoader.getLocation();  

    			if(location == null)			 
    				mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, TIMER_TIME_REFRESH);
    			
        	}
          }
        };
            
        //This receiver receive satellites count for update MainActivity info 
        reciverSatellite = new BroadcastReceiver()
        {   
        @Override
          public void onReceive( Context context, Intent intent )
          {
              int satelliteCount = intent.getIntExtra("count", 0);
              LatLng point = new LatLng(intent.getDoubleExtra("lat", 0.0),
            		  					intent.getDoubleExtra("lng", 0.0));
              drawPoly(point, Color.RED);
              Log.i("DEBUG:", "point" + "lat: " + point.latitude + "lon:" + point.longitude);
              tvSatelliteCount.setText(String.valueOf(satelliteCount));
          }
        };
        		      
        this.registerReceiver(receiverProvider, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        this.registerReceiver(reciverSatellite, new IntentFilter(Contract.SATELLITE_COUNT));
        
        mapAdapter = new MapAdapter(map, context);        		                 		 
			
		 /*if(UtilsNet.IsServiceRunning(context)){
			 bindService(iStartService, sConn, getApplicationContext().BIND_AUTO_CREATE);
		 }*/
		 
		 /*
		  * Draw a route after rotate a screen.
		  * If a route was shown on a screen.
		  * If routeDrivingMode is DRIVING_MODE_DB get data from a dataBase.
		  * And if service is running get data from service
		  */

		 if (savedInstanceState != null) {
	        	
			routeDrawingMode = savedInstanceState.getInt("routeDrivingMode");
			 
			switch (routeDrawingMode) {
			case Contract.DRAWING_MODE_DB:
		 		temp = savedInstanceState.getParcelableArrayList("arrayOfRoutes");
		 		listRoutePoints = savedInstanceState.getParcelableArrayList("arrayOfRoutes");
		 		mapAdapter.setRoute(listRoutePoints);
				break;
			case Contract.DRAWING_MODE_NONE:
				temp = savedInstanceState.getParcelableArrayList("arrayOfRoutes");
		 		list = savedInstanceState.getParcelableArrayList("arrayOfRoutes");
		 		mapAdapter.setRoute(list);
				break;
			default:
				break;
			}
			 	
			drawSavedRoute(temp);
	        	
	     }	 		 
        
    }
    
         
    private class TimeDisplayTimerTask extends TimerTask{

		@Override
		public void run() {
			h.post(new Runnable() {
				
				@Override
				public void run() {
					location = locationLoader.getLocation();
					Log.d("DEBUG:", "In timer");
					if(location != null) {
						setMapView();
						cancel();
					}
				}
			});						
			
		}
    	
    }
        
    
    private void setMapView(){
 		
		if(location != null){
			Log.i("DEBUG:", "In MAP");
			map.setIndoorEnabled(true);
			map.setMyLocationEnabled(true);    			
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),15));
		}
		
    }
    
    /*
     * Drawing a route after reCreate activity
     */
    
    private void drawSavedRoute(List<GPSInfo> tempList){
    	if(tempList != null){
    		for(GPSInfo info: tempList){
    			drawPoly(new LatLng(info.getLatitude(), info.getLongitude()), COLOR_ROUTE);
    		}
    	 }
    }
    
    private void drawPoly(LatLng point, int color){
   		endPoint = point;
   		Log.i("DEBUG:", "point" + "lat: " + point.latitude + "lon:" + point.longitude);
    	if(startPoint != null){
    		
    		map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(point.latitude, point.longitude)));
    		polylineOptions = new PolylineOptions().width(lineWidth).color(color);
    		polylineOptions.add(startPoint, endPoint);
        	map.addPolyline(polylineOptions);
    	}
    	startPoint = endPoint;

    }
    
    private boolean refreshAdapter() {
    	params = new ArrayList<ListRouteParams>();
		//listRoute = new ArrayList<GPSInfo>();
		helper = new GPSInfoHelper(context);
		//listRoute = helper.getGPSPointRoute();
		params = helper.getListParams();
				
		if(params != null){
			routeAdapter = new RouteAdapter(params, context, preferences);
			listView.setAdapter(routeAdapter);
			return true;
		}
		return false;
	}
       
    private void getPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
       /* provider = preferences.getString("providers", "Network");
        travelMode = preferences.getString("travelMode", "walking");
        refreshTime = Integer.parseInt(preferences.getString("refreshTime", "5"));*/
        lineWidth = Integer.parseInt(preferences.getString("lineWidth", "4"));
        Log.i("DEBUG:", "distanceUnit" + preferences.getString("distanceUnit", "m"));
    }
    
    private void getStatus(){

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.i("DEBUG:", "provider GPS" + isGPSEnabled);
    	isNetworkEnabled = locationManager
    			.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    	Log.i("DEBUG:", "provider Net" + isNetworkEnabled);
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            	Intent intentPref = new Intent(this, PrefActivity.class);
            	startActivity(intentPref);
            } else {
            	Intent intentPref = new Intent(this, PreferenceActivityP.class);
            	startActivity(intentPref);
            }
        	
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {

    	super.onCreateContextMenu(menu, v, menuInfo);
		if(v.getId() == R.id.lvMyRoute){
			 MenuInflater inflater = getMenuInflater();
			 inflater.inflate(R.menu.menu_list, menu);
		}
 
     }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
          AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

          switch(item.getItemId()) {
              case R.id.edit:
            	  selectedName = params.get(info.position).getName();
            	  new DialogRenameRoute(selectedName).show(getSupportFragmentManager(), "DialogSaveRoute");
                    return true;
              case R.id.delete:
            selectedName = params.get(info.position).getName();
            helper.deleteRow(selectedName);
            refreshAdapter();    
                    return true;
              default:
                    return super.onContextItemSelected(item);
          }
    }
    

    @Override
    protected void onStart() {
      super.onStart();
      
      iStartService = new Intent(context, TrackService.class);
      if(UtilsNet.IsServiceRunning(context)){
		 getApplicationContext();
		bindService(iStartService, sConn, Context.BIND_AUTO_CREATE);
      }      
      
    }
    
    @Override
    protected void onResume(){
    	super.onResume();    	
        //LocalBroadcastManager.getInstance(this).registerReceiver(receiverProvider, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        //LocalBroadcastManager.getInstance(this).registerReceiver(reciverSatellite, new IntentFilter(SATELLITE_COUNT));
    	//getPreferences();
        getStatus();        
      
    }

    private class ClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {			
			case R.id.ivRecord:		
				Log.d("DEBUG:", "Click enabled" + locationLoader.IsProviderEnable());
				if(locationLoader.IsProviderEnable()){
					routeDrawingMode = Contract.DRAWING_MODE_REAL;
					
			        if (!UtilsNet.IsServiceRunning(context)) {
			            // Bind to LocalService	
			        	ivStartStop.setImageResource(R.drawable.stop_selector);
			            bindService(iStartService, sConn, Context.BIND_AUTO_CREATE);
			            
			            map.clear();
			            startPoint = null;
			            //
			            mapAdapter.setRoute(null);
			            
			            Toast toast_start = Toast.makeText(context, context.getResources().getString(R.string.service_start), Toast.LENGTH_SHORT); 
						toast_start.show(); 
			        }
			        else if(UtilsNet.IsServiceRunning(context)){			        	
			        	
			        	//get obtained a rout data from a Service to be stored in the database 
			        	list = trackService.getList();
			        	
			        	if(list != null && list.size() > 2){
			        		new DialogSaveRoute().show(getSupportFragmentManager(), "DialogSaveRoute");
			        	} else StopRecord();
			        }
				}
				break;
			case R.id.ivMap:
				if(!UtilsNet.isOnline(getApplicationContext())){
					Toast toast = Toast.makeText(context, context.getResources().getString(R.string.network_off), Toast.LENGTH_SHORT); 
					toast.show();				
				}else if(UtilsNet.IsServiceRunning(context)){
					Toast toast = Toast.makeText(context, context.getResources().getString(R.string.service_started), Toast.LENGTH_SHORT); 
					toast.show();
				}else{
					Intent iMap = new Intent(context, ViewMapActivity.class);
					if(routeDrawingMode == Contract.DRAWING_MODE_NONE){
						if(list != null && list.size() < 2)
						new DialogMapAllert().show(getSupportFragmentManager(), "DialogMapAllert");
						else {
							iMap.putParcelableArrayListExtra("pointsList", (ArrayList<? extends Parcelable>) list);
							startActivity(iMap);
						}
					} else if(routeDrawingMode == Contract.DRAWING_MODE_DB){
						if(listRoutePoints != null && listRoutePoints.size() > 2)
						iMap.putParcelableArrayListExtra("pointsList", (ArrayList<? extends Parcelable>) listRoutePoints);
						startActivity(iMap);
					}
				}
				break;
			case R.id.ivSend:
				//new Transmitter(context).send();	
				new DialogSaveRoute().show(getSupportFragmentManager(), "DialogSaveRoute");
				break;	
			case R.id.ivList:
				routeDrawingMode = Contract.DRAWING_MODE_DB;

				if(!llListRoute.isShown()) {
					refreshAdapter();
						llListRoute.setVisibility(View.VISIBLE);	
						listView.setVisibility(View.VISIBLE);
				}
				else llListRoute.setVisibility(View.INVISIBLE);										
				break;
			case R.id.ivRouteExit:
				llListRoute.setVisibility(View.INVISIBLE);	
				break;			
				
			default:
				break;
			}
		
		}
    	
    }       
    
    /*
     *  CallBack from the DialoSaveRoute
     *  If click the SAVE button in DialoSaveRoute:
     *  1. Call stopping a Service
     *  2. Save route to DB
     */
    public void SaveRoute(String name){
    	
    	StopRecord();
    	
    	helper = new GPSInfoHelper(context);
    	routeName = name;
    	
    	if(name.isEmpty()){
    		routeName = "Route name: " + String.valueOf(Utils.getTimeForName(list.get(0).getTime()));
    	}   		
    	
    	for(GPSInfo info: list){
			info.setName(routeName);
			helper.insert(info);
		}   	
    	
    }
    
    /*
     * Stopping a Service
     * If click the NO button in DialoSaveRoute:
     * 1. Call stopping a Service
     */
    public void StopRecord(){
    	routeDrawingMode = Contract.DRAWING_MODE_NONE;
    	
    	ivStartStop.setImageResource(R.drawable.record_selector);
    	
    	trackService.stop();        	
    	unbindService(sConn);
    	
    	//set a points list to MapOnClick 
    	
    	mapAdapter.setRoute(list);
    	//???? maybe it is not necessary to do
    	//locationLoader.Unregister();
    }
    
	public void EditRouteName(String name) {
		helper = new GPSInfoHelper(context);
		helper.updateRow(name, selectedName);
		refreshAdapter();
	}	
    
    private class ListListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			map.clear();
			startPoint = null;
			listRoutePoints = helper.getRoutePoints(params.get(position).getName());
			llListRoute.setVisibility(View.INVISIBLE);	
			LatLng point = null;
			
			drawSavedRoute(listRoutePoints);
	    	/*for(GPSInfo info: listRoutePoints){
	    		point = new LatLng(info.getLatitude(), info.getLongitude());
	    		drawPoly(point, Color.RED);
	    	}*/
	    	
			mapAdapter.setRoute(listRoutePoints);
		}
    } 
    
    void sendNotif() {
		 nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		    Notification notif = new Notification(R.drawable.ic_launcher, "MediaPlayer", 
		      System.currentTimeMillis());
		    
		    Intent intent = new Intent(this, MainActivity.class);
		    intent.putExtra("stations", "");
		    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		    
		    notif.setLatestEventInfo(this, "RadioPlayer", "das", pIntent);
		    
		    notif.flags |= Notification.FLAG_AUTO_CANCEL;
		    
		    nm.notify(1, notif);
		    
		  }
    
    protected void onSaveInstanceState(Bundle outState) {
      
        if(routeDrawingMode == Contract.DRAWING_MODE_DB)
        	outState.putParcelableArrayList("arrayOfRoutes", (ArrayList<? extends Parcelable>) listRoutePoints);
        if(routeDrawingMode == Contract.DRAWING_MODE_NONE)
        	outState.putParcelableArrayList("arrayOfRoutes", (ArrayList<? extends Parcelable>) list);
        
        	outState.putInt("routeDrivingMode", routeDrawingMode);
        	
       super.onSaveInstanceState(outState);
    }

   /* protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        if(savedInstanceState != null){
        	routeDrivingMode = savedInstanceState.getInt("routeDrivingMode");
        	if(routeDrivingMode == DRIVING_MODE_DB)
        		listRoutePoints = savedInstanceState.getParcelableArrayList("arrayOfRoutes"); 
        	
        }

    }*/
    
    @Override
    protected void onPause() {
  	  	super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverProvider);
      	//LocalBroadcastManager.getInstance(this).unregisterReceiver(reciverSatellite);

    }
    
    @Override
    protected void onStop() {
        super.onStop();

        if(location != null){
        	editor = preferences.edit();
        	editor.putString("lastLocationLat", String.valueOf(location.getLatitude()));
        	editor.putString("lastLocationLng", String.valueOf(location.getLongitude()));
        	editor.commit();
        }
        
    }
    
    @Override
    protected void onDestroy() {
    	if(helper != null) helper.closeDB();
    	locationLoader.Unregister();

    	this.unregisterReceiver(receiverProvider);
    	this.unregisterReceiver(reciverSatellite);

		mTimer.cancel();
    	super.onDestroy();
    }   
    
    @Override
    public void onBackPressed() {
    	if(llListRoute.isShown()) {
    		llListRoute.setVisibility(View.INVISIBLE);
    	}
    	else {
           finish();            
        }
    }
    
    ServiceConnection sConn = new ServiceConnection() {       	
	    
	    public void onServiceDisconnected(ComponentName name) {
	        Log.d("DEBUG SER:", "MainActivity onServiceDisconnected");
	        bound = false;
	        trackService = null;
	    }

		@Override
		public void onServiceConnected(ComponentName component, IBinder binder) {
			bound = true;
			 Log.i("DEBUG SER:", "MainActivity onServiceConnected");
			 	LocalBinder mBinder = (LocalBinder) binder;
		        trackService = mBinder.getService();
		        
		        temp = trackService.getList();
		        drawSavedRoute(temp);
		        			
		}			
	 };

}
