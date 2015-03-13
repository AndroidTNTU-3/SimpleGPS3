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

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

/////////////////////////////////////
//Get response from the google server and parse a json data
////////////////////////////////////
public class GetPoliLine1 {
	
	Context context;
	
	private static GetPoliLine1 instance;
	
	public static interface PoliLoaderCallBack1{
		public void setPoli(List<List<HashMap<String, String>>> routes);
	}
	
	PoliLoaderCallBack1 poliLoaderCallBack;
    private static RequestQueue queue = null;
    
    private GetPoliLine1(Context context){
    	this.context = context;
    	queue = getQueue(context);
    }
     
    public static synchronized GetPoliLine1 getInstance(Context context) {
        if (instance == null) {
        	instance = new GetPoliLine1(context);
        }
        return instance;
    }	
	
	public void buildRequest(String url, final int partListCount){
		Log.d("DEBUG:", "count: " + String.valueOf(partListCount) + "-------------------");
		StringRequest jsonObjRequest = new StringRequest(Request.Method.GET, url,
				 new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						//Log.d("DEBUG:", response.toString());
						//longInfo(response.toString());
						Log.d("DEBUG:", response.toString());
						
						//jsonobject1 = response;
						new ParserData(partListCount).execute(response);
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						NetworkResponse networkResponse = error.networkResponse;
						final int status = networkResponse.statusCode;
						Log.d("DEBUGA:", "status:" + status);
						
					}					        

				}	
		);
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(4 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		queue.add(jsonObjRequest);
	}
	

	    public static synchronized RequestQueue getQueue(Context ctx) {
	        if (queue == null) {
	            queue = Volley.newRequestQueue(ctx.getApplicationContext());
	        }
	        return queue;
	    }
	
	
	public void longInfo(String str) {
        if(str.length() > 4000) {
            Log.i("",str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.i("",str);
    } 

	
	private class ParserData extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>{
		int count;
		ParserData(int count){
			this.count = count;
		}

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			// TODO Auto-generated method stub
			List<List<HashMap<String, String>>> routes = null;
			JSONObject jObject;
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
			Log.d("DEBUGA:", "count_asynk_end: " + String.valueOf(count) + "-------------------");
			poliLoaderCallBack.setPoli(routes);
		}
	}
	
	//registering callback
	public void setLoaderCallBack(PoliLoaderCallBack1 poliLoaderCallBack) {
		this.poliLoaderCallBack = poliLoaderCallBack;
	}

}
