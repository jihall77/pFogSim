/*
 * Title:        EdgeCloudSim - Sample Scenario Factory
 * 
 * Description:  Sample factory providing the default
 *               instances of required abstract classes 
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.sample_application;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import edu.auburn.pFogSim.mobility.VectorMobility;
import edu.auburn.pFogSim.mobility.MobilityModel;
import edu.auburn.pFogSim.netsim.ESBModel;
import edu.auburn.pFogSim.orchestrator.CentralOrchestrator;
import edu.auburn.pFogSim.orchestrator.CloudOnlyOrchestrator;
import edu.auburn.pFogSim.orchestrator.EdgeOnlyOrchestrator;
import edu.auburn.pFogSim.orchestrator.PuddleOrchestrator;
import edu.boun.edgecloudsim.core.ScenarioFactory;
import edu.boun.edgecloudsim.core.SimSettings.APP_TYPES;
import edu.boun.edgecloudsim.edge_client.CpuUtilizationModel_Custom;
//import edu.boun.edgecloudsim.edge_orchestrator.BasicEdgeOrchestrator;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.VmAllocationPolicy_Custom;
import edu.boun.edgecloudsim.network.NetworkModel;
import edu.boun.edgecloudsim.task_generator.IdleActiveLoadGenerator;
import edu.boun.edgecloudsim.task_generator.LoadGeneratorModel;

public class SampleScenarioFactory implements ScenarioFactory {
	private int numOfMobileDevice;
	private double simulationTime;
	private String orchestratorPolicy;
	private String simScenario;
	
	SampleScenarioFactory(int _numOfMobileDevice,
			double _simulationTime,
			String _orchestratorPolicy,
			String _simScenario){
		orchestratorPolicy = _orchestratorPolicy;
		numOfMobileDevice = _numOfMobileDevice;
		simulationTime = _simulationTime;
		simScenario = _simScenario;
	}
	
	@Override
	public LoadGeneratorModel getLoadGeneratorModel() {
		return new IdleActiveLoadGenerator(numOfMobileDevice, simulationTime, simScenario);
	}

	@Override
	public EdgeOrchestrator getEdgeOrchestrator() {
		if (simScenario.equals("PUDDLE_ORCHESTRATOR")) { 
			return new PuddleOrchestrator(orchestratorPolicy, simScenario);
		}
		else if (simScenario.equals("CENTRALIZED_ORCHESTRATOR")) {
			return new CentralOrchestrator(orchestratorPolicy, simScenario);
		}
		else if (simScenario.equals("CLOUD_ONLY")) {
			return new CloudOnlyOrchestrator(orchestratorPolicy, simScenario);
		}
		else if (simScenario.equals("EDGE_ONLY")) {
			return new EdgeOnlyOrchestrator(orchestratorPolicy, simScenario);
		}
		return null;
	}

	@Override
	public MobilityModel getMobilityModel() {
		return new VectorMobility(numOfMobileDevice,simulationTime);
	}

	@Override
	public NetworkModel getNetworkModel() {
		return new ESBModel(numOfMobileDevice);
	}

	@Override
	public VmAllocationPolicy getVmAllocationPolicy(List<? extends Host> hostList, int dataCenterIndex) {
		return new VmAllocationPolicy_Custom(hostList,dataCenterIndex);
	}

	@Override
	public UtilizationModel getCpuUtilizationModel(APP_TYPES _taskType) {
		return new CpuUtilizationModel_Custom(_taskType);
	}
}
