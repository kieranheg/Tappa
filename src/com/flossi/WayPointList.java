package com.flossi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WayPointList {
	
    private List<WayPoint> wayPoints;
	private Calendar startTime;
	private int startPosIndex;
	private int startSecsOffset;
    
    public WayPointList() {
    	wayPoints = new ArrayList<WayPoint>();
    	startPosIndex = 0;
    	startSecsOffset = 0;
    }
	
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	
	public Calendar getStartTime() {
		return startTime;
	}
	
    public void add(WayPoint wp) {
    	wayPoints.add(wp);
    }
    
    public void add(double lat,	double lon,	double speed, Calendar time) {
    	WayPoint wp = new WayPoint(lat,	lon, speed, time);
    	wayPoints.add(wp);
    }
    
    public int size() {
    	return wayPoints.size();
    }
    
    public WayPoint get(int i) {
    	return wayPoints.get(i);
    }

	public int getStartPosIndex() {
		return startPosIndex;
	}

	public void setStartPosIndex(int startPosIndex) {
		this.startPosIndex = startPosIndex;
	}

	public long getStartSecsOffset() {
		return startSecsOffset;
	}

	public void setStartSecsOffset(int startSecsOffset) {
		this.startSecsOffset = startSecsOffset;
	}

}
