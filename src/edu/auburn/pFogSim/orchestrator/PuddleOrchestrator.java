package edu.auburn.pFogSim.orchestrator;

import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
/**
 * implementation of Edge Orchestrator for using puddles
 * @author Jacob I Hall jih0007@auburn.edu
 *
 */
public class PuddleOrchestrator extends EdgeOrchestrator {

	
	public PuddleOrchestrator(String _policy, String _simScenario) {
		super(_policy, _simScenario);
	}
	@Override
	public void initialize() {
		
	}

	/**
	 * Get the id of host to send the device to.<br>
	 * Find the appropriate puddle, then ask the puddle for a host.
	 */
	@Override
	public int getDeviceToOffload(Task task) {
		
		return 0;
	}

	@Override
	public EdgeVM getVmToOffload(Task task) {
		
		return null;
	}

}
