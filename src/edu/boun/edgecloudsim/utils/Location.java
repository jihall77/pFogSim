/*
 * Title:        EdgeCloudSim - Location
 * 
 * Description:  Location class used in EdgeCloudSim
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.utils;

import edu.boun.edgecloudsim.core.SimSettings;

public class Location {
	private double xPos;
	private double yPos;
	private int servingWlanId;
	SimSettings.PLACE_TYPES placeType;
	public Location(SimSettings.PLACE_TYPES _placeType, int _servingWlanId, double _xPos, double _yPos){
		servingWlanId = _servingWlanId;
		placeType=_placeType;
		xPos = _xPos;
		yPos = _yPos;
	}
	
	public Location(int _servingWlanId, double _xPos, double _yPos) {
		servingWlanId = _servingWlanId;
		xPos = _xPos;
		yPos = _yPos;
	}
	
	@Override
	public boolean equals(Object other){
		boolean result = false;
	    if (other == null) return false;
	    if (!(other instanceof Location))return false;
	    if (other == this) return true;
	    
	    Location otherLocation = (Location)other;
	    if(this.xPos == otherLocation.xPos && this.yPos == otherLocation.yPos)
	    	result = true;

	    return result;
	}

	public int getServingWlanId(){
		return servingWlanId;
	}
	
	public SimSettings.PLACE_TYPES getPlaceType(){
		return placeType;
	}
	
	public double getXPos(){
		return xPos;
	}
	
	public double getYPos(){
		return yPos;
	}
}
