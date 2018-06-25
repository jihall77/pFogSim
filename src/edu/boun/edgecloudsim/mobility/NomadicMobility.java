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
		
		//Beware, I may make this its own object because it is hard to understand right now
		
		//We have...
			//A list of pairs
				//The pairs' key = Pair<Integer, Pair<Integer, Integer>>
					//Where the Integer is the wlan_id, the second pair here is the location (x as key and y as value)
				//The pairs' value = Pair<Boolean, Pair<Integer, Integer>>
					//Where the Boolean is whether the node is moving or not and the second pair describes the vector at which it moves
					// 	with dx as the key and dy as the value
		ArrayList<Pair<Pair<Integer, Pair<Double, Double>>, Pair<Boolean, Pair<Double, Double>>>> accessPoints = new ArrayList<Pair<Pair<Integer, Pair<Double, Double>>, Pair<Boolean, Pair<Double, Double>>>>();
		for(int i = 0; i < datacenterList.getLength(); i++)
		{
			Node datacenterNode = datacenterList.item(i);
			Element datacenterElement = (Element) datacenterNode;
			Element location = (Element)datacenterElement.getElementsByTagName("location").item(0);
			boolean wap = Boolean.parseBoolean(location.getElementsByTagName("wap").item(0).getTextContent());
			if(wap)
			{
				int wlan_id = Integer.parseInt(location.getElementsByTagName("wlan_id").item(0).getTextContent());
				double x_pos = Double.parseDouble(location.getElementsByTagName("x_pos").item(0).getTextContent());
				double y_pos = Double.parseDouble(location.getElementsByTagName("y_pos").item(0).getTextContent());
				boolean moving = Boolean.parseBoolean(location.getElementsByTagName("moving").item(0).getTextContent());
				double dx=0.0, dy=0.0;
				if(moving)
				{
					dx = Integer.parseInt(location.getElementsByTagName("dx").item(0).getTextContent());
					dy = Integer.parseInt(location.getElementsByTagName("dy").item(0).getTextContent());
				}
				Pair<Double, Double> loc = new Pair<Double, Double>(x_pos, y_pos);
				Pair<Integer, Pair<Double, Double>> key = new Pair<Integer, Pair<Double, Double>>(wlan_id, loc);
				
				Pair<Double, Double> vector = new Pair<Double, Double>(dx, dy);
				Pair<Boolean, Pair<Double, Double>> value = new Pair<Boolean, Pair<Double, Double>>(moving, vector);
				
				Pair<Pair<Integer, Pair<Double, Double>>, Pair<Boolean, Pair<Double, Double>>> info = new Pair<Pair<Integer, Pair<Double, Double>>, Pair<Boolean, Pair<Double, Double>>>(key, value);
				
				accessPoints.add(info);
			}
		}
			
		
		//initialize tree maps and position of mobile devices
		for(int i=0; i<numberOfMobileDevices; i++) {
			treeMapArray.add(i, new TreeMap<Double, Location>());
			
			//Picks a random wireless access point to start at
			int randDatacenterId = SimUtils.getRandomNumber(0, accessPoints.size()-1);
			int wlan_id = accessPoints.get(randDatacenterId).getKey().getKey();
			double x_pos = accessPoints.get(randDatacenterId).getKey().getValue().getKey();
			double y_pos = accessPoints.get(randDatacenterId).getKey().getValue().getValue();
			
			//start locating user from 10th seconds
			treeMapArray.get(i).put((double)10, new Location(wlan_id, x_pos, y_pos));
		}

		for(int i=0; i<numberOfMobileDevices; i++) {
			TreeMap<Double, Location> treeMap = treeMapArray.get(i);
			//Make random numbers to make the vectors
			double up = (int) (5 * (Math.random() - 0.5));
			double right = (int) (5 * (Math.random() - 0.5));

			while(treeMap.lastKey() < SimSettings.getInstance().getSimulationTime()) {		
				
				
				double x_pos = treeMap.lastEntry().getValue().getXPos();
				double y_pos = treeMap.lastEntry().getValue().getYPos();				
				int wlan_id = treeMap.lastEntry().getValue().getServingWlanId();
				  
				if(x_pos + right > this.MAX_WIDTH) right = right * -1;
				if(y_pos + up > this.MAX_HEIGHT) up = up * -1;
				double minDistance = this.MAX_HEIGHT * this.MAX_WIDTH;

				//Calculate which wlan_id you get
				
				double distance = 1000;
				for(int a = 0; a < accessPoints.size(); a++)
				{	
					if(accessPoints.get(a).getKey().getKey() == wlan_id)
					{
						double nodeX = accessPoints.get(a).getKey().getValue().getKey();
						double nodeY = accessPoints.get(a).getKey().getValue().getValue();
						if(accessPoints.get(a).getValue().getKey())
						{
							double dx = accessPoints.get(a).getValue().getValue().getKey();
							double dy = accessPoints.get(a).getValue().getValue().getValue();
							
							//Ensure they don't go past the borders of the simulation space
							if(nodeX + dx > this.MAX_WIDTH) dx = dx * -1;
							if(nodeY + dy > this.MAX_HEIGHT) dy = dy * -1;
							//Add the vector values to their respective components
							nodeX += dx * treeMap.size();
							nodeY += dy * treeMap.size();
						}

						distance = Math.sqrt(Math.pow(x_pos - nodeX + right, 2) + Math.pow(y_pos - nodeY + up, 2));
						break;
					}
				}
				
				//If the previous node is going to be further than 10 or more away, change nodes
				if(distance > 10)
				{
				double nodeX, nodeY;
				
				for(int z = 0; z < accessPoints.size(); z++)
					{						
						nodeX = accessPoints.get(z).getKey().getValue().getKey();
						nodeY = accessPoints.get(z).getKey().getValue().getValue();
						
						if(accessPoints.get(z).getValue().getKey())
						{
							double dx = accessPoints.get(z).getValue().getValue().getKey();
							double dy = accessPoints.get(z).getValue().getValue().getValue();
							
							//Ensure they don't go past the borders of the simulation space
							if(nodeX + dx > this.MAX_WIDTH) dx = dx * -1;
							if(nodeY + dy > this.MAX_HEIGHT) dy = dy * -1;
							//Add the vector values to their respective components
							nodeX += dx * treeMap.size();
							nodeY += dy * treeMap.size();
						}
						distance = Math.sqrt(Math.pow(x_pos - nodeX, 2) + Math.pow(y_pos - nodeY, 2));
						if(minDistance > distance) 
						{
							minDistance = distance;
							wlan_id = accessPoints.get(z).getKey().getKey();
						}
					}
				}
				//This first argument kind of dictates the speed at which the device moves, higher it is, slower the devices are
				//	smaller value in there, the more it updates
				//As it is now, allows devices to change wlan_ids around 600 times in an hour
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
