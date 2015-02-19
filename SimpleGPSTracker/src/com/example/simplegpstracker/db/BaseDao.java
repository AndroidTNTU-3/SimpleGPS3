package com.example.simplegpstracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class BaseDao {
	
	protected SQLiteDatabase db;
	
	protected DbHelper openHelper;
	
	Context context;
	
	public BaseDao(Context context){
		this.context = context;
	}

	protected void openDb(){
		openHelper = DbHelper.getInstance(context);
        db = openHelper.getWritableDatabase();
	}
	
	protected void closeDb(){
		if (db != null && db.isOpen()) {
			db.close();
			db = null;
        }
	}

}
