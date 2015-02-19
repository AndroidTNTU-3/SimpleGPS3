package com.example.simplegpstracker.preference;

import com.example.simplegpstracker.R;
import com.example.simplegpstracker.R.id;
import com.example.simplegpstracker.R.layout;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UrlDialogFragment extends DialogFragment{
	
	private SharedPreferences preferences;
	TextView tv;
	
	
	 @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		    getDialog().setTitle("Title!");
		    View v = inflater.inflate(R.layout.dialog_url, null);
		    tv = (TextView) v.findViewById(R.id.textViewUrl);
		    Button btn = (Button) v.findViewById(R.id.button_set_url);
		    btn.setOnClickListener(new ClickListener());
		    return v;
		  }
	 
	 private class ClickListener implements OnClickListener{

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.button_set_url){
				preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			    Editor ed = preferences.edit();
			    ed.putString("url", tv.getText().toString());
			    ed.commit();
			    dismiss();
			}
			
		}
		 
	 }
	 
	 public void onDismiss(DialogInterface dialog) {
		    super.onDismiss(dialog);
	 	}

		  public void onCancel(DialogInterface dialog) {
		    super.onCancel(dialog);
		  }
	
}
