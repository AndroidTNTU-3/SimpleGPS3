package com.example.simplegpstracker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class TestMap extends FragmentActivity {
	private GoogleMap map;
	private SupportMapFragment mapFragment;
	int startStop;
	
	ImageView button_start_stop;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startStop = 1;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
		map = mapFragment.getMap();
		
		button_start_stop = (ImageView) findViewById(R.id.ivRecord);
		button_start_stop.setOnClickListener(new MyClick());

    }
    
    private class MyClick implements OnClickListener{

		@Override
		public void onClick(View v) {
	    	startStop = -startStop;
			int id = v.getId();
			if (id == R.id.ivRecord) {
				if(startStop == 1)
				button_start_stop.setImageResource(R.drawable.stop_selector);
				else button_start_stop.setImageResource(R.drawable.record_selector);
			} 
		}
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    protected void onResume(){
    	super.onResume();
        
    }
}
