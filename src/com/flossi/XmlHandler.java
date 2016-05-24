package com.flossi;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlHandler {
    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    Document document;
    
	public XmlHandler() throws ParserConfigurationException {
		this.factory = DocumentBuilderFactory.newInstance();
		this.builder = factory.newDocumentBuilder();
	}
	
	public void init(String xmlFile) throws ParserConfigurationException, SAXException, IOException {
		document = builder.parse(new File(xmlFile));
	}
	
	public NodeList getNodeList(String EleTag) {
		NodeList nodeList = document.getElementsByTagName(EleTag);
		return nodeList;
	}

	public Element getElement(String EleTag) {
		Element element = null;
		NodeList nodeListStart = getNodeList(EleTag);
		for (int i = 0; i < nodeListStart.getLength(); i++) {
			Node node = nodeListStart.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				element = (Element) node;
			}	
		}
		return element;
	}
	
//	
//	public void parseXmlFile() throws DOMException, ParseException, SAXException, IOException {
//		log.log("Parsing xml file " + xmlFile);		
//		document = builder.parse(new File(xmlFile));
//		
//		NodeList nodeListStart = document.getElementsByTagName(METADATA);
//		for (int i = 0; i < nodeListStart.getLength(); i++) {
//			Node node = nodeListStart.item(i);
//			if (node.getNodeType() == Node.ELEMENT_NODE) {
//				Element elem = (Element) node;		
//				String date = elem.getElementsByTagName(TIME).item(0)
//						.getChildNodes().item(0).getNodeValue();						
//				Calendar cal = DatatypeConverter.parseDateTime(date);
//				wpList.setStartTime(cal);
//				log.log(xmlFile +  " Start time is " + date);	
//			}	
//		}
//
//		int wayPts = 0;
//		NodeList nodeList = document.getElementsByTagName(TRACK_POINT);
//		for (int i = 0; i < nodeList.getLength(); i++) {
//			Node node = nodeList.item(i);
//			if (node.getNodeType() == Node.ELEMENT_NODE) {
//				Element elem = (Element) node;		
//				double lat = Double.parseDouble(node.getAttributes().getNamedItem(LAT).getNodeValue());
//				double lon = Double.parseDouble(node.getAttributes().getNamedItem(LON).getNodeValue());
//				double speed = Double.parseDouble(elem.getElementsByTagName(SPEED).item(0)
//						.getChildNodes().item(0).getNodeValue());
//				String date = elem.getElementsByTagName(TIME).item(0)
//						.getChildNodes().item(0).getNodeValue();						
//				Calendar cal = DatatypeConverter.parseDateTime(date);
//
//				wpList.add(lat, lon, speed, cal);
//				wayPts++;
//			}	
//		}
//		log.log("Finished parsing " + xmlFile + ", " + wayPts + " waypoints read");		
//	}	
}
