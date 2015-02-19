package com.example.simplegpstracker.db;

import java.util.ArrayList;
import java.util.List;

import com.example.simplegpstracker.entity.GPSInfo;
import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ProcessedInfoHelper extends BaseDao{
	
	//private SQLiteDatabase db;
	
	public ProcessedInfoHelper(Context context) {
		super(context);
		openDb();
	}
	
    public long insert(GPSInfo gpsInfo) {   
        ContentValues values = getValues(gpsInfo);
        return db.insert(DbHelper.TRACKER_PROCESSED_DB_TABLE, null, values);
    }
    
    private ContentValues getValues(GPSInfo gpsInfo) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.TRACKER_DB_ID, gpsInfo.getId());
        values.put(DbHelper.TRACKER_DB_LATITUDE, gpsInfo.getLatitude());
        values.put(DbHelper.TRACKER_DB_LONGITUDE, gpsInfo.getLongitude());
        values.put(DbHelper.TRACKER_DB_ACCURACY, gpsInfo.getAccuracy());
        values.put(DbHelper.TRACKER_DB_ACCEL, gpsInfo.getAcceleration());
        values.put(DbHelper.TRACKER_DB_SPEED, gpsInfo.getSpeed());
        values.put(DbHelper.TRACKER_DB_BEARING, gpsInfo.getBearing());
        values.put(DbHelper.TRACKER_DB_GYR_X, gpsInfo.getGyroscopex());
        values.put(DbHelper.TRACKER_DB_GYR_Y, gpsInfo.getGyroscopey());
        values.put(DbHelper.TRACKER_DB_GYR_Z, gpsInfo.getGyroscopez());
        values.put(DbHelper.TRACKER_DB_NAME, gpsInfo.getName());
        values.put(DbHelper.TRACKER_DB_TIME, gpsInfo.getTime());
        return values;
    }
    
    public List<GPSInfo> getGPSPoint() {

    	List<GPSInfo> list = null;
		String sql = "SELECT * FROM " + DbHelper.TRACKER_PROCESSED_DB_TABLE;
		
		Cursor cursor = db.rawQuery(sql, null);
		
		if (cursor.getCount() != 0) {

			list = new ArrayList<GPSInfo>();
			cursor.moveToFirst();
			do {
				GPSInfo gpsInfo = new GPSInfo();
				gpsInfo.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.TRACKER_DB_ID)));
				gpsInfo.setLatitude(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_LATITUDE)));
				gpsInfo.setLongitude(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_LONGITUDE)));
				gpsInfo.setAccuracy(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_ACCURACY)));
				gpsInfo.setAcceleration(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_ACCEL)));
				gpsInfo.setSpeed(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_SPEED)));
				gpsInfo.setBearing(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_BEARING)));
				gpsInfo.setGyroscopex(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_GYR_X)));
				gpsInfo.setGyroscopey(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_GYR_Y)));
				gpsInfo.setGyroscopez(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_GYR_Z)));
				gpsInfo.setName(cursor.getString(cursor.getColumnIndex(DbHelper.TRACKER_DB_NAME)));
				gpsInfo.setTime(cursor.getLong(cursor.getColumnIndex(DbHelper.TRACKER_DB_TIME)));
				list.add(gpsInfo);
			} while(cursor.moveToNext());	

		}
		
		if (cursor != null) cursor.close();
		return list;
	}
    
    public GPSInfo getPointInfo(int index) {
    	String sql = "SELECT * FROM " + DbHelper.TRACKER_PROCESSED_DB_TABLE + " WHERE _id = " + index;
		GPSInfo gpsInfo = new GPSInfo();
		Cursor cursor = db.rawQuery(sql, null);
		
		if (cursor.getCount() != 0) {

			cursor.moveToFirst();
			do {
				
				gpsInfo.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.TRACKER_DB_ID)));
				gpsInfo.setLatitude(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_LATITUDE)));
				gpsInfo.setLongitude(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_LONGITUDE)));
				gpsInfo.setAccuracy(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_ACCURACY)));
				gpsInfo.setAcceleration(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_ACCEL)));
				gpsInfo.setSpeed(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_SPEED)));
				gpsInfo.setBearing(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_BEARING)));
				gpsInfo.setGyroscopex(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_GYR_X)));
				gpsInfo.setGyroscopey(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_GYR_Y)));
				gpsInfo.setGyroscopez(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_GYR_Z)));
				gpsInfo.setName(cursor.getString(cursor.getColumnIndex(DbHelper.TRACKER_DB_NAME)));
				gpsInfo.setTime(cursor.getLong(cursor.getColumnIndex(DbHelper.TRACKER_DB_TIME)));
			} while(cursor.moveToNext());
		}
		
		if (cursor != null) cursor.close();
		return gpsInfo;
    }
    
    public ArrayList<LatLng> getLatLngPoint() {
    	ArrayList<LatLng> points = new ArrayList<LatLng>();
    	
    	String sql = "SELECT * FROM " + DbHelper.TRACKER_PROCESSED_DB_TABLE;
    	Cursor cursor = db.rawQuery(sql, null);

		if (cursor.getCount() != 0) {

			cursor.moveToFirst();
			do {
				points.add(new LatLng(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_LATITUDE)),
									  cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_LONGITUDE))));
			} while(cursor.moveToNext());
		}
		if (cursor != null) cursor.close();
    	return points;
    }

	public void cleanOldRecords() {
        db.delete(DbHelper.TRACKER_PROCESSED_DB_TABLE, null, null);
    }
	
	public void closeDB() {
        db.close();
	}
}
