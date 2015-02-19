package com.example.simplegpstracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


///////////////////////////////////////
//DB Helper: operation with DB tables//	
///////////////////////////////////////

public class DbHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gpstrackerbase";
    public static final String TRACKER_DB_TABLE = "trackerBase";
    public static final String TRACKER_KALMAN_DB_TABLE = "trackerKalman";
    public static final String TRACKER_KALMANT_DB_TABLE = "trackerKalmanT";
    public static final String TRACKER_PROCESSED_DB_TABLE = "trackerProcessed";
    
    public static final String TRACKER_DB_ID = "cid";
    public static final String TRACKER_DB_LATITUDE = "latitude";
    public static final String TRACKER_DB_LONGITUDE = "longitude";
    public static final String TRACKER_DB_ACCURACY = "accuracy";
    public static final String TRACKER_DB_ACCEL = "acceleration";
    public static final String TRACKER_DB_SPEED = "speed";
    public static final String TRACKER_DB_BEARING = "bearing";
    public static final String TRACKER_DB_GYR_X = "gyrx";
    public static final String TRACKER_DB_GYR_Y = "gyry";
    public static final String TRACKER_DB_GYR_Z = "gyrz";
    public static final String TRACKER_DB_NAME = "name";
    public static final String TRACKER_DB_TIME = "time";

    
    
    public static final String CREATE_DB_TRACKER_TABLE = "CREATE TABLE IF NOT EXISTS " + TRACKER_DB_TABLE
            + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + TRACKER_DB_ID + " TEXT," + TRACKER_DB_LATITUDE + " DOUBLE," 
    		+ TRACKER_DB_LONGITUDE + " DOUBLE," + TRACKER_DB_ACCURACY + " FLOAT," + TRACKER_DB_GYR_X + " DOUBLE," 
            + TRACKER_DB_GYR_Y + " FLOAT," +  TRACKER_DB_GYR_Z + " FLOAT," + TRACKER_DB_ACCEL + " DOUBLE," + TRACKER_DB_SPEED + " FLOAT,"
            + TRACKER_DB_BEARING + " DOUBLE," + TRACKER_DB_NAME + " TEXT," + TRACKER_DB_TIME + " LONG);";
    
    public static final String CREATE_DB_TRACKER_KALMAN_TABLE = "CREATE TABLE IF NOT EXISTS " + TRACKER_KALMAN_DB_TABLE
            + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + TRACKER_DB_ID + " TEXT," + TRACKER_DB_LATITUDE + " DOUBLE," 
    		+ TRACKER_DB_LONGITUDE + " DOUBLE," + TRACKER_DB_ACCURACY + " FLOAT," + TRACKER_DB_GYR_X + " DOUBLE," 
            + TRACKER_DB_GYR_Y + " FLOAT," +  TRACKER_DB_GYR_Z + " FLOAT," + TRACKER_DB_ACCEL + " DOUBLE," + TRACKER_DB_SPEED + " FLOAT,"
            + TRACKER_DB_BEARING + " DOUBLE," + TRACKER_DB_NAME + " TEXT," + TRACKER_DB_TIME + " LONG);";
    
    public static final String CREATE_DB_TRACKER_KALMANT_TABLE = "CREATE TABLE IF NOT EXISTS " + TRACKER_KALMANT_DB_TABLE
            + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + TRACKER_DB_ID + " TEXT," + TRACKER_DB_LATITUDE + " DOUBLE," 
    		+ TRACKER_DB_LONGITUDE + " DOUBLE," + TRACKER_DB_ACCURACY + " FLOAT," + TRACKER_DB_GYR_X + " DOUBLE," 
            + TRACKER_DB_GYR_Y + " FLOAT," +  TRACKER_DB_GYR_Z + " FLOAT," + TRACKER_DB_ACCEL + " DOUBLE," + TRACKER_DB_SPEED + " FLOAT,"
            + TRACKER_DB_BEARING + " DOUBLE," + TRACKER_DB_NAME + " TEXT," + TRACKER_DB_TIME + " LONG);";
    
    public static final String CREATE_DB_TRACKER_PROCESSED_TABLE = "CREATE TABLE IF NOT EXISTS " + TRACKER_PROCESSED_DB_TABLE
            + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + TRACKER_DB_ID + " TEXT," + TRACKER_DB_LATITUDE + " DOUBLE," 
    		+ TRACKER_DB_LONGITUDE + " DOUBLE," + TRACKER_DB_ACCURACY + " FLOAT," + TRACKER_DB_GYR_X + " DOUBLE," 
            + TRACKER_DB_GYR_Y + " FLOAT," +  TRACKER_DB_GYR_Z + " FLOAT," + TRACKER_DB_ACCEL + " DOUBLE," + TRACKER_DB_SPEED + " FLOAT,"
            + TRACKER_DB_BEARING + " DOUBLE," + TRACKER_DB_NAME + " TEXT," + TRACKER_DB_TIME + " LONG);";
    
	
	private static DbHelper mInstance = null;
    private static SQLiteDatabase myWritableDb;
    
    public static DbHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    
    /**
     * Returns a writable database instance in order not to open and close many
     * SQLiteDatabase objects simultaneously
     *
     * @return a writable instance to SQLiteDatabase
     */
    
    public SQLiteDatabase getMyWritableDatabase() {
        if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
            myWritableDb = this.getWritableDatabase();
        }
 
        return myWritableDb;
    }
    
    private DbHelper(Context context) {
		super(context, DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DB_TRACKER_TABLE);
		db.execSQL(CREATE_DB_TRACKER_KALMAN_TABLE);
		db.execSQL(CREATE_DB_TRACKER_KALMANT_TABLE);
		db.execSQL(CREATE_DB_TRACKER_PROCESSED_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_DB_TRACKER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_DB_TRACKER_KALMAN_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_DB_TRACKER_KALMANT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_DB_TRACKER_PROCESSED_TABLE);
	}

}
