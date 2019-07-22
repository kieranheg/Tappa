package com.flossi;

import com.flossi.observers.StateObserverInterface;
import com.flossi.observers.ViewObserverInterface;
import com.flossi.plumbing.Logging;
import com.flossi.plumbing.State;
import com.flossi.utils.GPSUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Responsible for maintaining all data, state and app logicf for Strava data
 * 
 * @author kieran
 *
 */

public class StravaModel implements StravaModelInterface {
	private final int WAYPOINT_READ_START_POS = 0;
	private final int MAX_START_GAP = 5;
	private final int MAX_WAY_POINTS_SECS = 2;
	private final int MAX_SYNC_WAY_POINTS_SECS = 2;
	private final int MAX_WAY_POINT_SYNC_THRESHOLD = 200;
	
	State state;
	private ArrayList<StateObserverInterface> stateObservers;
	private ArrayList<ViewObserverInterface> viewObservers;
	
	long blackOutSecs;
	Logging log;
	
	double gap;
	
	String xmlFile1;
	String xmlFile2;
	StravaXmlHandler xmlHandler;
	
	WayPointList wayPoints1;
	WayPointList wayPoints2;
	
	public StravaModel(String fName1, String fName2) {
		log = new Logging(this.getClass().getSimpleName());
		log.log("Starting Constructor");
		state = new State();
		state.setStateStopped();
		notifyStateObservers();
		stateObservers = new ArrayList();
		viewObservers = new ArrayList();
		this.xmlFile1 = fName1;
		this.xmlFile2 = fName2;
		blackOutSecs = 0;
		gap=0;
	}
	
	public double getGap() {
		return gap;
	}

	public void setGap(double gap) {
		this.gap = gap;
	}

	@Override
	public void initialize() {
		state.setStateStarting();
		notifyStateObservers();
		try {
			xmlHandler = new StravaXmlHandler();
			readData();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	@Override
	public void registerObserver(ViewObserverInterface viewObserverInterface) {
		viewObservers.add(viewObserverInterface);
	}
	
	public void notifyViewObservers() {
		if (viewObservers != null) {
			for (ViewObserverInterface viewObserver : viewObservers) {
				viewObserver.updateView();
			}
		}
	}
	
	public void registerObserver(StateObserverInterface stateObserver) {
		stateObservers.add(stateObserver);
	}
	
	public void notifyStateObservers() {
		if (stateObservers != null) {
			for (StateObserverInterface stateObserver : stateObservers) {
				stateObserver.updateState(state.getState());
			}
		}
	}
	
	private void readData() {
		wayPoints1 = new WayPointList();
		wayPoints1.setStartPosIndex(WAYPOINT_READ_START_POS);
		wayPoints2 = new WayPointList();
		wayPoints2.setStartPosIndex(WAYPOINT_READ_START_POS);
		try {
			buildWayPointMaps();
		} catch (DOMException | ParserConfigurationException | ParseException
				| SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void race() {
		state.setStateStarted();
		notifyStateObservers();
		int wp1Pos = wayPoints1.getStartPosIndex();
		int wp2Pos = wayPoints2.getStartPosIndex();
		while (wp1Pos <= wayPoints1.size() && wp2Pos <= wayPoints2.size()) {
			WayPoint wayPt1 = wayPoints1.get(wp1Pos); 
			WayPoint wayPt2 = wayPoints2.get(wp2Pos);
			if (wayPt1.getSecs() > MAX_WAY_POINTS_SECS ||
					wayPt2.getSecs() > MAX_WAY_POINTS_SECS) {
				state.setStateResync();
				notifyStateObservers();
				skipWayPointBlackouts(wayPoints1, wp1Pos, wayPoints2, wp2Pos);
				wp1Pos = wayPoints1.getStartPosIndex();;
				wp2Pos = wayPoints2.getStartPosIndex();
				if (state.isStateResync()) {
					log.log("****** Cant re-sync Way Points ******");
					break;
				}
				else if (state.isStateSync()) {
					wp1Pos = wayPoints1.getStartPosIndex();
					wp2Pos = wayPoints2.getStartPosIndex();
					wayPt1 = wayPoints1.get(wp1Pos); 
					wayPt2 = wayPoints2.get(wp2Pos);
					log.log("Race re-synced @ Pos1 " + wp1Pos + " Pos2 " + wp2Pos);
				}
			}
			setGap(GPSUtils.calculateGps2m(wayPt1.getLat(), wayPt1.getLon(), wayPt2.getLat(), wayPt2.getLon()));
//			log.log("Waypoint " + wp1Pos + " Speed1 " + wayPt1.getSpeed() + " Speed2 " + wayPt2.getSpeed() + "\tGap " + gap +"m");
			notifyViewObservers();
			// after processing
			delay(500);
			wp1Pos++;
			wp2Pos++;
		}
		log.log("** RACE FINISHED **");
		state.setStateStopped();
		notifyStateObservers();
	}
	
	private void skipWayPointBlackouts(WayPointList wpList1, int wp1Pos, WayPointList wpList2, int wp2Pos) {
		long wpSecGap = 0;
		if (wpList1.get(wp1Pos).getSecs() > (blackOutSecs - MAX_WAY_POINTS_SECS)) {
			wpSecGap = wpList1.get(wp1Pos).getSecs();
			blackOutSecs = wpList2.get(wp2Pos).getSecs();
			log.log("Skipping Black Outs on List 1: Gap is " + wpSecGap + " Syncing on list 2..");
			syncWayPoints(wpList1, wp1Pos, wpList2, wp2Pos, wpSecGap);
		}
		else {
			wpSecGap = wpList2.get(wp2Pos).getSecs();
			blackOutSecs = wpList1.get(wp1Pos).getSecs();
			log.log("Skipping Black Outs on List 2: Gap is " + wpSecGap + " Syncing on list 1..");
			syncWayPoints(wpList2, wp2Pos, wpList1, wp1Pos, wpSecGap);	
		}
	}
	
	private void syncWayPoints(WayPointList wpList1, int wp1Pos, WayPointList wpList2, int wp2Pos, final long wpSecGap) {
		delay(500);
		log.log("Syncing @ " + wp2Pos + " Gap is " + wpSecGap + " Black Out Secs " + blackOutSecs);
		// TODO add end of list test to for
		for (; wp1Pos < MAX_WAY_POINT_SYNC_THRESHOLD; wp1Pos++) {
			for (; wp2Pos < MAX_WAY_POINT_SYNC_THRESHOLD;) {
				if (isTimeSyncExceeded(blackOutSecs, wpSecGap)) {
					state.setStateExceed();
					notifyStateObservers();
					wpList1.setStartPosIndex(wp1Pos);
					wpList2.setStartPosIndex(wp2Pos);
					log.log("Re-Sync FAILED: WayPoint1 " + wp1Pos + " WayPoint2 " + wp2Pos + " Gap was " + wpSecGap + " Black Out Secs " + blackOutSecs);
					break;
				}
				else if (isTimeSync(blackOutSecs, wpSecGap)) {
					wpList1.setStartPosIndex(wp1Pos);
					wpList2.setStartPosIndex(wp2Pos);
					state.setStateSync();
					log.log("Re-Sync: WayPoint1 " + wp1Pos + " WayPoint2 " + wp2Pos + " Gap was " + wpSecGap + " Black Out Secs " + blackOutSecs);
					break;
				}
				else {
					wp2Pos++;
					blackOutSecs += wpList2.get(wp2Pos).getSecs();
					syncWayPoints(wpList1, wp1Pos, wpList2, wp2Pos, wpSecGap);
				}
			}
			if (!state.isStateResync()) {
				break;
			}
		}
	}
	
	private boolean isTimeSyncExceeded(long accumTime, long targetTime) {
		boolean isTimeSyncExceeded = false;
		if ((accumTime >= targetTime + MAX_SYNC_WAY_POINTS_SECS)) {
			isTimeSyncExceeded = true;
		}
		return isTimeSyncExceeded;
	}

	private boolean isTimeSync(long accumTime, long targetTime) {
		boolean isSync = false;
		if ((accumTime >= targetTime - MAX_SYNC_WAY_POINTS_SECS) && (accumTime <= targetTime + MAX_SYNC_WAY_POINTS_SECS)) {
			isSync = true;
		}
		return isSync;
	}

	private boolean buildWayPointMaps() throws ParserConfigurationException, DOMException, ParseException, SAXException, IOException {
		boolean mapsEstablished = false;
		xmlHandler.init(xmlFile1, wayPoints1);
		xmlHandler.parseXmlFile();
		wayPoints1 = xmlHandler.getWayPointList();
		xmlHandler.init(xmlFile2, wayPoints2);
		xmlHandler.parseXmlFile();
		wayPoints2 = xmlHandler.getWayPointList();
		mapsEstablished = findStartPosition(wayPoints1, wayPoints2);
		if (mapsEstablished) {
			normaliseTimeStamps(wayPoints1);
			normaliseTimeStamps(wayPoints2);
		}
		return mapsEstablished;
	}
	
	private void normaliseTimeStamps(WayPointList wpL) {	
		Calendar wpPrevTime = null;
		for (int wpIndex = 0; wpIndex < wpL.size(); wpIndex++) {
			Calendar wpTime = wpL.get(wpIndex).getTime();
			if (wpPrevTime != null) {
				long secsGap = getTimeDifference(wpPrevTime, wpTime);
				wpL.get(wpIndex).setSecs(secsGap);
//				log.log(wpL.get(wpIndex).toString());
//				delay(200);
			}
			else {
				wpL.get(wpIndex).setSecs(0);
			}
			wpPrevTime = wpTime;
		}
	}
	
	private boolean findStartPosition(WayPointList wpList1, WayPointList wpList2) {
		boolean found = false;
		WayPoint wpL1 = wpList1.get(wpList1.getStartPosIndex());
		for (int wp2Index = wpList2.getStartPosIndex(); wp2Index < wpList2.size(); wp2Index++) {
			WayPoint wpL2 = wpList2.get(wp2Index); 
			double gap = GPSUtils.calculateGps2m(wpL1.getLat(), wpL1.getLon(), wpL2.getLat(), wpL2.getLon());
			if (gap < MAX_START_GAP) {
				long offset = getTimeDifference(wpL1.getTime(), wpL2.getTime());
				wpL2.setSecs(offset);
				log.log("Start point (" 
						+ wpL1.getLat() +  ", " + wpL1.getLon() + ") and "
						+ "(" + wpL2.getLat() + ", " + wpL2.getLon() 
						+ "). Gap is " + gap
						+ " Offset is " + offset);
				log.log("Start Point List 1: " + wpList1.getStartPosIndex() + " Start Point List 2: " + wpList2.getStartPosIndex());
				found = true;
				break;
			}
			if (wp2Index++ > MAX_WAY_POINT_SYNC_THRESHOLD) {
				log.log("****** Start point not found ******");
				break;
			}
		}
		return found;
	}
	
	private static long getTimeDifference(Calendar wayPt1Time, Calendar wayPt2Time) {
		DateTime startTime = new DateTime(wayPt1Time);
		DateTime endTime = new DateTime(wayPt2Time);
		Interval interval = new Interval(startTime, endTime);
		Duration duration = interval.toDuration();
		return duration.getStandardSeconds();
	}
	
	private static void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException exp) {
		}
	}

}
