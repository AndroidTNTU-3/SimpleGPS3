package com.example.simplegpstracker;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.entity.ListRouteParams;
import com.example.simplegpstracker.utils.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RouteAdapter extends BaseAdapter {
	
	private List<ListRouteParams> params;
	private Context context;
	private SharedPreferences preferences;
	private String speedUnitValue;
	private String distanceUnitValue;
	private String speedUnitEntry;
	private String distanceUnitEntry;
	private double koefDistance = 1.0;
	private double koefSpeed = 1.0;
	private String speed;
	private String distance;
	
	
	RouteAdapter(List<ListRouteParams> params, Context context, SharedPreferences preferences){
		this.params = params;
		this.context= context;
		this.preferences = preferences;
		getPreference();
	}
	
	private void getPreference(){
		distanceUnitValue = preferences.getString("distanceUnit", "m");
		speedUnitValue = preferences.getString("speedUnit", "ms");
		distanceUnitEntry = getEntry(distanceUnitValue, R.array.distance_unit, R.array.distance_unit_value);
		speedUnitEntry = getEntry(speedUnitValue, R.array.speed_unit, R.array.speed_unit_value);
	}
	
	private String getEntry(String preferenceValue, int arrayEntry, int arrayValue ){
		String entry = null;
		CharSequence[] entryes = context.getResources().getTextArray(arrayEntry);
		CharSequence[] values = context.getResources().getTextArray(arrayValue);
		int i = Arrays.asList(values).indexOf(preferenceValue); 
		entry = (String) entryes[i];
		return entry;
	}
	
	private String getDistance(float distance){
		DecimalFormat dfd;
		if(distanceUnitValue.equals("km")) {
			koefDistance = 0.001;
			dfd = new DecimalFormat("#.###");
		}else dfd = new DecimalFormat("0");
		return String.valueOf(dfd.format(distance*koefDistance));
	}
	
	private String getSpeed(double speed){
		
		if(speedUnitValue.equals("kmh")) koefSpeed = 60*60*0.001;
		DecimalFormat df = new DecimalFormat("#.##");
		return String.valueOf(df.format(speed*koefSpeed));
	}
	
	

	@Override
	public int getCount() {
		return params.size();
	}

	@Override
	public Object getItem(int position) {	
		return params.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.route_list_row, parent, false);
			
			
			TextView name = (TextView) convertView.findViewById(R.id.tvRouteName);
			TextView startTime = (TextView) convertView.findViewById(R.id.tvRouteStartTime);
			TextView stopTime = (TextView) convertView.findViewById(R.id.tvRouteStopTime);
			TextView duration = (TextView) convertView.findViewById(R.id.tvRouteDuration);
			TextView averageSpeed = (TextView) convertView.findViewById(R.id.tvRouteSpeed);
			TextView distance = (TextView) convertView.findViewById(R.id.tvRouteDistance);
			
			ViewHolder vh = new ViewHolder(name, startTime, stopTime, duration, averageSpeed, distance);
			
			convertView.setTag(vh);
			
		}

		
		ViewHolder vh = (ViewHolder) convertView.getTag();
		
		vh.name.setText(params.get(position).getName());
		vh.startTime.setText(" " + params.get(position).getStartTime());
		vh.stopTime.setText(" " + params.get(position).getStopTime());
		vh.duration.setText(" " + params.get(position).getDuration());
		vh.averageSpeed.setText(" " 
		+ Utils.getFormattedSpeed(params.get(position).getAverageSpeed(), distanceUnitValue) 
		+ " " + speedUnitEntry);
		vh.distance.setText(" " 
		+ Utils.getFormattedDistance(params.get(position).getDistance(), speedUnitValue) 
		+ " " + distanceUnitEntry);

		return convertView;
	}
	
	
	
	private class ViewHolder{
		public final TextView name;
		public final TextView startTime;
		public final TextView stopTime;
		public final TextView duration;
		public final TextView averageSpeed;
		public final TextView distance;

		
		public ViewHolder (TextView name, TextView startTime, TextView stopTime, TextView duration, TextView averageSpeed, TextView distance){
			this.name = name;
			this.startTime = startTime;
			this.stopTime = stopTime;
			this.duration = duration;
			this.averageSpeed = averageSpeed;
			this.distance = distance;
		}
		
		
	}
	
	

}
