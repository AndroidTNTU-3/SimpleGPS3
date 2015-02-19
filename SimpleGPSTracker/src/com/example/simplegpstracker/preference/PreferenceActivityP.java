package com.example.simplegpstracker.preference;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class PreferenceActivityP extends PreferenceActivity{
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_pref);
		 getFragmentManager().beginTransaction()
	        .replace(android.R.id.content, new FragmentPreference()).commit();

	}

}
