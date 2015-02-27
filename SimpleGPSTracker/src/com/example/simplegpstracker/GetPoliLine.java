package com.example.simplegpstracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

/////////////////////////////////////
//Get response from the google server and parse a json data
////////////////////////////////////
public class GetPoliLine {
	
	public static interface PoliLoaderCallBack{
		public void setPoli(List<List<HashMap<String, String>>> routes);
	}
	
	PoliLoaderCallBack poliLoaderCallBack;
	
	public void start(String url){
		new HttpConnection().execute(url);
	}

	private class HttpConnection extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... arg) {
			String data = "";
			InputStream iStream = null;
			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(arg[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.connect();
				iStream = urlConnection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						iStream));
				StringBuffer sb = new StringBuffer();
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				data = sb.toString();
				br.close();
			} catch (Exception e) {
				Log.d("Exception while reading url", e.toString());
			} finally {
				try {
					if(iStream != null)
					iStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				urlConnection.disconnect();
			}
			return data;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.i("DEBUG", " In HttpConnection");
			new ParserData().execute(result);
			
		}
	}
	
	private class ParserData extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>{

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			// TODO Auto-generated method stub
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				PathJSONParser parser = new PathJSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {

			//send routes to ViewMapActivity
			
			poliLoaderCallBack.setPoli(routes);
		}
	}
	
	//registering callback
	public void setLoaderCallBack(PoliLoaderCallBack poliLoaderCallBack) {
		this.poliLoaderCallBack = poliLoaderCallBack;
	}

}
