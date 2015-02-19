package com.example.simplegpstracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class GPSInfo implements Parcelable{
	
	private int Id;
	private Double latitude;
	private Double longitude;
	private float accuracy;
	private String name;
	private long time;
	private double acceleration;
	private float speed;
	private double bearing;
	private float gyroscopex;
	private float gyroscopey;
	private float gyroscopez;
	
	public float getGyroscopez() {
		return gyroscopez;
	}
	
	public float getGyroscopex() {
		return gyroscopex;
	}

	public void setGyroscopex(float gyroscopex) {
		this.gyroscopex = gyroscopex;
	}
	
	public void setGyroscopey(float gyroscopey) {
		this.gyroscopey = gyroscopey;
	}
	
	public void setGyroscopez(float gyroscopez) {
		this.gyroscopez = gyroscopez;
	}

	public GPSInfo(){};
	
	public GPSInfo(Double longitude, Double latitude){
		setLongitude(longitude);
		setLatitude(latitude);
	}
	
	public int getId() {
		return Id;
	}
	

	public float getGyroscopey() {
		return gyroscopey;
	}

	

	public void setId(int id) {
		Id = id;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}
	
	protected GPSInfo(Parcel in) {
        Id = in.readInt();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        accuracy = in.readFloat();
        name = in.readString();
        time = in.readLong();
        acceleration = in.readDouble();
        speed = in.readFloat();
        bearing = in.readDouble();
        gyroscopex = in.readFloat();
        gyroscopey = in.readFloat();
        gyroscopez = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        dest.writeFloat(accuracy);
        dest.writeString(name);
        dest.writeLong(time);
        dest.writeDouble(acceleration);
        dest.writeFloat(speed);
        dest.writeDouble(bearing);
        dest.writeFloat(gyroscopex);
        dest.writeFloat(gyroscopey);
        dest.writeFloat(gyroscopez);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GPSInfo> CREATOR = new Parcelable.Creator<GPSInfo>() {
        @Override
        public GPSInfo createFromParcel(Parcel in) {
            return new GPSInfo(in);
        }

        @Override
        public GPSInfo[] newArray(int size) {
            return new GPSInfo[size];
        }
    };


}
