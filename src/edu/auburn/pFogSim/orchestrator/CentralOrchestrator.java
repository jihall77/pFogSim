package edu.auburn.pFogSim.orchestrator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;

import edu.auburn.pFogSim.Radix.DistRadix;
import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeVM;

public class CentralOrchestrator extends EdgeOrchestrator {

	ArrayList<EdgeHost> hosts;
	
	public CentralOrchestrator(String _policy, String _simScenario) {
		super(_policy, _simScenario);
	}
	
	@Override
	public void initialize() {
		hosts = new ArrayList<EdgeHost>();
		for (Datacenter node : SimManager.getInstance().getLocalServerManager().getDatacenterList()) {
			hosts.add(((EdgeHost) node.getHostList().get(0)));
		}

	}

	@Override
	public int getDeviceToOffload(Task task) {
		return getHost(task).getId();
	}

	@Override
	public EdgeVM getVmToOffload(Task task) {
		return ((EdgeVM) getHost(task).getVmList().get(0));
	}
	
	private EdgeHost getHost(Task task) {
		DistRadix sort = new DistRadix(hosts, task.getSubmittedLocation());
		LinkedList<EdgeHost> nodes = sort.sortNodes();
		EdgeHost host = nodes.poll();
		while(!goodHost(host, task)) {
			host = nodes.poll();//find the closest node capable of handling the task
		}
		return host;
	}

}