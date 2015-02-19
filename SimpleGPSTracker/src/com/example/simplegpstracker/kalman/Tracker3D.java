package com.example.simplegpstracker.kalman;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Tracker3D {
	double dt;
	double measurementNoise;
	double accelNoise;
	
	org.apache.commons.math3.filter.KalmanFilter filter;
	
	RealMatrix A;
	RealMatrix B;
	RealMatrix H;
	RealVector x;
	RealMatrix tmp;
	RealMatrix Q;
	RealMatrix P0;
	RealMatrix R;
	RealVector u;
	RealVector tmpPNoise;
	RealVector mNoise;
	RealVector pNoise;
	
	double position, velocity;
	
	public Tracker3D(double timeStep, double processNoise) {

		// discrete time interval
			dt = timeStep;
			// position measurement noise (meter)
			measurementNoise = processNoise;
		// acceleration noise (meter/sec^2)
			accelNoise = 0.2d;
	}
	
	public void setState(double position, double velocity){

			// A = [ 1 dt ]
	//	     [ 0  1 ]
		A = new Array2DRowRealMatrix(new double[][] { { 1, dt }, { 0, 1 } });
		// B = [ dt^2/2 ]
	//	     [ dt     ]
		B = new Array2DRowRealMatrix(new double[][] { { Math.pow(dt, 2d) / 2d }, { dt } });
		// H = [ 1 0 ]
		H = new Array2DRowRealMatrix(new double[][] { { 1d, 0d } });
		// x = [ 0 0 ]
		x = new ArrayRealVector(new double[] { position, velocity});
		//x = new ArrayRealVector(new double[] { 0, 0 });
	
		tmp = new Array2DRowRealMatrix(new double[][] {
		    { Math.pow(dt, 4d) / 4d, Math.pow(dt, 3d) / 2d },
		    { Math.pow(dt, 3d) / 2d, Math.pow(dt, 2d) } });
		// Q = [ dt^4/4 dt^3/2 ]
	//	     [ dt^3/2 dt^2   ]
		Q = tmp.scalarMultiply(Math.pow(accelNoise, 2));
		// P0 = [ 1 1 ]
	//	      [ 1 1 ]
		P0 = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });
		// R = [ measurementNoise^2 ]
		R = new Array2DRowRealMatrix(new double[] { Math.pow(measurementNoise, 2) });
	
		// constant control input, increase velocity by 0.1 m/s per cycle
		//u = new ArrayRealVector(new double[] { 0.1d });
		//u = new ArrayRealVector(new double[] {0.1d});
		ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
		MeasurementModel mm = new DefaultMeasurementModel(H, R);
		filter = new org.apache.commons.math3.filter.KalmanFilter(pm, mm);
	
		//RandomGenerator rand = new JDKRandomGenerator();
	
		tmpPNoise = new ArrayRealVector(new double[] { Math.pow(dt, 2d) / 2d, dt });
		mNoise = new ArrayRealVector(1);
	}
	
	public void Update(double newPosition, double newVelocity, double processNoise){
		
		//u = new ArrayRealVector(new double[] {0.1d});
		filter.predict();
		
	    position = filter.getStateEstimation()[0];
	    velocity = filter.getStateEstimation()[1];
	    System.out.println("P : "+position+" : "+velocity);
	    //x = new ArrayRealVector(new double[] { newPosition, newVelocity});
	    // simulate the process
	    pNoise = tmpPNoise.mapMultiply(processNoise * processNoise);

	    // x = A * x + B * u + pNoise
	    x = A.operate(x).add(pNoise);

	    // simulate the measurement
	    mNoise.setEntry(0, measurementNoise * measurementNoise);

	    // z = H * x + m_noise
	    RealVector z = H.operate(x).add(mNoise);

	    filter.correct(z);

	    position = filter.getStateEstimation()[0];
	    velocity = filter.getStateEstimation()[1];
	}
	
	public double getPosition(){
		return position;
	}

}
