package com.example.simplegpstracker.factory;

import java.util.List;

import com.example.simplegpstracker.db.KalmanInfoHelperT;
import com.example.simplegpstracker.entity.GPSInfo;
import com.example.simplegpstracker.kalman.KManager;
import com.example.simplegpstracker.kalman.KalmanManager;

import android.content.Context;
import android.location.Location;

public abstract class FactoryKalmanBuilder {
	
	public static final int KALMAN_MANGER = 0;
	public static final int KALMAN_MANGER_C = 1;
	public static final int KALMAN_MANGER_GEO = 2;	
	
	protected KManager km;
	protected Location location;
	protected List<GPSInfo> list;
	protected GPSInfo kalmanInfo;
	protected KalmanInfoHelperT kalmanHelper;
	Context context;
	
	
	/*public FactoryKalmanBuilder(KManager manager){
		km = manager;		
	}*/
		
	
	public void init(List<GPSInfo> list, Context context){
		location = new Location(KalmanManager.KALMAN_PROVIDER);
		this.list = list;
		this.context = context;
		kalmanHelper = new KalmanInfoHelperT(context);
        kalmanHelper.cleanOldRecords();     
	}
	

	public abstract void compute();

}
