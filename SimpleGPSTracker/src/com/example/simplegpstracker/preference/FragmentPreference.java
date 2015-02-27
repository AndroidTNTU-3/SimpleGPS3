package com.example.simplegpstracker.preference;

import java.util.Arrays;
import java.util.Locale;

import com.example.simplegpstracker.R;
import com.example.simplegpstracker.R.array;
import com.example.simplegpstracker.R.color;
import com.example.simplegpstracker.R.string;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

@SuppressLint("NewApi")
public class FragmentPreference extends PreferenceFragment implements OnPreferenceChangeListener{
	
	private PreferenceClickListener preferenceClickListener = new PreferenceClickListener();
	private UrlDialogFragment dialogUrl;
	private SharedPreferences preferences;
	private Context context;
	
	private String provider;
	private String travelMode;
	private String refreshTime;
	private String speedUnit;
	private String distanceUnit;
	private String lineWidth;
	private String kalman;
	private String viewRoute;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        context = getActivity();
        dialogUrl = new UrlDialogFragment();
        getPreferenceValue();
        setPreferenceScreen(createPreferences());
    }
	
	private void getPreferenceValue(){
		preferences = PreferenceManager.getDefaultSharedPreferences(context);

		/*provider = preferences.getString("providers", "Network");
		travelMode = preferences.getString("travelMode", "walking");
		refreshTime = preferences.getString("refreshTime", "5");
		lineWidth = preferences.getString("lineWidth", "4");
		viewRoute = preferences.getString("viewRoute", "marker");				
		kalman = preferences.getString("kalman", "Off");*/
		
		refreshTime = preferences.getString("refreshTime", "5");
		lineWidth = preferences.getString("lineWidth", "4");
		provider = getEntry(preferences.getString("providers", "Network"), 
				R.array.location_provider, R.array.location_provider_value);
		
		speedUnit = getEntry(preferences.getString("speedUnit", "ms"), 
				R.array.speed_unit, R.array.speed_unit_value);
		distanceUnit = getEntry(preferences.getString("distanceUnit", "m"), 
				R.array.distance_unit, R.array.distance_unit_value);
		travelMode = getEntry(preferences.getString("travelMode", "walking"),
				R.array.travel_mode, R.array.travel_mode_value);
		viewRoute = getEntry(preferences.getString("viewRoute", "marker"),
				R.array.view_route, R.array.view_route_value);
		kalman = getEntry(preferences.getString("kalman", "off"),
				R.array.kalman_name, R.array.kalman_value);
		
	}
	
	//Set a color text
	private Spannable coloredSummary(String text){
		Spannable coloredText = new SpannableString (text);
		coloredText.setSpan( new ForegroundColorSpan(Color.GRAY), 0, coloredText.length(), 0 );
    	return coloredText;
	}
	
	private String getEntry(String preferenceValue, int arrayEntry, int arrayValue ){
		String entry = null;
		CharSequence[] entryes = context.getResources().getTextArray(arrayEntry);
		CharSequence[] values = context.getResources().getTextArray(arrayValue);
		int i = Arrays.asList(values).indexOf(preferenceValue); 
		entry = (String) entryes[i];
		return entry;
	}
	
	//1.Create PreferenceScreen
	private PreferenceScreen createPreferences() {
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());
		addCategory(root);
		return root;
	}
	//2.Add PreferenceCategory
	private PreferenceCategory addCategory(PreferenceScreen root) {

		PreferenceCategory rootPreferenceCategory = new PreferenceCategory(getActivity());
		PreferenceCategory rootPreferenceCategoryUnit = new PreferenceCategory(getActivity());
		PreferenceCategory rootPreferenceCategoryRoute = new PreferenceCategory(getActivity());
		PreferenceCategory rootPreferenceCategoryAdd = new PreferenceCategory(getActivity());
		rootPreferenceCategory.setTitle(getString(R.string.title_pref_location));
		rootPreferenceCategoryUnit.setTitle(getString(R.string.title_pref_unit));
		rootPreferenceCategoryRoute.setTitle(getString(R.string.title_pref_route));
		rootPreferenceCategoryAdd.setTitle(getString(R.string.title_pref_addition));
				
		root.addPreference(rootPreferenceCategory);		
		addListPreference(rootPreferenceCategory);
		
		root.addPreference(rootPreferenceCategoryUnit);		
		addListUnitPreference(rootPreferenceCategoryUnit);
		
		root.addPreference(rootPreferenceCategoryRoute);		
		addListRoutePreference(rootPreferenceCategoryRoute);
		
		root.addPreference(rootPreferenceCategoryAdd);		
		addListAdditionPreference(rootPreferenceCategoryAdd);
		
		return rootPreferenceCategory;
	}
		
	private void addListPreference(PreferenceCategory rootPreferenceCategory){
		rootPreferenceCategory.addPreference(createLocationListPreference());
		rootPreferenceCategory.addPreference(createRefreshListPreference());
	}
	
	private void addListUnitPreference(PreferenceCategory rootPreferenceCategory){
		rootPreferenceCategory.addPreference(createSpeedUnitListPreference());
		rootPreferenceCategory.addPreference(createDistanceUnitListPreference());
	}
	
	private void addListRoutePreference(PreferenceCategory rootPreferenceCategory){
		rootPreferenceCategory.addPreference(createLineWidthPreference());
		rootPreferenceCategory.addPreference(createViewRouteListPreference());
		rootPreferenceCategory.addPreference(createTravelListPreference());
	}
	
	private void addListAdditionPreference(PreferenceCategory rootPreferenceCategory){
		rootPreferenceCategory.addPreference(createKalmanListPreference());
		rootPreferenceCategory.addPreference(createRootPreference());
	}
	
	private Preference createLocationListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setOnPreferenceChangeListener(this);
    	listPreference.setTitle(getResources().getString(R.string.providers_title));    	
    	listPreference.setSummary(coloredSummary(provider));
    	listPreference.setKey(getResources().getString(R.string.providers_key));
    	listPreference.setEntries(R.array.location_provider);
    	listPreference.setEntryValues(R.array.location_provider_value);
    	return listPreference;
	}
	
	private Preference createRefreshListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setOnPreferenceChangeListener(this);
    	listPreference.setTitle(getResources().getString(R.string.refresh_time_title));
    	listPreference.setSummary(coloredSummary(refreshTime));
    	listPreference.setKey(getResources().getString(R.string.refresh_time_key));
    	listPreference.setEntries(R.array.refresh_time_value);
    	listPreference.setEntryValues(R.array.refresh_time_value);
    	return listPreference;
	}
	
	private Preference createSpeedUnitListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setOnPreferenceChangeListener(this);
    	listPreference.setTitle(getResources().getString(R.string.speed_unit_title));
    	listPreference.setSummary(coloredSummary(speedUnit));
    	listPreference.setKey(getResources().getString(R.string.speed_unit_key));
    	listPreference.setEntries(R.array.speed_unit);
    	listPreference.setEntryValues(R.array.speed_unit_value);
    	return listPreference;
	}
	
	private Preference createDistanceUnitListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setOnPreferenceChangeListener(this);
    	listPreference.setTitle(getResources().getString(R.string.distance_unit_title));
    	listPreference.setSummary(coloredSummary(distanceUnit));
    	listPreference.setKey(getResources().getString(R.string.distance_unit_key));
    	listPreference.setEntries(R.array.distance_unit);
    	listPreference.setEntryValues(R.array.distance_unit_value);
    	return listPreference;
	}
	
	private Preference createViewRouteListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setOnPreferenceChangeListener(this);
    	listPreference.setTitle(getResources().getString(R.string.view_route_title));
    	listPreference.setSummary(coloredSummary(viewRoute));
    	listPreference.setKey(getResources().getString(R.string.view_route_key));
    	listPreference.setEntries(R.array.view_route);
    	listPreference.setEntryValues(R.array.view_route_value);
    	return listPreference;
	}
	
	private Preference createTravelListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setOnPreferenceChangeListener(this);
    	listPreference.setTitle(getResources().getString(R.string.travel_mode_title));
    	listPreference.setSummary(coloredSummary(travelMode));
    	listPreference.setKey(getResources().getString(R.string.travel_mode_key));
    	listPreference.setEntries(R.array.travel_mode);
    	listPreference.setEntryValues(R.array.travel_mode_value);
    	return listPreference;
	}
	
	private Preference createKalmanListPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setOnPreferenceChangeListener(this);
    	listPreference.setTitle(getResources().getString(R.string.kalman_filter_title));
    	listPreference.setSummary(coloredSummary(kalman));
    	listPreference.setKey(getResources().getString(R.string.kalman_filter_key));
    	listPreference.setEntries(R.array.kalman_name);
    	listPreference.setEntryValues(R.array.kalman_value);
    	return listPreference;
	}
	
	private Preference createLineWidthPreference(){
    	ListPreference listPreference = new ListPreference(getActivity());
    	listPreference.setOnPreferenceChangeListener(this);
    	listPreference.setTitle(getResources().getString(R.string.line_width_title));
    	listPreference.setSummary(coloredSummary(lineWidth));
    	listPreference.setKey(getResources().getString(R.string.line_width_key));
    	listPreference.setEntries(R.array.line_width_value);
    	listPreference.setEntryValues(R.array.line_width_value);
    	return listPreference;
	}
	
	private Preference createRootPreference() {
		Preference preference = new Preference(getActivity());
        preference.setTitle(R.string.url_server_title);
        preference.setOnPreferenceClickListener(preferenceClickListener);

        preference.setKey(getResources().getString(R.string.url_server_key));
        
		return preference;
	}
	
	private class PreferenceClickListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            	if (key.equals(getResources().getString(R.string.url_server_key))) {
            	
            	dialogUrl.show(getFragmentManager(), "dlg1");

                return true;
            }

            return false;
        }
    }

	@Override
	public boolean onPreferenceChange(Preference preference, Object object) {
		//String value = object.toString().trim();
	    //String key = preference.getKey();

	    int i = ((ListPreference)preference).findIndexOfValue(object.toString());
	    CharSequence[] entries = ((ListPreference)preference).getEntries();
	    preference.setSummary(entries[i]);
	   // Log.i("DEBUG:", "preference value" + ); 
	    	//preference.setSummary(((ListPreference) preference).getEntry());
		return true;
	}
		
	
}
