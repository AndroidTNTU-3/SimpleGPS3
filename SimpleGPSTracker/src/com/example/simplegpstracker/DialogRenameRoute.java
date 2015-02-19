package com.example.simplegpstracker;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DialogRenameRoute extends DialogFragment{
	
	EditText text;
	private String name;
	
	DialogRenameRoute(String selectedName){
		name = selectedName;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getDialog().setTitle("Title!");
	    View v = inflater.inflate(R.layout.fragment_dialog_rename, null);
	    v.findViewById(R.id.btnSaveRename).setOnClickListener(new MyListener());
	    v.findViewById(R.id.btnNoRename).setOnClickListener(new MyListener());
	    text = (EditText) v.findViewById(R.id.etRouteNameRename);
	    text.setText(name);
	    return v;
	  }
	
	private class MyListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			int id = v.getId();
		    switch (id) {
		    case R.id.btnSaveRename:
		    	((MainActivity)getActivity()).EditRouteName(String.valueOf(text.getText()));
		    	dismiss();
		      break;
		    case R.id.btnNoRename:
		      dismiss();
		      break;
		    }
			
		}
		
	}

}
