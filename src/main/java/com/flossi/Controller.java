package com.flossi;


public class Controller implements ControllerInterface {

	StravaModelInterface model;
	View view;
	
	public Controller(StravaModelInterface model) {
		this.model = model;
		this.view = new View(this, model);
	}
	
	@Override
	public void start() {
		model.initialize();		
	}

	@Override
	public void stop() {	
	}

	@Override
	public void race() {
		model.race();
	}

}
