package com.flossi.plumbing;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Logging {

	private enum State {ON, OFF}; 
	private State state;
	private String className;
	
	public Logging(String className) {
		this.state = State.ON;
		this.className = className;
	}
	
	public void log(String logStr) {
		switch (state) {
		case ON:
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(cal.getTime()) + " " + className + "\t" + logStr);
			break;
		case OFF:
			break;
		default:
			break;
		}
	}

	public void setStateOn() {
		this.state = State.ON;
	}
	
	public void setStateOff() {
		this.state = State.OFF;
	}	
}
