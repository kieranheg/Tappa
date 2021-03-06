package com.flossi;

public class FlossyV2 {

	final static String XMLFILE1 = "src/main/resources/Ride1.xml";
	final static String XMLFILE2 = "src/main/resources/Ride2.xml";
	
	public static void main(String[] args) {
		StravaModelInterface model = new StravaModel(XMLFILE1, XMLFILE2);
		ControllerInterface controller = new Controller(model);
		controller.start();
		controller.race();
		controller.stop();
	}

}
