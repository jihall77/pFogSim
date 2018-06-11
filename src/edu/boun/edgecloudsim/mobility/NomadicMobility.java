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
	private int MAX_WIDTH = 250;
	private int MAX_HEIGHT = 250;
	
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
		
		//initialize tree maps and position of mobile devices
		for(int i=0; i<numberOfMobileDevices; i++) {
			treeMapArray.add(i, new TreeMap<Double, Location>());
			
			//Current MAX size is 250
			/*
			int x_pos = (int) Math.random() * 250;
			int y_pos = (int) Math.random() * 250;
			
			int wlan_id = getAccessPoint().getId();
			
			treeMapArray.get(i).put((double)10, new Location(wlan_id, x_pos, y_pos));
			
			
			*/
			int randDatacenterId = SimUtils.getRandomNumber(0, SimSettings.getInstance().getNumOfEdgeDatacenters()-1);
			Node datacenterNode = datacenterList.item(randDatacenterId);
			Element datacenterElement = (Element) datacenterNode;
			Element location = (Element)datacenterElement.getElementsByTagName("location").item(0);
			String attractiveness = location.getElementsByTagName("attractiveness").item(0).getTextContent();
			SimSettings.PLACE_TYPES placeType = SimUtils.stringToPlace(attractiveness);
			int wlan_id = Integer.parseInt(location.getElementsByTagName("wlan_id").item(0).getTextContent());
			int x_pos = Integer.parseInt(location.getElementsByTagName("x_pos").item(0).getTextContent());
			int y_pos = Integer.parseInt(location.getElementsByTagName("y_pos").item(0).getTextContent());

			//start locating user from 10th seconds
			treeMapArray.get(i).put((double)10, new Location(placeType, wlan_id, x_pos, y_pos));
		}
		
		for(int i=0; i<numberOfMobileDevices; i++) {
			TreeMap<Double, Location> treeMap = treeMapArray.get(i);
			int up = (int) (5 * Math.random() - 0.5);
			int right = (int) (5 * Math.random() - 0.5);

			while(treeMap.lastKey() < SimSettings.getInstance().getSimulationTime()) {		
				
				
				int x_pos = treeMapArray.get(i).lastEntry().getValue().getXPos();
				int y_pos = treeMapArray.get(i).lastEntry().getValue().getYPos();				
				int wlan_id = treeMapArray.get(i).lastEntry().getValue().getServingWlanId();
				//Make random numbers to make the vectors
				//Make negatives by subtracting 0.5
				  
				if(x_pos > MAX_WIDTH) right = right * -1;
				if(y_pos > MAX_HEIGHT) up = up * -1;
				
				//Calculate which wlan_id you get
				
				//treeMap.put(treeMap.lastKey() + 0.5, new Location(wlan_id, x_pos + right, y_pos + up));
				
				//Make random numbers to make the vectors
				//Make negatives by subtracting 0.5
				  
				if(x_pos > this.MAX_WIDTH) right = right * -1;
				if(y_pos > this.MAX_HEIGHT) up = up * -1;
				double minDistance = this.MAX_HEIGHT * this.MAX_WIDTH;
				//HashSet<NodeSim> nodes = MM1Queue.getInstance().getNetworkTopology().getNodes();
				//Calculate which wlan_id you get
				/*for(NodeSim node : nodes)
				{
					int nodeX = node.getLocation().getKey();
					int nodeY = node.getLocation().getValue();
					double distance = Math.sqrt(Math.pow(x_pos - nodeX, 2) + Math.pow(y_pos - nodeY, 2));
					if(minDistance > distance) 
					{
						minDistance = distance;
						wlan_id = node.getWlanId();
					}
				}*/
				
				int nodeX, nodeY;
				double distance;
				for(int z = 0; z < datacenterList.getLength(); z++)
				{
					Node datacenterNode = datacenterList.item(z);
					Element datacenterElement = (Element) datacenterNode;
					Element location = (Element)datacenterElement.getElementsByTagName("location").item(0);		
					//int wlan_id = Integer.parseInt(location.getElementsByTagName("wlan_id").item(0).getTextContent());
					nodeX = Integer.parseInt(location.getElementsByTagName("x_pos").item(0).getTextContent());
					nodeY = Integer.parseInt(location.getElementsByTagName("y_pos").item(0).getTextContent());

					distance = Math.sqrt(Math.pow(x_pos - nodeX, 2) + Math.pow(y_pos - nodeY, 2));
					if(minDistance > distance) 
					{
						minDistance = distance;
						wlan_id = Integer.parseInt(location.getElementsByTagName("wlan_id").item(0).getTextContent());
					}
				}
				treeMap.put(treeMap.lastKey() + 0.5, new Location(wlan_id, x_pos + right, y_pos + up));
				
			
				/*
				boolean placeFound = false;
				int currentLocationId = treeMap.lastEntry().getValue().getServingWlanId();
				double waitingTime = expRngList[currentLocationId].sample();
				
				while(placeFound == false){
					int newDatacenterId = SimUtils.getRandomNumber(0,SimSettings.getInstance().getNumOfEdgeDatacenters()-1);
					if(newDatacenterId != currentLocationId){
						placeFound = true;
						Node datacenterNode = datacenterList.item(newDatacenterId);
						Element datacenterElement = (Element) datacenterNode;
						Element location = (Element)datacenterElement.getElementsByTagName("location").item(0);
						String attractiveness = location.getElementsByTagName("attractiveness").item(0).getTextContent();
						SimSettings.PLACE_TYPES placeType = SimUtils.stringToPlace(attractiveness);
						int wlan_id = Integer.parseInt(location.getElementsByTagName("wlan_id").item(0).getTextContent());
						int x_pos = Integer.parseInt(location.getElementsByTagName("x_pos").item(0).getTextContent());
						int y_pos = Integer.parseInt(location.getElementsByTagName("y_pos").item(0).getTextContent());
						//boolean accesspoint = 
						
						
						//treeMap.put(treeMap.lastKey()+waitingTime, new Location(placeType, wlan_id, x_pos, y_pos));
						treeMap.put(treeMap.lastKey()+waitingTime, new Location(wlan_id, x_pos, y_pos));
						System.out.println("treeMap\n\tplaceType = " + placeType + "\n\twlan_id = " + wlan_id + 
								"\n\tx_pos = " + x_pos + "\n\ty_pos = " + y_pos);
					}
				}
				if(!placeFound){
					SimLogger.printLine("impossible is occured! location cannot be assigned to the device!");
			    	System.exit(0);
				}
				*/
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

}
