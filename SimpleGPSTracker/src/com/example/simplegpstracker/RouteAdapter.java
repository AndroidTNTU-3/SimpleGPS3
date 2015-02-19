package com.example.simplegpstracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.entity.ListRouteParams;
import com.example.simplegpstracker.utils.Utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RouteAdapter extends BaseAdapter {
	
	private List<ListRouteParams> params;
	private Context context;
	
	
	RouteAdapter(List<ListRouteParams> params, Context context){
		this.params = params;
		this.context= context;
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
		vh.averageSpeed.setText(" " + params.get(position).getAverageSpeed() + " " + context.getResources().getString(R.string.speed_value));
		vh.distance.setText(" " + params.get(position).getDistance() + " " + context.getResources().getString(R.string.distance_value));

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
