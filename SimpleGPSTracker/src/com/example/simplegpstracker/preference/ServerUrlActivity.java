package com.example.simplegpstracker.preference;

import com.example.simplegpstracker.R;
import com.example.simplegpstracker.R.id;
import com.example.simplegpstracker.R.layout;
import com.example.simplegpstracker.R.menu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ServerUrlActivity extends Activity {
	SharedPreferences preferences;
	Button bUrlSet;
	EditText etUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_url);
		bUrlSet = (Button) findViewById(R.id.bUrlSet);
		bUrlSet.setOnClickListener(new ClickListener());
		etUrl = (EditText) findViewById(R.id.etUrl);
	}
	
	private class ClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			
			preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    Editor ed = preferences.edit();
		    ed.putString("url_server", etUrl.getText().toString());
		    ed.commit();
			
		}

    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server_url, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
