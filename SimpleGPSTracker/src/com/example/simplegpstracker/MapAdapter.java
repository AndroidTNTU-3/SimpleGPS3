package com.example.simplegpstracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

public class MapAdapter {
	
	private GoogleMap map;
	private Context context;
	private List<GPSInfo> listRoutePoints;
	private List<LatLng> listLatLngPoints;
	private LatLng destPointInfo;
	private GPSInfo destInfo = null;
	private GPSInfoHelper helper = null;
	private Marker marker;
	TextView tvLat;
	TextView tvLng;
	TextView tvAccel;
	
	
	MapAdapter(GoogleMap map, Context context){
		this.map = map;
		this.context = context;
		helper = new GPSInfoHelper(context);
		initMap();
	}
	
	public void setRoute(List<GPSInfo> listRoutePoints){
		this.listRoutePoints = listRoutePoints;
	}
	
	private void initMap(){
				
		
		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			@Override
			public View getInfoContents(Marker marker) {
				return null;
			}

			@Override
			public View getInfoWindow(Marker marker) {
				
				// Getting view from the layout file info_window_layout
                View v = inflater.inflate(R.layout.info_window_layout, null);
                
                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                
                // Getting reference to the TextView to set accelerate
                TextView tvAccel = (TextView) v.findViewById(R.id.tv_accel);
                
             // Getting reference to the TextView to set speed
                TextView tvSpeed = (TextView) v.findViewById(R.id.tv_speed);

                // Getting the position from the marker
                LatLng latLng = marker.getPosition();
                
                tvLat.setText("Latitude: " + destInfo.getLatitude());
        		tvLng.setText("Longitude: "+ destInfo.getLongitude());
        		DecimalFormat df = new DecimalFormat("#.##");
                tvAccel.setText(context.getResources().getString(R.string.accelerate) + ": " + df.format(destInfo.getAcceleration()) 
                		+ " " + context.getResources().getString(R.string.accelerate_value));
                tvSpeed.setText(context.getResources().getString(R.string.speed) + ": " + df.format(destInfo.getSpeed()) 
                		+ " " + context.getResources().getString(R.string.speed_value));            
                
				return v;
			}
			
		});
		
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

			@Override
			public void onMapClick(LatLng clickCoords) {
				
				if (marker != null) {
                    marker.remove();
                }

				listLatLngPoints = getArrayLatLng();
				if(listLatLngPoints != null){
					destPointInfo = PolyUtil.GetTargetPoint(clickCoords, listLatLngPoints, false, 2);
					//destInfo = helper.getPoint(destPointInfo);
					int index = PolyUtil.GetTargetIndex();
					if (index >= listRoutePoints.size()) index = listRoutePoints.size() - 1;
					destInfo = listRoutePoints.get(index);
					
					if(destInfo != null){
							                
		             // Creating an instance of MarkerOptions to set position
		                MarkerOptions markerOptions = new MarkerOptions();
	
		                // Setting position on the MarkerOptions
		                if(destPointInfo != null){
		                markerOptions.position(destPointInfo);
		                
		                markerOptions.icon((BitmapDescriptorFactory.fromResource(R.drawable.marker)));
	
		                // Animating to the currently touched position
		                map.animateCamera(CameraUpdateFactory.newLatLng(destPointInfo));
	
		                // Adding marker on the GoogleMap
		                marker = map.addMarker(markerOptions);
	
		                // Showing InfoWindow on the GoogleMap
		                marker.showInfoWindow();
		                }
					}
				}
				
			}
			
		});
		
		
	}

	private List<LatLng> getArrayLatLng() {
		List<LatLng> list = new ArrayList<LatLng>();
		
       
		if(listRoutePoints != null){
	        for(GPSInfo info: listRoutePoints){
	        	list.add(new LatLng(info.getLatitude(), info.getLongitude()));
	        }	
			return list;
		}
		return null;
	}
	

}
