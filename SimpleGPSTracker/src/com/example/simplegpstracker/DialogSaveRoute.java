package com.example.simplegpstracker;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DialogSaveRoute extends DialogFragment{
	
	EditText text;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    getDialog().setTitle("Title!");
		    View v = inflater.inflate(R.layout.fragment_dialog_route, null);
		    v.findViewById(R.id.btnSave).setOnClickListener(new MyListener());
		    v.findViewById(R.id.btnNo).setOnClickListener(new MyListener());
		    v.findViewById(R.id.btnMaybe).setOnClickListener(new MyListener());
		    text = (EditText) v.findViewById(R.id.etRouteName);
		    return v;
		  }

		private class MyListener implements android.view.View.OnClickListener{

			@Override
			public void onClick(View v) {
				int id = v.getId();
			    switch (id) {
			    case R.id.btnSave:
			    	((MainActivity)getActivity()).SaveRoute(String.valueOf(text.getText()));
			    	dismiss();
			      break;
			    case R.id.btnNo:
			      dismiss();
			      break;
			    case R.id.btnMaybe:
			    	dismiss();
			      break;
			    }
				
			}
			
		}
		

}
