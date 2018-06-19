/*
 * Title:        EdgeCloudSim - Simulation Manager
 * 
 * Description: 
 * SimManager is an singleton class providing many abstract classeses such as
 * Network Model, Mobility Model, Edge Orchestrator to other modules
 * Critical simulation related information would be gathered via this class 
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeServerManager;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.edge_server.VmAllocationPolicy_Custom;
import edu.auburn.pFogSim.Puddle.Puddle;
import edu.auburn.pFogSim.clustering.FogCluster;
import edu.auburn.pFogSim.clustering.FogHierCluster;
import edu.auburn.pFogSim.netsim.Link;
import edu.auburn.pFogSim.netsim.NetworkTopology;
import edu.auburn.pFogSim.netsim.NodeSim;
import edu.auburn.pFogSim.netsim.Router;
import edu.boun.edgecloudsim.edge_client.MobileDeviceManager;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.mobility.MobilityModel;
import edu.boun.edgecloudsim.task_generator.LoadGeneratorModel;
import edu.boun.edgecloudsim.network.MM1Queue;
import edu.boun.edgecloudsim.network.NetworkModel;
import edu.boun.edgecloudsim.utils.EdgeTask;
import edu.boun.edgecloudsim.utils.SimLogger;
import javafx.util.Pair;

public class SimManager extends SimEntity {
	private static final int CREATE_TASK = 0;
	private static final int CHECK_ALL_VM = 1;
	private static final int GET_LOAD_LOG = 2;
	private static final int PRINT_PROGRESS = 3;
	private static final int STOP_SIMULATION = 4;
	
	//List of ids for wireless access points devices are connected to, max devices rn is 1000
	private int[] wapIdList = new int [2100];
	private int numOfMobileDevice;
	private NetworkModel networkModel;
	private MobilityModel mobilityModel;
	private ScenarioFactory scenarioFactory;
	private EdgeOrchestrator edgeOrchestrator;
	private EdgeServerManager edgeServerManager;
	private LoadGeneratorModel loadGeneratorModel;
	private MobileDeviceManager mobileDeviceManager;
	private NetworkTopology networkTopology;
	
	private static SimManager instance = null;
	
	public SimManager(ScenarioFactory _scenarioFactory, int _numOfMobileDevice, String _simScenario) throws Exception {
		super("SimManager");
		scenarioFactory = _scenarioFactory;
		numOfMobileDevice = _numOfMobileDevice;

		SimLogger.print("Creating tasks...");
		loadGeneratorModel = scenarioFactory.getLoadGeneratorModel();
		loadGeneratorModel.initializeModel();
		SimLogger.printLine("Done, ");
		
		SimLogger.print("Creating device locations...");
		mobilityModel = scenarioFactory.getMobilityModel();
		mobilityModel.initialize();
		SimLogger.printLine("Done.");

		//Generate network model
		networkModel = scenarioFactory.getNetworkModel();
		networkModel.initialize();
		
		//Generate edge orchestrator
		edgeOrchestrator = scenarioFactory.getEdgeOrchestrator();
		edgeOrchestrator.initialize();
		
		//Create Physical Servers
		edgeServerManager = new EdgeServerManager();

		//Create Client Manager
		mobileDeviceManager = new MobileDeviceManager();
		
		
		instance = this;
	}
	
	public static SimManager getInstance(){
		return instance;
	}
	
	/**
	 * Triggering CloudSim to start simulation
	 */
	public void startSimulation() throws Exception{
		//Starts the simulation
		SimLogger.print(super.getName()+" is starting...");
		
		//Start Edge Servers & Generate VMs
		edgeServerManager.startDatacenters();
		edgeServerManager.createVmList(mobileDeviceManager.getId());

		CloudSim.startSimulation();
	}
	
	public ScenarioFactory getScenarioFactory(){
		return scenarioFactory;
	}
	
	public int getNumOfMobileDevice(){
		return numOfMobileDevice;
	}
	
	public NetworkModel getNetworkModel(){
		return networkModel;
	}

	public MobilityModel getMobilityModel(){
		return mobilityModel;
	}
	
	public EdgeOrchestrator getEdgeOrchestrator(){
		return edgeOrchestrator;
	}
	
	public EdgeServerManager getLocalServerManager(){
		return edgeServerManager;
	}

	public MobileDeviceManager getMobileDeviceManager(){
		return mobileDeviceManager;
	}
	
	@Override
	public void startEntity() {
		
		for(int i=0; i<edgeServerManager.getDatacenterList().size(); i++)
			mobileDeviceManager.submitVmList(edgeServerManager.getVmList(i));
		
		//Creation of tasks are scheduled here!
		for(int i=0; i< loadGeneratorModel.getTaskList().size(); i++)
		{
			schedule(getId(), loadGeneratorModel.getTaskList().get(i).startTime, CREATE_TASK, loadGeneratorModel.getTaskList().get(i));
		}
		
		//Get all of the initial wireless access points ids for all the mobile devices
		for(int i = 0; i < mobilityModel.getSize(); i++)
		{
			wapIdList[i] = mobilityModel.getWlanId(i);
		}
		
		//Periodic event loops starts from here!
		schedule(getId(), 5, CHECK_ALL_VM);
		schedule(getId(), SimSettings.getInstance().getSimulationTime()/100, PRINT_PROGRESS);
		schedule(getId(), SimSettings.getInstance().getVmLoadLogInterval(), GET_LOAD_LOG);
		schedule(getId(), SimSettings.getInstance().getSimulationTime(), STOP_SIMULATION);
		
		SimLogger.printLine("Done.");
	}

	@Override
	public void processEvent(SimEvent ev) {
		synchronized(this){
			switch (ev.getTag()) {
			case CREATE_TASK:
				//SimLogger.printLine("CREATE_TASK reached");
				try {
					EdgeTask edgeTask = (EdgeTask) ev.getData();
					mobileDeviceManager.submitTask(edgeTask);						
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
				break;
			case CHECK_ALL_VM:
				//SimLogger.printLine("CHECK_ALL_VM reached");
				int totalNumOfVm = SimSettings.getInstance().getNumOfEdgeVMs();
				if(VmAllocationPolicy_Custom.getCreatedVmNum() != totalNumOfVm){
					SimLogger.printLine("All VMs cannot be created! Terminating simulation...");
					System.exit(0);
				}
				//else SimLogger.printLine("All VMs could be created!");
				break;
			case GET_LOAD_LOG:
				SimLogger.getInstance().addVmUtilizationLog(CloudSim.clock(),edgeServerManager.getAvgUtilization());
				schedule(getId(), SimSettings.getInstance().getVmLoadLogInterval(), GET_LOAD_LOG);
				break;
			case PRINT_PROGRESS:
				//Updates the positions of FOG Devices if necessary
				HashSet<Link> links = ((MM1Queue)SimManager.getInstance().getNetworkModel()).getNetworkTopology().getLinks();
				HashSet<NodeSim> nodes = ((MM1Queue)SimManager.getInstance().getNetworkModel()).getNetworkTopology().getNodes();
				
				ArrayList<Link> newLinks = new ArrayList<Link>();
				ArrayList<NodeSim> newNodes = new ArrayList<NodeSim>();
				for(NodeSim node : nodes)
				{
					if(node.isMoving())
					{
					//Update positions
						Pair<Integer, Integer> currentLoc = node.getLocation();
						//Change links
						for(Link link : links)
						{
							if(link.getLeftLink() == currentLoc)
							{
								//Sets that location to what it will be in a bit
								link.setLeftLink(new Pair<Integer, Integer>(currentLoc.getKey() + node.getVector().getKey(), currentLoc.getValue() + node.getVector().getValue()));
							}
							else if(link.getRightLink() == currentLoc)
							{
								//Sets that location to what it will be in a bit
								link.setRightLink(new Pair<Integer, Integer>(currentLoc.getKey() + node.getVector().getKey(), currentLoc.getValue() + node.getVector().getValue()));

							}
							
						}
						//Change nodes
						node.setLocation(new Pair<Integer, Integer>(node.getLocation().getKey() + currentLoc.getKey(), node.getLocation().getValue() + currentLoc.getValue()));
					}
					newNodes.add(node);
				}
				for(Link link : links)
				{
					newLinks.add(link);
				}
				//Rerun clustering and puddles
				FogHierCluster clusterObject = new FogHierCluster(newNodes);
				networkTopology = new NetworkTopology(newNodes, newLinks);
				if(!networkTopology.cleanNodes())
				{
					SimLogger.printLine("Topology is not valid");
					System.exit(0);
				}
				//Sets network topology and uses it to make the Puddle Objects
				((MM1Queue) SimManager.getInstance().getNetworkModel()).setNetworkTopology(networkTopology);
				networkTopology.setPuddles(EdgeServerManager.getInstance().makePuddles(clusterObject));
				
				//Goes through all devices and checks to see if WAP ids have changed
				//	Currently checks devices every 12 seconds in simulation (which runs for 20mins {Duration: 0.333.. hrs})
				double time = CloudSim.clock();
				for(int q = 0; q < mobilityModel.getSize(); q++)
				{
					//If the id has changed, update the value in our list and move the cloudlet to a more appropriate VM
					if(wapIdList[q] != mobilityModel.getWlanId(q, time))
					{
						wapIdList[q] = mobilityModel.getWlanId(q, time);
						if (mobileDeviceManager.getCloudletList().size() > q) {
							Task task = (Task) mobileDeviceManager.getCloudletList().get(q);
							task.setSubmittedLocation(mobilityModel.getLocation(q, time));
							//SimLogger.printLine("MIGRATION!!!!!!!!!!!!!!!!!!");
							mobileDeviceManager.migrateTask(task);
						}
					}
				}
				//Prints progress
				int progress = (int)((CloudSim.clock()*100)/SimSettings.getInstance().getSimulationTime());
				if(progress % 10 == 0)
					SimLogger.print(Integer.toString(progress));
				else
					SimLogger.print(".");
				if(CloudSim.clock() < SimSettings.getInstance().getSimulationTime())
					schedule(getId(), SimSettings.getInstance().getSimulationTime()/100, PRINT_PROGRESS);
				break;
			case STOP_SIMULATION:
				SimLogger.printLine("100");
				CloudSim.terminateSimulation();
				try {
					SimLogger.getInstance().simStopped();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
				break;
			default:
				Log.printLine(getName() + ": unknown event type");
				break;
			}
		}
	}
	
	@Override
	public void shutdownEntity() {
		edgeServerManager.terminateDatacenters();
	}
}