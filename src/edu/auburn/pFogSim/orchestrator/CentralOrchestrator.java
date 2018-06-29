/**
 * Centralized Orchestrator for comparison against Puddle algorithm
 * @author jih0007@auburn.edu
 */
package edu.auburn.pFogSim.orchestrator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Vm;

import edu.auburn.pFogSim.Radix.DistRadix;
import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.utils.Location;

public class CentralOrchestrator extends EdgeOrchestrator {

	ArrayList<EdgeHost> hosts;
	
	public CentralOrchestrator(String _policy, String _simScenario) {
		super(_policy, _simScenario);
	}
	/**
	 * get all the hosts in the network into one list
	 */
	@Override
	public void initialize() {
		hosts = new ArrayList<EdgeHost>();
		for (Datacenter node : SimManager.getInstance().getLocalServerManager().getDatacenterList()) {
			hosts.add(((EdgeHost) node.getHostList().get(0)));
		}

	}
	/**
	 * get the id of the appropriate host
	 */
	@Override
	public int getDeviceToOffload(Task task) {
		return getHost(task).getId();
	}
	/**
	 * the the appropriate VM to run on
	 */
	@Override
	public EdgeVM getVmToOffload(Task task) {
		return ((EdgeVM) getHost(task).getVmList().get(0));
	}
	/**
	 * find the host
	 * @param task
	 * @return
	 */
	private EdgeHost getHost(Task task) {
		DistRadix sort = new DistRadix(hosts, task.getSubmittedLocation());//use radix sort based on distance from task
		LinkedList<EdgeHost> nodes = sort.sortNodes();
		EdgeHost host = nodes.poll();
		try {
			while(!goodHost(host, task)) {
				host = nodes.poll();//find the closest node capable of handling the task
			}
		} catch (NullPointerException e)
		{
			//If there are no nodes in the list that can take the task, send to the cloud
			host = SimManager.getInstance().getLocalServerManager().findHostById(0);
			
		}
		return host;
	}

}
