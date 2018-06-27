/*
 * Equal Share Bandwidth Model
 * Considers that each device receives an equal share of a location's available bandwidth
 */

package edu.auburn.pFogSim.netsim;

import org.cloudbus.cloudsim.core.CloudSim;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
//import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.network.NetworkModel;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimLogger;
import java.util.LinkedList;
//import edu.auburn.pFogSim.netsim.*;

public class ESBModel extends NetworkModel {
	private double WlanPoissonMean; //seconds
	private double WanPoissonMean; //seconds
	private double avgTaskInputSize; //bytes
	private double avgTaskOutputSize; //bytes
	private int maxNumOfClientsInPlace;
	private NetworkTopology networkTopology;
	private static ESBModel instance = null;
	private Router router;
	
	public ESBModel() {
		super();
	}
	
	public ESBModel(int _numberOfMobileDevices) {
		super(_numberOfMobileDevices);
	}
	
	public static ESBModel getInstance() {
		if(instance == null) {
			instance = new ESBModel();
		}
		return instance;
	}
	
	@Override
	public void initialize() {
		WlanPoissonMean=0;
		WanPoissonMean=0;
		avgTaskInputSize=0;
		avgTaskOutputSize=0;
		maxNumOfClientsInPlace=0;
		
		//Calculate interarrival time and task sizes
		double numOfTaskType = 0;
		SimSettings SS = SimSettings.getInstance();
		for (SimSettings.APP_TYPES taskType : SimSettings.APP_TYPES.values()) {
			double weight = SS.getTaskLookUpTable()[taskType.ordinal()][0]/(double)100;
			if(weight != 0) {
				WlanPoissonMean += (SS.getTaskLookUpTable()[taskType.ordinal()][2])*weight;
				
				double percentageOfCloudCommunication = SS.getTaskLookUpTable()[taskType.ordinal()][1];
				WanPoissonMean += (WlanPoissonMean)*((double)100/percentageOfCloudCommunication)*weight;
				
				avgTaskInputSize += SS.getTaskLookUpTable()[taskType.ordinal()][5]*weight;
				
				avgTaskOutputSize += SS.getTaskLookUpTable()[taskType.ordinal()][6]*weight;
				
				numOfTaskType++;
			}
		}

		//networkTopology.cleanNodes();
		WlanPoissonMean = WlanPoissonMean/numOfTaskType;
		avgTaskInputSize = avgTaskInputSize/numOfTaskType;
		avgTaskOutputSize = avgTaskOutputSize/numOfTaskType;
		router = new Router();
	}

    /**
    * source device is always mobile device in our simulation scenarios!
    */
	@Override
	public double getUploadDelay(int sourceDeviceId, int destDeviceId, double dataSize, boolean wifiSrc, boolean wifiDest) {
		double delay = 0;
		Location accessPointLocation = null;
		Location destPointLocation = null;
		/*changed by pFogSim--
		 * ok... so this looks really stupid, but its not... mostly
		 * unfortunately mobile devices and host devices use the same range of id's
		 * and this is far too deep to go through the process of separating them
		 * as such, anytime that a host device is sent into this method it is multiplied by -1
		 * this will cause and index out of bounds exception when searching for a mobile location
		 * when you get such exception, flip the sign of the id and search it as a host
		 */
		try {
			accessPointLocation = SimManager.getInstance().getMobilityModel().getLocation(sourceDeviceId,CloudSim.clock());
		}
		catch (IndexOutOfBoundsException e) {
			sourceDeviceId *= -1;
			accessPointLocation = SimManager.getInstance().getLocalServerManager().findHostById(sourceDeviceId).getLocation();
		}
		try {
			destPointLocation = SimManager.getInstance().getMobilityModel().getLocation(destDeviceId,CloudSim.clock());
		}
		catch (IndexOutOfBoundsException e) {
			destDeviceId *= -1;
			destPointLocation = SimManager.getInstance().getLocalServerManager().findHostById(destDeviceId).getLocation();
		}
		Location source;
		Location destination;
		NodeSim src;
		NodeSim dest;
		NodeSim current;
		NodeSim nextHop;
		LinkedList<NodeSim> path = null;
		source = new Location(accessPointLocation.getXPos(), accessPointLocation.getYPos());
		destination = new Location(destPointLocation.getXPos(), destPointLocation.getYPos());
		
		if(wifiSrc) {
			src = networkTopology.findNode(source, true);
		}
		else {
			src = networkTopology.findNode(source, false);
		}
		if(wifiDest) {
			dest = networkTopology.findNode(destination, true);
		}
		else {
			dest = networkTopology.findNode(destination, false);
		}
	    path = router.findPath(networkTopology, src, dest);
		delay += getWlanUploadDelay(accessPointLocation, CloudSim.clock());
		//SimLogger.printLine("Number of hops: " + path.size());
		while (!path.isEmpty()) {
			current = path.poll();
			nextHop = path.peek();
			accessPointLocation = new Location(null, 0, current.getLocation().getXPos(), current.getLocation().getYPos());//we only care about the x-y position, the other details are irrelevant here
			delay += getWlanUploadDelay(accessPointLocation, CloudSim.clock() + delay);
			if (nextHop == null) {
				break;
			}
			if (current.traverse(nextHop) < 0) {
				SimLogger.printLine("not adjacent");
			}
			delay += current.traverse(nextHop);
		}
		
		/*
		//mobile device to cloud server
		if(destDeviceId == SimSettings.CLOUD_DATACENTER_ID){
			double wlanDelay = getWlanUploadDelay(accessPointLocation, CloudSim.clock());
			double wanDelay = getWanUploadDelay(accessPointLocation, CloudSim.clock() + wlanDelay);
			if(wlanDelay > 0 && wanDelay >0)
				delay = wlanDelay + wanDelay;
		}
		//mobile device to edge orchestrator
		else if(destDeviceId == SimSettings.EDGE_ORCHESTRATOR_ID){
			delay = getWlanUploadDelay(accessPointLocation, CloudSim.clock()) +
					SimSettings.getInstance().getInternalLanDelay();
		}
		//mobile device to edge device (wifi access point)
		else if (destDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID) {
			delay = getWlanUploadDelay(accessPointLocation, CloudSim.clock());
		}
		*/
		return delay;
	}

    /**
    * destination device is always mobile device in our simulation scenarios!
    */
	@Override
	public double getDownloadDelay(int sourceDeviceId, int destDeviceId, double dataSize, boolean wifiSrc, boolean wifiDest) {
		/*
		//Special Case -> edge orchestrator to edge device
		if(sourceDeviceId == SimSettings.EDGE_ORCHESTRATOR_ID &&
				destDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID){
			return SimSettings.getInstance().getInternalLanDelay();
		}

		double delay = 0;
		Location accessPointLocation = SimManager.getInstance().getMobilityModel().getLocation(destDeviceId,CloudSim.clock());
		
		//cloud server to mobile device
		if(sourceDeviceId == SimSettings.CLOUD_DATACENTER_ID){
			double wlanDelay = getWlanDownloadDelay(accessPointLocation, CloudSim.clock());
			double wanDelay = getWanDownloadDelay(accessPointLocation, CloudSim.clock() + wlanDelay);
			if(wlanDelay > 0 && wanDelay >0)
				delay = wlanDelay + wanDelay;
		}
		//edge device (wifi access point) to mobile device
		else{
			delay = getWlanDownloadDelay(accessPointLocation, CloudSim.clock());
			
			EdgeHost host = (EdgeHost)(SimManager.
					getInstance().
					getLocalServerManager().
					getDatacenterList().get(sourceDeviceId).
					getHostList().get(0));
			
			//if source device id is the edge server which is located in another location, add internal lan delay
			//in our scenasrio, serving wlan ID is equal to the host id, because there is only one host in one place
			if(host.getLocation().getServingWlanId() != accessPointLocation.getServingWlanId())
				delay += (SimSettings.getInstance().getInternalLanDelay() * 2);
		}
		*/
		return getUploadDelay(sourceDeviceId, destDeviceId, dataSize, wifiSrc, wifiDest);//getUploadDelay has been made bi-directional
	}
	
	public int getMaxNumOfClientsInPlace(){
		return maxNumOfClientsInPlace;
	}
	
	private int getDeviceCount(Location deviceLocation, double time){
		int deviceCount = 0;
		
		for(int i=0; i<numberOfMobileDevices; i++) {
			Location location = SimManager.getInstance().getMobilityModel().getLocation(i,time);
			if(location.equals(deviceLocation))
				deviceCount++;
		}
		
		//record max number of client just for debugging
		if(maxNumOfClientsInPlace<deviceCount)
			maxNumOfClientsInPlace = deviceCount;
		
		return deviceCount;
	}
	
	private double calculateMM1(double propogationDelay, int bandwidth /*Kbps*/, double PoissonMean, double avgTaskSize /*KB*/, int deviceCount){
		double Bps=0, mu=0, lamda=0;
		
		avgTaskSize = avgTaskSize * (double)1000; //convert from KB to Byte
		
		Bps = bandwidth * (double)1000 / (double)8; //convert from Kbps to Byte per seconds
        //lamda = ((double)1/(double)PoissonMean); //task per seconds
		//mu = Bps / avgTaskSize ; //task per seconds
		double result = (avgTaskSize * deviceCount) / Bps;
		
		result += propogationDelay;
		//SimLogger.printLine("Delay: " + result);
		return result;
	}
	
	private double getWlanDownloadDelay(Location accessPointLocation, double time) {
		return calculateMM1(0,
				SimSettings.getInstance().getWlanBandwidth(),
				WlanPoissonMean,
				avgTaskOutputSize,
				getDeviceCount(accessPointLocation, time));
	}
	
	private double getWlanUploadDelay(Location accessPointLocation, double time) {
		return calculateMM1(0,
				SimSettings.getInstance().getWlanBandwidth(),
				WlanPoissonMean,
				avgTaskInputSize,
				getDeviceCount(accessPointLocation, time));
	}
	
	private double getWanDownloadDelay(Location accessPointLocation, double time) {
		return calculateMM1(SimSettings.getInstance().getWanPropogationDelay(),
				SimSettings.getInstance().getWanBandwidth(),
				WanPoissonMean,
				avgTaskOutputSize,
				getDeviceCount(accessPointLocation, time));
	}
	
	private double getWanUploadDelay(Location accessPointLocation, double time) {
		return calculateMM1(SimSettings.getInstance().getWanPropogationDelay(),
				SimSettings.getInstance().getWanBandwidth(),
				WanPoissonMean,
				avgTaskInputSize,
				getDeviceCount(accessPointLocation, time));
	}
	
	public void setNetworkTopology(NetworkTopology _networkTopology) {
		networkTopology = _networkTopology;
	}
	
	public NetworkTopology getNetworkTopology() {
		return networkTopology;
	}

	@Override
	public void uploadStarted(Location accessPointLocation, int destDeviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void uploadFinished(Location accessPointLocation, int destDeviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadStarted(Location accessPointLocation, int sourceDeviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadFinished(Location accessPointLocation, int sourceDeviceId) {
		// TODO Auto-generated method stub
		
	}
}