/**
 * alternate orchestrator for comparison against the puddle orchestrator
 * 
 * this orchestrator only assigns tasks to the cloud
 */

package edu.auburn.pFogSim.orchestrator;

import org.cloudbus.cloudsim.Datacenter;

import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeVM;

public class CloudOnlyOrchestrator extends EdgeOrchestrator {

	
	public CloudOnlyOrchestrator(String _policy, String _simScenario) {
		super(_policy, _simScenario);
	}
	
	@Override
	public void initialize() {

	}

	@Override
	public int getDeviceToOffload(Task task) {
		return cloud.getHostList().get(0).getId();
	}

	@Override
	public EdgeVM getVmToOffload(Task task) {
		return ((EdgeVM) cloud.getHostList().get(0).getVmList().get(0));
	}
	
}
