package com.example.simplegpstracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogMapAllert extends DialogFragment implements OnClickListener{
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
	        .setTitle(getResources().getString(R.string.allert_map_title)).
	        setPositiveButton(R.string.dialog_warning_button_ok, this)	    
	        .setMessage(getResources().getString(R.string.allert_map_message));
	    return adb.create();
	  }

	@Override
	public void onClick(DialogInterface arg0, int which) {
		switch (which) {
	    case Dialog.BUTTON_POSITIVE:
	    	dismiss();
	      break;
	    case Dialog.BUTTON_NEGATIVE:
	   
	      break;
	    case Dialog.BUTTON_NEUTRAL:
	    
	      break;
	    }
		
	}

}
