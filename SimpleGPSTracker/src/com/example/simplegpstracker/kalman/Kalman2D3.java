package com.example.simplegpstracker.kalman;

import android.util.Log;
import Jama.Matrix;

///////////////////////////////////////////////////////////////////////////////
//
//  Kalman2D.cs
//
//  By Philip R. Braica (HoshiKata@aol.com, VeryMadSci@gmail.com)
//
//  Distributed under the The Code Project Open License (CPOL)
//  http://www.codeproject.com/info/cpol10.aspx
///////////////////////////////////////////////////////////////////////////////


/** 
 Kalman 2D.
*/
public class Kalman2D3
{
	public double[] data;
	public double LastGain;
	double position, dt, processNoise;
	/** 
	 State.
	*/
	Matrix m_x;
	//MatrixC m_x = new MatrixC(1, 2);
	/** 
	 Covariance.
	*/

	Matrix m_p;
	/** 
	 Minimal covariance.
	*/

	Matrix m_q;

	/** 
	 Minimal innovative covariance, keeps filter from locking in to a solution.
	*/

	double m_r;
	
	Matrix f;
	Matrix h;
	Matrix ht;
	
	Matrix temp;

	/** 
	 The last updated value, can also be set if filter gets
	 sudden absolute measurement data for the latest update.
	*/
	public final double getValue()
	{
		return m_x.get(0, 0);
	}
	public final void setValue(double value)
	{
		m_x.set(0, 0, value);
	}

	/** 
	 How fast the value is changing.
	*/
	public final double getVelocity()
	{
		return m_x.get(1, 0);
	}

	/** 
	 The last kalman gain used, useful for debug.
	*/
	private double privateLastGain;
	public final double getLastGain()
	{
		return privateLastGain;
	}
	protected final void setLastGain(double value)
	{
		privateLastGain = value;
	}

	/** 
	 Last updated positional variance.
	 
	 @return 
	*/
	public final double Variance()
	{
		return m_p.get(0,0);
	}

	/** 
	 Predict the value forward from last measurement time by dt.
	 X = F*X + H*U        
	 
	 @param dt
	 @return 
	*/
	public final double Predicition(double dt)
	{
		return m_x.get(0,0) + (dt * m_x.get(1,0));
	}

	/** 
	 Get the estimated covariance of position predicted 
	 forward from last measurement time by dt.
	 P = F*P*F^T + Q.
	 
	 @param dt
	 @return 
	*/
	public final double Variance(double dt)
	{
		return m_p.get(0,0) + dt*(m_p.get(1,0) + m_p.get(0,1)) + dt*dt*m_p.get(1,1) + m_q.get(0,0);
		// Not needed.
		// m_p[1] = m_p[1] + dt * m_p[3] + m_q[1];
		// m_p[2] = m_p[2] + dt * m_p[3] + m_q[2];
		// m_p[3] = m_p[3] + m_q[3];
	}

	/** 
	 Reset the filter.
	 
	 @param qx Measurement to position state minimal variance.
	 @param qv Measurement to velocity state minimal variance.
	 @param r Measurement covariance (sets minimal gain).
	 @param pd Initial variance.
	 @param ix Initial position.
	*/
	public Kalman2D3(double position, double velosity, double dt, double processNoise){
		this.position = position;
		this.dt = dt;
		this.processNoise = processNoise;
		
		temp = new Matrix(new double[][]{ {Math.pow(dt, 4d)/4d, Math.pow(dt, 3d)/2d}, 
										  {Math.pow(dt, 3d)/2d, Math.pow(dt, 2d)} });

		m_q = temp.times(processNoise*processNoise);


		//Set P
		/*data = new double[] { 1, 1, 1, 1 };
		m_p.setData(data);*/
		m_p = new Matrix(new double[][] { {1, 0}, 
										  {0, 1}});
        
        m_x = new Matrix(new double[][]{{position}, {velosity}});
				
		f = new Matrix(new double[][] { {1, dt}, 
										{0, 1 }});

		h = new Matrix(new double[][] { {1, 0},
										{0, 1} });

		ht = new Matrix(new double[][] { {1, 0}, 
										 {0, 1} });

		// U = {0,0}		
		
	}
	
	

	/** 
	 Update the state by measurement m at dt time from last measurement.
	 
	 @param m
	 @param dt
	 @return 
	*/
	public final double Update(double mx, double mv, double noise)
	{
		// Predict to now, then update.
		// Predict:
		//   X = F*X + H*U
		//   P = F*P*F^T + Q.
		// Update:
		//   Y = M – H*X          Called the innovation = measurement – state transformed by H.	
		//   S = H*P*H^T + R      S= Residual covariance = covariane transformed by H + R
		//   K = P * H^T *S^-1    K = Kalman gain = variance / residual covariance.
		//   X = X + K*Y          Update with gain the new measurement
		//   P = (I – K * H) * P  Update covariance to this time.
		//
		// Same as 1D but mv is used instead of delta m_x[0], and H = [1,1].

		
		
		// X = F*X + H*U
		m_r = noise*noise;
		Log.i("DEBUG", "mx:" + mx);
		m_x = f.times(m_x);
		Log.i("DEBUG", "m_x_predicted:" + m_x.get(0, 0));
		// P = F*P*F^T + Q
		m_p = f.times(m_p).times(f.transpose()).plus(m_q);

		// Y = M – H*X  
		Matrix z = new Matrix(new double[][]{{mx}, {mv}});
		//Matrix y = new Matrix(new double[][] { {mx - m_x.get(0,0), mv - m_x.get(0,1) }});
		Matrix y = z.minus(h.times(m_x));
		// S = H*P*H^T + R 
		 Matrix r = new Matrix(new double[][] { {m_r,0},{0,m_r*0.1} });

		Matrix s = h.times(m_p.times(h.transpose())).plus(r);
		//Matrix s = h.times(m_p.times(h.transpose()));
		Matrix sinv = s.inverse();
		// K = P * H^T *S^-1 

		Matrix k = new Matrix(2, 2);
		try{
		  k = (m_p.times(h.transpose())).times(sinv);	
		}catch (RuntimeException e){
			
		}


		// X = X + K*Y
		m_x = m_x.plus(k.times(y));

		// P = (I - K * H) * P
		Matrix i = new Matrix(new double[][] { {1, 0}, {0, 1} });
		m_p = m_p.times(i.minus(k.times(h)));
		


		// Return latest estimate.
		Log.i("DEBUG", "m_x:" + m_x.get(0,0));
		return m_x.get(0,0);
	}
	
	public double getPosition() { return m_x.get(0,0); }



}