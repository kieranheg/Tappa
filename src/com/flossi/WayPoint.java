package com.flossi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WayPoint {
	
	final static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private double lat;
	private double lon;
	private double speed;
	private Calendar time;
	private long secs;
	
	public WayPoint() {
	}
	
	public WayPoint(double lat,	double lon,	double speed, Calendar time) {
		this.lat = lat;
		this.lon = lon;
		this.speed = speed;
		this.time = time;
		this.secs = 0;
	}
	
	public void setWayPoint(double lat,	double lon,	double speed, Calendar time) {
		this.lat = lat;
		this.lon = lon;
		this.speed = speed;
		this.time = time;
	}
		
	@Override
	public String toString() {
		return "<" 
				+ lat + ", " + lon
				+ " Time " + dateFormat.format(time.getTime())
				+ " Secs " + secs
				+ " Speed " + speed
				+ ">";
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public double getSpeed() {
		return speed;
	}

	public Calendar getTime() {
		return time;
	}

	public void setSecs(long secsGap) {
		this.secs = secsGap;
	}
	
	public long getSecs() {
		return secs;
	}
}
