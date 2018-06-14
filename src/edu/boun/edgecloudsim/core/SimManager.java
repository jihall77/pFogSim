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

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeServerManager;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.edge_server.VmAllocationPolicy_Custom;
import edu.boun.edgecloudsim.edge_client.MobileDeviceManager;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.mobility.MobilityModel;
import edu.boun.edgecloudsim.task_generator.LoadGeneratorModel;
import edu.boun.edgecloudsim.network.NetworkModel;
import edu.boun.edgecloudsim.utils.EdgeTask;
import edu.boun.edgecloudsim.utils.SimLogger;

public class SimManager extends SimEntity {
	private static final int CREATE_TASK = 0;
	private static final int CHECK_ALL_VM = 1;
	private static final int GET_LOAD_LOG = 2;
	private static final int PRINT_PROGRESS = 3;
	private static final int STOP_SIMULATION = 4;
	
	private int[] wapIdList = new int [1000];
	private int numOfMobileDevice;
	private NetworkModel networkModel;
	private MobilityModel mobilityModel;
	private ScenarioFactory scenarioFactory;
	private EdgeOrchestrator edgeOrchestrator;
	private EdgeServerManager edgeServerManager;
	private LoadGeneratorModel loadGeneratorModel;
	private MobileDeviceManager mobileDeviceManager;
	
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
		SimLogger.printLine("" + mobilityModel.getSize());
		for(int i = 0; i < mobilityModel.getSize(); i++)
		{
			//SimLogger.printLine("" + i);
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
				double time = CloudSim.clock();
				for(int q = 0; q < mobilityModel.getSize(); q++)
				{
					if(wapIdList[q] != mobilityModel.getWlanId(q, time))
					{
						//SimLogger.printLine("transfer of task needed!");
						wapIdList[q] = mobilityModel.getWlanId(q, time);
						if (mobileDeviceManager.getCloudletList().size() > q) {
							Task task = (Task) mobileDeviceManager.getCloudletList().get(q);
							task.setSubmittedLocation(mobilityModel.getLocation(q, time));
							mobileDeviceManager.migrateTask(task);
							SimLogger.printLine("Task migrated...");
						}
						
					}
					//else
						//SimLogger.printLine("No transfer necessary!");
				}
				
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
