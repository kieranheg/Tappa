package com.flossi;

import com.flossi.observers.StateObserverInterface;
import com.flossi.observers.ViewObserverInterface;
import com.flossi.plumbing.Logging;
import com.flossi.plumbing.State.States;

public class View implements StateObserverInterface, ViewObserverInterface {
	private final String SPACE = " ";
	private ControllerInterface controller;
	private StravaModelInterface model;
	private Logging log;
	
	public View(ControllerInterface controller, StravaModelInterface model) {
		log = new Logging(this.getClass().getSimpleName());
		this.controller = controller;
		this.model = model;
		model.registerObserver((StateObserverInterface) this);
		model.registerObserver((ViewObserverInterface) this);
	}

	@Override
	public void updateState(States state) {
		switch (state) {
		case STARTING :
			log.log("STARTING");
			break;
		case STARTED :
			log.log("STARTED");
			break;
		case STOPPED :
			log.log("STOPPED");
			break;
		case SYNC :
			log.log("SYNC");
			break;
		case RESYNC :
			log.log("RESYNC");
			break;
		case EXCEED :
			log.log("EXCEED");
			break;
		default :
			log.log("*** ERROR unknown state");
			break;
		}
		
	}

	@Override
	public void updateView() {
		double gap = model.getGap();
		System.out.print(gap + "m \t");
		StringBuilder sb = new StringBuilder(); 
		String lastBike = "";
		if (gap > 0.0) {
			sb.append("1");
			lastBike = "2";
		}
		else {
			sb.append("2");
			lastBike = "1";
		}
		for (int n=0; n<gap; n++) {
			sb.append(SPACE);
			if (gap > 10) {
				
			}
		}
		sb.append(lastBike);
		System.out.println(sb.toString());
	}

}
