package com.example.simplegpstracker.kalman;

/**
 * Created by elen on 11.01.2015.
 */
public class MatrixC {
    protected int m_c = 0;
    protected int m_r = 0;
    //private int columns;
    //private int rows;

    public double[] Data;

    public MatrixC()
    {
        Data = null;
    }


    public MatrixC(int cols, int rows)
    {
        Resize(cols, rows);
    }

    public MatrixC(MatrixC m)
    {
        Set(m);
    }
    
    public void MatrixSetData(double[] data){
    	for (int i = 0; i < data.length; i++)
        {
            Data[i] = data[i];
        }
    }


    public final void Set(MatrixC m)
    {
        Resize(m.getColumns(), m.getRows());

        for (int i = 0; i < m.Data.length; i++)
        {
            Data[i] = m.Data[i];
        }
    }

    public final int getColumns()
    {
        return m_c;
    }
    public final void setColumns(int value)
    {
        Resize(value, m_r);
    }

    public final int getRows()
    {
        return m_r;
    }
    public final void setRows(int value)
    {
        Resize(m_c, value);
    }

    public final void Resize(int cols, int rows)
    {
        if ((m_c == cols) && (m_r == rows))
        {
            return;
        }

        m_c = cols;

        m_r = rows;

        Data = new double[cols * rows];

        Zero();
    }

    public final MatrixC clone()
    {

        MatrixC m = new MatrixC();

        m.Resize(this.getColumns(), this.getRows());

        for (int i = 0; i < Data.length; i++)
        {

            m.Data[i] = Data[i];
        }

        return m;
    }

    public final double Get(int x, int y)
    {
        return Data[x + y * m_c];
    }



    public final double Trace(int index)
    {
        return Get(index, index);
    }


    public final void Set(int x, int y, double v)
    {
        Data[x + (y * m_c)] = v;
    }

    private double[] privateData;
    public final double[] getData()
    {
        return privateData;
    }
    public final void setData(double[] value)
    {
        privateData = value;
    }

    public final void Multiply(double scalar)
    {
        for (int i = 0; i < Data.length; i++)
        {
            Data[i] *= scalar;
        }
    }


    public static MatrixC Multiply(MatrixC m, double scalar)
    {
        MatrixC rv = m.clone();
        rv.Multiply(scalar);
        return rv;
    }


    public static MatrixC Multiply(MatrixC a, MatrixC b)
    {
        MatrixC rv = new MatrixC(b.getColumns(), a.getRows());
        int min = a.getColumns() < b.getRows() ? a.getColumns() : b.getRows();
        for (int i = 0; i < a.getRows(); i++)
        {
            for (int j = 0; j < b.getColumns(); j++)
            {
                double s = 0;
                for (int k = 0; k < min; k++)
                {
                    double av = a.Get(k, i);
                    double bv = b.Get(j, k);
                    s += av * bv;
                }
                rv.Set(j, i, s);
            }
        }
        return rv;
    }


    public final void Multiply(MatrixC b)
    {

        MatrixC tmp = MatrixC.Multiply(this, b);

        this.Set(tmp);
    }


    public static MatrixC MultiplyABAT(MatrixC a, MatrixC b)
    {

        MatrixC rv = Multiply(a, b);

        MatrixC t = MatrixC.Transpose(a);

        rv.Multiply(t);

        return rv;
    }


    public static MatrixC Add(MatrixC a, double scalar)
    {
        MatrixC rv = new MatrixC(a);

        rv.Add(scalar);

        return rv;
    }

    public final void Add(double scalar)
    {

        for (int i = 0; i < Data.length; i++)
        {

            Data[i] += scalar;
        }
    }

    public static MatrixC Add(MatrixC a, MatrixC b)
    {

        MatrixC rv = new MatrixC(a);

        rv.Add(b);

        return rv;
    }

    public final void Add(MatrixC a)
    {
        for (int i = 0; i < Data.length; i++)
        {
            Data[i] += a.Data[i];
        }
    }


    public static MatrixC Subtract(MatrixC a, double scalar)
    {
        MatrixC rv = new MatrixC(a);
        rv.Subtract(scalar);
        return rv;
    }

    public final void Subtract(double scalar)
    {
        for (int i = 0; i < Data.length; i++)
        {
            Data[i] -= scalar;
        }
    }

    public static MatrixC Subtract(MatrixC a, MatrixC b)
    {
        MatrixC rv = new MatrixC(a);
        rv.Subtract(b);
        return rv;
    }

    public final void Subtract(MatrixC a)
    {
        for (int i = 0; i < Data.length; i++)
        {
            Data[i] -= a.Data[i];
        }
    }


    public static MatrixC Transpose(MatrixC m)
    {
        MatrixC rv = new MatrixC(m.m_r, m.m_c);
        for (int i = 0; i < m.m_c; i++)
        {
            for (int j = 0; j < m.m_r; j++)
            {
                rv.Set(j, i, m.Get(i, j));
            }
        }
        return rv;
    }


    public final void Transpose()
    {
        MatrixC rv = new MatrixC(this.m_r, this.m_c);

        for (int i = 0; i < m_c; i++)
        {

            for (int j = 0; j < m_r; j++)
            {
                rv.Set(j, i, this.Get(i, j));
            }
        }
        this.Set(rv);
    }


    public final boolean IsIdentity()
    {
        if (m_c != m_r)  return false;
        int check = m_c + 1;
        int j = 0;
        for (int i = 0; i < Data.length; i++) {
            if (j == check) {
                j = 0;
                if (Data[i] != 1) return false;
            }
            else {
                if (Data[i] != 0) return false;
            }
            j++;
        }
        return true;
    }

    public void SetIdentity()
    {
        if (m_c != m_r)
        {
            return;
        }
        int check = m_c + 1;
        int j = 0;
        for (int i = 0; i < Data.length; i++)
        {
            Data[i] = (j == check) ? 1 : 0;
            j = j == check ? 1 : j + 1;
        }
    }

    public void Zero()
    {
        for (int i = 0; i < Data.length; i++)
        {
            Data[i] = 0;
        }
    }

    public double Determinant()
    {

        if (m_c != m_r) return 0;

        if (m_c == 0) return 0;
        if (m_c == 1) return Data[0];
        if (m_c == 2) return (Data[0] * Data[3]) - (Data[1] * Data[2]);
        if (m_c == 3)
            return (Data[0] * ((Data[8] * Data[4]) - (Data[7] * Data[5]))) -
                    (Data[3] * ((Data[8] * Data[1]) - (Data[7] * Data[2]))) +
                    (Data[6] * ((Data[5] * Data[1]) - (Data[4] * Data[2])));

        // only supporting 1x1, 2x2 and 3x3
        return 0;

    }

    public static MatrixC Invert(MatrixC m)
    {
        if (m.m_c != m.m_r) return null;
        double det = m.Determinant();
        if (det == 0) return null;
        MatrixC rv = new MatrixC(m);
        if (m.m_c == 1) rv.Data[0] = 1 / rv.Data[0];
        det = 1 / det;
        if (m.m_c == 2)
        {
            rv.Data[0] = det * m.Data[3];
            rv.Data[3] = det * m.Data[0];
            rv.Data[1] = -det * m.Data[2];
            rv.Data[2] = -det * m.Data[1];
        }
        if (m.m_c == 3)
        {
            rv.Data[0] = det * (m.Data[8] * m.Data[4]) - (m.Data[7] * m.Data[5]);
            rv.Data[1] = -det * (m.Data[8] * m.Data[1]) - (m.Data[7] * m.Data[2]);
            rv.Data[2] = det * (m.Data[5] * m.Data[1]) - (m.Data[4] * m.Data[2]);

            rv.Data[3] = -det * (m.Data[8] * m.Data[3]) - (m.Data[6] * m.Data[5]);
            rv.Data[4] = det * (m.Data[8] * m.Data[0]) - (m.Data[6] * m.Data[2]);
            rv.Data[5] = -det * (m.Data[5] * m.Data[0]) - (m.Data[3] * m.Data[2]);

            rv.Data[6] = det * (m.Data[7] * m.Data[3]) - (m.Data[6] * m.Data[4]);
            rv.Data[7] = -det * (m.Data[7] * m.Data[0]) - (m.Data[6] * m.Data[2]);
            rv.Data[8] = det * (m.Data[4] * m.Data[0]) - (m.Data[3] * m.Data[1]);
        }
        return rv;
    }
}
