package com.example.simplegpstracker.kalman;

import android.util.Log;

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
public class Kalman2D
{
	public double[] data;
	public double LastGain;
	double position, dt, processNoise;
	/** 
	 State.
	*/

	MatrixC m_x = new MatrixC(1, 2);
	/** 
	 Covariance.
	*/

	MatrixC m_p = new MatrixC(2, 2);
	/** 
	 Minimal covariance.
	*/

	MatrixC m_q = new MatrixC(2, 2);

	/** 
	 Minimal innovative covariance, keeps filter from locking in to a solution.
	*/

	double m_r;
	
	MatrixC f;
	MatrixC h;
	MatrixC ht;
	
	MatrixC temp;

	/** 
	 The last updated value, can also be set if filter gets
	 sudden absolute measurement data for the latest update.
	*/
	public final double getValue()
	{
		return m_x.Data[0];
	}
	public final void setValue(double value)
	{
		m_x.Data[0] = value;
	}

	/** 
	 How fast the value is changing.
	*/
	public final double getVelocity()
	{
		return m_x.Data[1];
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
		return m_p.Data[0];
	}

	/** 
	 Predict the value forward from last measurement time by dt.
	 X = F*X + H*U        
	 
	 @param dt
	 @return 
	*/
	public final double Predicition(double dt)
	{
		return m_x.Data[0] + (dt * m_x.Data[1]);
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
		return m_p.Data[0] + dt*(m_p.Data[2] + m_p.Data[1]) + dt*dt*m_p.Data[3] + m_q.Data[0];
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
	public Kalman2D(double position, double velosity, double dt, double processNoise){
		this.position = position;
		this.dt = dt;
		this.processNoise = processNoise;
		
		temp = new MatrixC(2,2);
		temp.Data[0] = Math.pow(dt, 4d) / 4d;
		temp.Data[1] = Math.pow(dt, 3d) / 2d;
		temp.Data[2] = Math.pow(dt, 3d) / 2d;
		temp.Data[3] = Math.pow(dt, 2d);

		m_q = MatrixC.Multiply(temp, processNoise*processNoise);
		
		//Set P
		/*data = new double[] { 1, 1, 1, 1 };
		m_p.setData(data);*/
		m_p.Data[0] = m_p.Data[3] = 3;
        m_p.Data[1] = m_p.Data[2] = 0;		
				
		m_x.Data[0] = position;
		m_x.Data[1] = velosity;
				
		f = new MatrixC(2, 2);
		f.MatrixSetData(new double[] { 1, dt, 0, 1 });
		h = new MatrixC(2, 2);
		h.MatrixSetData(new double[] { 1, 0, 0, 1 });
		ht = new MatrixC(2, 2);
		ht.MatrixSetData(new double[] { 1, 0, 0, 1 });
		// U = {0,0}		
		
	}
	
	public final void SetMatrix(double position, double velosity, double dt, double processNoise)
	{
		/*m_q.Data[0] = qx * qx;
		m_q.Data[1] = qv * qx;
		m_q.Data[2] = qv * qx;
		m_q.Data[3] = qv * qv;*/
		
		//Set Q
		temp = new MatrixC(2,2);
		temp.Data[0] = Math.pow(dt, 4d) / 4d;
		temp.Data[1] = Math.pow(dt, 3d) / 2d;
		temp.Data[2] = Math.pow(dt, 3d) / 2d;
		temp.Data[3] = Math.pow(dt, 2d);

		m_q.Multiply(temp, processNoise*processNoise);
		
		m_r = processNoise*processNoise;

		/*m_p.Data[0] = m_p.Data[3] = pd;
		m_p.Data[0] = m_p.Data[3];
		m_p.Data[1] = m_p.Data[2] = 0;
		m_p.Data[1] = m_p.Data[2];*/
		
		//Set P
		
		data = new double[] { 1, 1, 1, 1 };
		m_p.setData(data);
		
		
		m_x.Data[0] = position;
		m_x.Data[1] = velosity;
		
		f = new MatrixC(2, 2);
		data = new double[] { 1, dt, 0, 1 };
		f.MatrixSetData(data);
		h = new MatrixC(2, 2);
		data = new double[] { 1, 0, 0, 1 };
		h.MatrixSetData(data);
		ht = new MatrixC(2, 2);
		data = new double[] { 1, 0, 0, 1 };
		ht.MatrixSetData(data);
		// U = {0,0}
	}
	
	public void setState(){
		
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
		Log.i("DEBUG", "m_x_pered:" + m_x.Data[0]);
		m_x = MatrixC.Multiply(f, m_x);

		// P = F*P*F^T + Q
		m_p = MatrixC.MultiplyABAT(f, m_p);
		m_p.Add(m_q);

		// Y = M – H*X  
		MatrixC y = new MatrixC(1, 2);
		y.MatrixSetData(new double[] { mx - m_x.Data[0], mv - m_x.Data[1] });

		// S = H*P*H^T + R 
		MatrixC s = MatrixC.MultiplyABAT(h, m_p);
		/*s.Data[0] += m_r;
		s.Data[3] += m_r*0.1;*/
		s.Data[0] += m_r;
		s.Data[3] += 0.1;

		// K = P * H^T *S^-1 
		MatrixC tmp = MatrixC.Multiply(m_p, ht);
		MatrixC sinv = MatrixC.Invert(s);
		MatrixC k = new MatrixC(2, 2);

		if (sinv != null)
		{
			k = MatrixC.Multiply(tmp, sinv);
		}

		LastGain = k.Determinant();

		// X = X + K*Y
		m_x.Add(MatrixC.Multiply(k, y));

		// P = (I – K * H) * P
		MatrixC kh = MatrixC.Multiply(k, h);
		MatrixC id = new MatrixC(2, 2);
		id.setData(new double[] { 1, 0, 0, 1 });
		kh.Multiply(-1);
		id.Add(kh);
		id.Multiply(m_p);
		m_p.Set(id);


		// Return latest estimate.
		Log.i("DEBUG", "m_x:" + m_x.Data[0]);
		return m_x.Data[0];
	}
	
	public double getPosition() { return m_x.Data[0]; }


}