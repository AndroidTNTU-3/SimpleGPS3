package com.example.simplegpstracker;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

public class DialogSaveRoute extends DialogFragment{
	
	EditText text;
	CheckBox cbxSend;
	int sendToServer = Contract.SEND_TO_SERVER_OFF;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    getDialog().setTitle("Title!");
		    View v = inflater.inflate(R.layout.fragment_dialog_route, null);
		    v.findViewById(R.id.btnSave).setOnClickListener(new MyListener());
		    v.findViewById(R.id.btnNo).setOnClickListener(new MyListener());
		    v.findViewById(R.id.btnBack).setOnClickListener(new MyListener());
		    cbxSend = (CheckBox) v.findViewById(R.id.cbxSend);
		    cbxSend.setOnClickListener(new MyListener());
		    text = (EditText) v.findViewById(R.id.etRouteName);
		    return v;
		  }

		private class MyListener implements android.view.View.OnClickListener{

			@Override
			public void onClick(View v) {
				int id = v.getId();
			    switch (id) {
			    case R.id.cbxSend:
			    	sendToServer = Contract.SEND_TO_SERVER_ON;
			      break;
			    case R.id.btnSave:
			    	((MainActivity)getActivity()).SaveRoute(String.valueOf(text.getText()), sendToServer);
			    	dismiss();
			      break;
			    case R.id.btnNo:
			    	((MainActivity)getActivity()).StopRecord();
			      dismiss();
			      break;
			    case R.id.btnBack:
			    	dismiss();
			      break;
			    }
				
			}
			
		}
		

}
