package com.flossi;

import observers.StateObserverInterface;
import observers.ViewObserverInterface;


public interface StravaModelInterface {

	public void initialize();

	public void race();
	
	public double getGap();
	
	public void setGap(double gap);

	public void registerObserver(StateObserverInterface stateObserverInterface);

	public void registerObserver(ViewObserverInterface viewObserverInterface);

}
