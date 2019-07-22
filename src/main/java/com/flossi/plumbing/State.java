package com.flossi.plumbing;

public class State {
	
	public enum States {STARTING, STARTED, STOPPED, SYNC, RESYNC, EXCEED}; 
	private States state;
	private Logging log;

	public State() {
		log = new Logging(this.getClass().getSimpleName());
		setStateStopped();
	}
	
	public States getState() {
		return state;
	}

	public boolean isStateResync() {
		return (this.state.equals(States.RESYNC));
	}

	public boolean isStateSync() {
		return (this.state.equals(States.SYNC));
	}
	
	public void setStateStarting() {
		log.log("STARTING");
		this.state = States.STARTING;
	}
	
	public void setStateStarted() {
		log.log("STARTED");
		this.state = States.STARTED;
	}
	
	public void setStateStarted(boolean started) {
		if (started) {
			log.log("STARTED");
			this.state = States.STARTED;
		}
	}
	
	public void setStateStopped() {
		log.log("STOPPED");
		this.state = States.STOPPED;
	}
	
	public void setStateSync() {
		log.log("SYNC");
		this.state = States.SYNC;
	}

	public void setStateResync() {
		log.log("RESYNC");
		this.state = States.RESYNC;
	}	

	public void setStateExceed() {
		log.log("EXCEED");
		this.state = States.EXCEED;
	}	
	
}
