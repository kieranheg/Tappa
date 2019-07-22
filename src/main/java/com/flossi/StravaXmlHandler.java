package com.flossi;

import com.flossi.plumbing.Logging;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Calendar;


public class StravaXmlHandler extends XmlHandler {
	final String METADATA = "metadata";
	final String TRACK_POINT = "trkpt";
	final String LAT = "lat";
	final String LON = "lon";
	final String SPEED = "ele";
	final String TIME = "time";
	
	Logging log;
	private WayPointList wpList;
	private String xmlFile;
	
	public StravaXmlHandler() throws ParserConfigurationException {
		super();
		log = new Logging(this.getClass().getSimpleName());
	}
	
	public void init(String xmlFile, WayPointList wpList) throws ParserConfigurationException, SAXException, IOException {
		super.init(xmlFile);
		this.xmlFile =xmlFile;
		this.wpList = wpList;
	}
	
	public void parseXmlFile() {
		getStartTime();
		getWayPoints();
	}
	
	public WayPointList getWayPointList() {
		return wpList;
	}
	
	private void getStartTime() {
		Element element = getElement(METADATA);
		if (element != null) {
			String date = element.getElementsByTagName(TIME).item(0)
					.getChildNodes().item(0).getNodeValue();						
			Calendar cal = DatatypeConverter.parseDateTime(date);
			wpList.setStartTime(cal);
			log.log(xmlFile +  " Start time is " + date);
		}
	}

	private void getWayPoints() {
		NodeList nodeList = getNodeList(TRACK_POINT);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;		
				double lat = Double.parseDouble(node.getAttributes().getNamedItem(LAT).getNodeValue());
				double lon = Double.parseDouble(node.getAttributes().getNamedItem(LON).getNodeValue());
				double speed = Double.parseDouble(elem.getElementsByTagName(SPEED).item(0)
						.getChildNodes().item(0).getNodeValue());
				String date = elem.getElementsByTagName(TIME).item(0)
						.getChildNodes().item(0).getNodeValue();						
				Calendar cal = DatatypeConverter.parseDateTime(date);

				wpList.add(lat, lon, speed, cal);
			}
		}
		log.log("Finished parsing " + xmlFile + ", " + wpList.size() + " waypoints read");	
	}
		
}
