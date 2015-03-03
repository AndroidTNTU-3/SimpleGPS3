package com.example.simplegpstracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.google.gson.Gson;

/////////////////////////////////////
//Packing data from a database to a json
//and send it to server
////////////////////////////////////

public class Transmitter {
	
	Context context;
	private String json;
	private String urlServer;
	private List<GPSInfo> list;
	
	private SharedPreferences preferences;
	
	Transmitter(List<GPSInfo> list, Context context){
		this.context = context;
		this.list = list;
		//get a server URL from preference
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		//test server
		urlServer = preferences.getString("url", "http://hmkcode.appspot.com/jsonservlet");
	}
	
	public void send(){
		
		//get json from database
		getJson();
		
		//send json to your url
		new HttpAsyncTask().execute(urlServer, json);
	}
	
	
	private String getJson(){
		//get waypoints from DB
		//convert to json by Gson
		/*GPSInfoHelper helper = new GPSInfoHelper(context);
	    List<GPSInfo> list = new ArrayList<GPSInfo>();
	    list = helper.getGPSPoint();
	    helper.closeDB();*/
	    json = new Gson().toJson(list);
	    return json;
	}
	
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
 
        	InputStream inputStream = null;
            String result = "";
            String url = params[0];
            String json = params[1];
            HttpClient httpclient = null;
            try {
    
                // 1. create HttpClient
                httpclient = new DefaultHttpClient();
     
                // 2. make POST request
                HttpPost httpPost = new HttpPost(url);
                  
                // 3. json to StringEntity
                StringEntity se = new StringEntity(json);
     
                // 4. set httpPost Entity
                httpPost.setEntity(se);
     
                // 5. Set headers to inform server about the type of the content   
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
     
                // 6. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
     
                // 7. receive response as inputStream if need it
                inputStream = httpResponse.getEntity().getContent();
     
                // 8. convert inputstream to string
                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Ooops!";
     
            } catch (Exception e) {
                Log.d("DEBUG Input Stream:", e.getLocalizedMessage());
            }finally {
                httpclient.getConnectionManager().shutdown();
            }
     
            // 9. return result
            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(context, "Data Sent!", Toast.LENGTH_LONG).show();
        	Log.d("DEBUG Input Stream", "Data sent");
       }
    }
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }  

}
