/*
 * Title:        EdgeCloudSim - Nomadic Mobility model implementation
 * 
 * Description: 
 * MobilityModel implements basic nomadic mobility model where the
 * place of the devices are changed from time to time instead of a
 * continuous location update.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.mobility;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import javafx.util.Pair;


import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;


public class NomadicMobility extends MobilityModel {
	private List<TreeMap<Double, Location>> treeMapArray;
	private int MAX_WIDTH = 1000;
	private int MAX_HEIGHT = 1000;
	
	public NomadicMobility(int _numberOfMobileDevices, double _simulationTime) {
		super(_numberOfMobileDevices, _simulationTime);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initialize() {
		treeMapArray = new ArrayList<TreeMap<Double, Location>>();
		
		ExponentialDistribution[] expRngList = new ExponentialDistribution[SimSettings.getInstance().getNumOfEdgeDatacenters()];

		//create random number generator for each place
		Document doc = SimSettings.getInstance().getEdgeDevicesDocument();
		NodeList datacenterList = doc.getElementsByTagName("datacenter");
		for (int i = 0; i < datacenterList.getLength(); i++) {
			Node datacenterNode = datacenterList.item(i);
			Element datacenterElement = (Element) datacenterNode;
			Element location = (Element)datacenterElement.getElementsByTagName("location").item(0);
			String attractiveness = location.getElementsByTagName("attractiveness").item(0).getTextContent();
			SimSettings.PLACE_TYPES placeType = SimUtils.stringToPlace(attractiveness);
			
			expRngList[i] = new ExponentialDistribution(SimSettings.getInstance().getMobilityLookUpTable()[placeType.ordinal()]);
		}
		
		//Go through datacenterlist and pick out just the wireless access points
		ArrayList<Pair<Integer, Pair<Integer, Integer>>> accessPoints = new ArrayList<Pair<Integer, Pair<Integer, Integer>>>();
		for(int i = 0; i < datacenterList.getLength(); i++)
		{
			Node datacenterNode = datacenterList.item(i);
			Element datacenterElement = (Element) datacenterNode;
			Element location = (Element)datacenterElement.getElementsByTagName("location").item(0);
			boolean wap = Boolean.parseBoolean(location.getElementsByTagName("wap").item(0).getTextContent());
			if(wap)
			{
				int wlan_id = Integer.parseInt(location.getElementsByTagName("wlan_id").item(0).getTextContent());
				int x_pos = Integer.parseInt(location.getElementsByTagName("x_pos").item(0).getTextContent());
				int y_pos = Integer.parseInt(location.getElementsByTagName("y_pos").item(0).getTextContent());
				Pair<Integer, Integer> loc = new Pair<Integer, Integer>(x_pos, y_pos);
				Pair<Integer, Pair<Integer, Integer>> info = new Pair<Integer, Pair<Integer, Integer>>(wlan_id, loc);
				accessPoints.add(info);
			}
		}
			
		
		//initialize tree maps and position of mobile devices
		for(int i=0; i<numberOfMobileDevices; i++) {
			treeMapArray.add(i, new TreeMap<Double, Location>());
			
			//Picks a random wireless access point to start at
			int randDatacenterId = SimUtils.getRandomNumber(0, accessPoints.size()-1);
			int wlan_id = accessPoints.get(randDatacenterId).getKey();
			int x_pos = accessPoints.get(randDatacenterId).getValue().getKey();
			int y_pos = accessPoints.get(randDatacenterId).getValue().getValue();
			
			//start locating user from 10th seconds
			treeMapArray.get(i).put((double)10, new Location(wlan_id, x_pos, y_pos));
		}
		
		for(int i=0; i<numberOfMobileDevices; i++) {
			TreeMap<Double, Location> treeMap = treeMapArray.get(i);
			//Make random numbers to make the vectors
			int up = (int) (5 * (Math.random() - 0.5));
			int right = (int) (5 * (Math.random() - 0.5));

			while(treeMap.lastKey() < SimSettings.getInstance().getSimulationTime()) {		
				
				
				int x_pos = treeMap.lastEntry().getValue().getXPos();
				int y_pos = treeMap.lastEntry().getValue().getYPos();				
				int wlan_id = treeMap.lastEntry().getValue().getServingWlanId();
				  
				if(x_pos > this.MAX_WIDTH) right = right * -1;
				if(y_pos > this.MAX_HEIGHT) up = up * -1;
				double minDistance = this.MAX_HEIGHT * this.MAX_WIDTH;

				//Calculate which wlan_id you get
				
				double distance = 1000;
				for(int a = 0; a < accessPoints.size(); a++)
				{	
					if(accessPoints.get(a).getKey() == wlan_id)
					{
						int nodeX = accessPoints.get(a).getValue().getKey();
						int nodeY = accessPoints.get(a).getValue().getValue();
						distance = Math.sqrt(Math.pow(x_pos - nodeX + right, 2) + Math.pow(y_pos - nodeY + up, 2));
						break;
					}
				}
				
				//If the previous node is going to be further than 10 or more away, change nodes
				if(distance > 10)
				{
				int nodeX, nodeY;
				
				for(int z = 0; z < accessPoints.size(); z++)
					{						
						nodeX = accessPoints.get(z).getValue().getKey();
						nodeY = accessPoints.get(z).getValue().getValue();
						distance = Math.sqrt(Math.pow(x_pos - nodeX, 2) + Math.pow(y_pos - nodeY, 2));
						if(minDistance > distance) 
						{
							minDistance = distance;
							wlan_id = accessPoints.get(z).getKey();
						}
					}
				}
				//This first argument kind of dictates the speed at which the device moves, higher it is, slower the devices are
				//	smaller value in there, the more it updates
				treeMap.put(treeMap.lastKey() + 50.0, new Location(wlan_id, x_pos + right, y_pos + up));		
				
			}
		}
	}

	

	@Override
	public Location getLocation(int deviceId, double time) {
		TreeMap<Double, Location> treeMap = treeMapArray.get(deviceId);
		
		Entry<Double, Location> e = treeMap.floorEntry(time);
	    
	    if(e == null){
	    	SimLogger.printLine("impossible is occured! no location is found for the device!");
	    	System.exit(0);
	    }
	    
		return e.getValue();
	}
	public int getWlanId(int deviceId, double time) 
	{
		int wlan_id = -1;
		
		if(time >= 0 && deviceId >= 0)
		{	
			TreeMap<Double, Location> treeMap = treeMapArray.get(deviceId);
			
			Entry<Double, Location> e = treeMap.floorEntry(time);
			
			wlan_id = e.getValue().getServingWlanId();
		}
		else throw new IllegalArgumentException();
		return wlan_id;
	}
	
	public int getWlanId(int deviceId) 
	{
		//Gets device id at time 20.0, lower number won't work
		int wlan_id = -1;
		
		if(deviceId >= 0)
		{	
			TreeMap<Double, Location> treeMap = treeMapArray.get(deviceId);

			Entry<Double, Location> e = treeMap.floorEntry(20.0);
			
			wlan_id = e.getValue().getServingWlanId();
		}
		else throw new IllegalArgumentException();
		return wlan_id;
	}
	
	public int getSize()
	{
		return treeMapArray.size();
	}

}
