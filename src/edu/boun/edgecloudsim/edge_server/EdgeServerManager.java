/*
 * Title:        EdgeCloudSim - Edge Server Manager
 * 
 * Description: 
 * EdgeServerManager is responsible for creating datacenters, hosts and VMs.
 * It also provides the list of VMs running on the hosts.
 * This information is critical for the edge orchestrator.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.edge_server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;
import javafx.util.Pair;
import edu.auburn.pFogSim.clustering.*;
import edu.auburn.pFogSim.netsim.*;
import edu.boun.edgecloudsim.network.MM1Queue;

public class EdgeServerManager {
	private List<Datacenter> localDatacenters;
	private List<List<EdgeVM>> vmList;
	private int hostIdCounter;
	
	//CJ Added these to make the lists of all the nodes and respective links 
	//	to pass to topology constructor
	private List<NodeSim> nodesForTopography = new ArrayList<NodeSim>();
	private List<Link> linksForTopography = new ArrayList<Link>();

	public EdgeServerManager() {
		localDatacenters=new ArrayList<Datacenter>();
		vmList = new ArrayList<List<EdgeVM>>();
		hostIdCounter = 0;
	}

	public List<EdgeVM> getVmList(int hostId){
		return vmList.get(hostId);
	}
	
	public List<Datacenter> getDatacenterList(){
		return localDatacenters;
	}
	
	public void startDatacenters() throws Exception{
		//create random number generator for each place
		Document doc = SimSettings.getInstance().getEdgeDevicesDocument();
		NodeList datacenterList = doc.getElementsByTagName("datacenter");
		for (int i = 0; i < datacenterList.getLength(); i++) {
			Node datacenterNode = datacenterList.item(i);
			Element datacenterElement = (Element) datacenterNode;
			localDatacenters.add(createDatacenter(i, datacenterElement));
		}
		
		//CJ Adding the links after the nodes, essentially the same process as with nodes
		doc = SimSettings.getInstance().getLinksDocument();
		NodeList linksList = doc.getElementsByTagName("link");
		for(int i = 0; i < linksList.getLength(); i++) {
			
			Node links = linksList.item(i);
			Element linkElement = (Element) links;
			
			NodeList leftLinksList = linkElement.getElementsByTagName("left");
			Node leftLinks = leftLinksList.item(0);
			Element leftLinkss = (Element)leftLinks;
			int x_pos1 = Integer.parseInt(leftLinkss.getElementsByTagName("x_pos").item(0).getTextContent());
			int y_pos1 = Integer.parseInt(leftLinkss.getElementsByTagName("y_pos").item(0).getTextContent());
			Pair<Integer, Integer> leftCoor = new Pair<Integer, Integer>(x_pos1, y_pos1);
			//SimLogger.printLine("We found x_pos1 = " + x_pos1 + " and y_pos1 = " + y_pos1);
			
			NodeList rightLinksList = linkElement.getElementsByTagName("right");
			Node rightLinks = rightLinksList.item(0);
			Element rightLinkss = (Element)rightLinks;
			int x_pos2 = Integer.parseInt(rightLinkss.getElementsByTagName("x_pos").item(0).getTextContent());
			int y_pos2 = Integer.parseInt(rightLinkss.getElementsByTagName("y_pos").item(0).getTextContent());
			Pair<Integer, Integer> rightCoor = new Pair<Integer, Integer>(x_pos2, y_pos2);

			//SimLogger.printLine("We found x_pos1 = " + x_pos2 + " and y_pos1 = " + y_pos2);
			
			double left_lat = Double.parseDouble(linkElement.getElementsByTagName("left_latency").item(0).getTextContent());
			double right_lat = Double.parseDouble(linkElement.getElementsByTagName("right_latency").item(0).getTextContent());
			
			Link newLink = new Link(rightCoor,leftCoor, right_lat, left_lat);
			linksForTopography.add(newLink);
		}
		//This line that comes next needs to be apologized for, it's horrific and I'm sorry
		//	-CJ
		FogHierCluster clusterObject = new FogHierCluster((ArrayList<NodeSim>)nodesForTopography);
		NetworkTopology networkTopology = new NetworkTopology(nodesForTopography, linksForTopography);
		if(!networkTopology.cleanNodes())
		{
			SimLogger.printLine("Topology is not valid");
			System.exit(0);
		}
		//JIH lets see if this works
		((MM1Queue) SimManager.getInstance().getNetworkModel()).setNetworkTopology(networkTopology);
		
	}

	public void createVmList(int brockerId){
		int hostCounter=0;
		int vmCounter=0;
		
		//SimLogger.printLine("createVmList reached");
		
		//Create VMs for each hosts
		Document doc = SimSettings.getInstance().getEdgeDevicesDocument();
		NodeList datacenterList = doc.getElementsByTagName("datacenter");
		for (int i = 0; i < datacenterList.getLength(); i++) {
			Node datacenterNode = datacenterList.item(i);
			Element datacenterElement = (Element) datacenterNode;
			NodeList hostNodeList = datacenterElement.getElementsByTagName("host");
			for (int j = 0; j < hostNodeList.getLength(); j++) {
				
				vmList.add(hostCounter, new ArrayList<EdgeVM>());
				
				Node hostNode = hostNodeList.item(j);
				Element hostElement = (Element) hostNode;
				NodeList vmNodeList = hostElement.getElementsByTagName("VM");
				for (int k = 0; k < vmNodeList.getLength(); k++) {
					Node vmNode = vmNodeList.item(k);					
					Element vmElement = (Element) vmNode;

					String vmm = vmElement.getAttribute("vmm");
					
					int numOfCores = Integer.parseInt(vmElement.getElementsByTagName("core").item(0).getTextContent());
					double mips = Double.parseDouble(vmElement.getElementsByTagName("mips").item(0).getTextContent());
					int ram = Integer.parseInt(vmElement.getElementsByTagName("ram").item(0).getTextContent());
					long storage = Long.parseLong(vmElement.getElementsByTagName("storage").item(0).getTextContent());
					long bandwidth = SimSettings.getInstance().getWlanBandwidth() / (hostNodeList.getLength()+vmNodeList.getLength());
					
					
					//VM Parameters		
					EdgeVM vm = new EdgeVM(vmCounter, brockerId, mips, numOfCores, ram, bandwidth, storage, vmm, new CloudletSchedulerTimeShared());
					vm.setVmType(SimSettings.VM_TYPES.EDGE_VM);
					vmList.get(hostCounter).add(vm);
					vmCounter++;
				}

				hostCounter++;
			}
		}
	}
	
	public void terminateDatacenters(){
		for (Datacenter datacenter : localDatacenters) {
			datacenter.shutdownEntity();
		}
	}

	//average utilization of all VMs
	public double getAvgUtilization(){
		double totalUtilization = 0;
		double vmCounter = 0;
		
		// for each datacenter...
		for(int i= 0; i<localDatacenters.size(); i++) {
			List<? extends Host> list = localDatacenters.get(i).getHostList();
			// for each host...
			for (int j=0; j < list.size(); j++) {
				Host host = list.get(j);
				List<EdgeVM> vmArray = SimManager.getInstance().getLocalServerManager().getVmList(host.getId());
				//for each vm...
				for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
					totalUtilization += vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
					vmCounter++;
				}
			}
		}
		return totalUtilization / vmCounter;
	}

	private Datacenter createDatacenter(int index, Element datacenterElement) throws Exception{
		String arch = datacenterElement.getAttribute("arch");
		String os = datacenterElement.getAttribute("os");
		String vmm = datacenterElement.getAttribute("vmm");
		double costPerBw = Double.parseDouble(datacenterElement.getElementsByTagName("costPerBw").item(0).getTextContent());
		double costPerSec = Double.parseDouble(datacenterElement.getElementsByTagName("costPerSec").item(0).getTextContent());
		double costPerMem = Double.parseDouble(datacenterElement.getElementsByTagName("costPerMem").item(0).getTextContent());
		double costPerStorage = Double.parseDouble(datacenterElement.getElementsByTagName("costPerStorage").item(0).getTextContent());
		
		List<EdgeHost> hostList=createHosts(datacenterElement);
		
		String name = "Datacenter_" + Integer.toString(index);
		double time_zone = 3.0;         // time zone this resource located
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, costPerSec, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
	
		VmAllocationPolicy vm_policy = SimManager.getInstance().getScenarioFactory().getVmAllocationPolicy(hostList,index);
		datacenter = new Datacenter(name, characteristics, vm_policy, storageList, 0);
		
		return datacenter;
	}
	
	private List<EdgeHost> createHosts(Element datacenterElement){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more Machines
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		
		
		
		Element location = (Element)datacenterElement.getElementsByTagName("location").item(0);
		String attractiveness = location.getElementsByTagName("attractiveness").item(0).getTextContent();
		int wlan_id = Integer.parseInt(location.getElementsByTagName("wlan_id").item(0).getTextContent());
		int x_pos = Integer.parseInt(location.getElementsByTagName("x_pos").item(0).getTextContent());
		int y_pos = Integer.parseInt(location.getElementsByTagName("y_pos").item(0).getTextContent());
		int level =Integer.parseInt(location.getElementsByTagName("level").item(0).getTextContent());
		SimSettings.PLACE_TYPES placeType = SimUtils.stringToPlace(attractiveness);

		NodeList hostNodeList = datacenterElement.getElementsByTagName("host");
		for (int j = 0; j < hostNodeList.getLength(); j++) {
			Node hostNode = hostNodeList.item(j);
			
			Element hostElement = (Element) hostNode;
			int numOfCores = Integer.parseInt(hostElement.getElementsByTagName("core").item(0).getTextContent());
			double mips = Double.parseDouble(hostElement.getElementsByTagName("mips").item(0).getTextContent());
			int ram = Integer.parseInt(hostElement.getElementsByTagName("ram").item(0).getTextContent());
			long storage = Long.parseLong(hostElement.getElementsByTagName("storage").item(0).getTextContent());
			long bandwidth = SimSettings.getInstance().getWlanBandwidth() / hostNodeList.getLength();
			
			// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
			//    create a list to store these PEs before creating
			//    a Machine.
			List<Pe> peList = new ArrayList<Pe>();

			// 3. Create PEs and add these into the list.
			//for a quad-core machine, a list of 4 PEs is required:
			for(int i=0; i<numOfCores; i++){
				peList.add(new Pe(i, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
			}
			
			//Make NodeSim object with the input x/y positions and add that to the list of nodes
			NodeSim newNode = new NodeSim(x_pos, y_pos, level, wlan_id);
			nodesForTopography.add(newNode);
			
			
			//4. Create Hosts with its id and list of PEs and add them to the list of machines
			EdgeHost host = new EdgeHost(
					hostIdCounter,
					new RamProvisionerSimple(ram),
					new BwProvisionerSimple(bandwidth), //kbps
					storage,
					peList,
					new VmSchedulerSpaceShared(peList)
				);
			
			host.setPlace(new Location(placeType, wlan_id, x_pos, y_pos));
			host.setLevel(level);
			hostList.add(host);
			hostIdCounter++;
		}
		

		return hostList;
	}
}
