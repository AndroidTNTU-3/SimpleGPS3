package com.example.simplegpstracker.entity;

public class ListRouteParams {
	
	String name;
	String startTime;
	String stopTime;
	String duration;
	double averageSpeed;
	float distance;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStopTime() {
		return stopTime;
	}
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public double getAverageSpeed() {
		return averageSpeed;
	}
	public void setAverageSpeed(double averageSpeed) {
		this.averageSpeed = averageSpeed;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}		

}
