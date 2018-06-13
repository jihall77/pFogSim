package edu.auburn.pFogSim.orchestrator;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.edge_client.CpuUtilizationModel_Custom;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.auburn.pFogSim.netsim.NetworkTopology;
import edu.boun.edgecloudsim.network.MM1Queue;
import edu.boun.edgecloudsim.utils.Location;
import javafx.util.Pair;
import edu.auburn.pFogSim.Puddle.Puddle;
import edu.auburn.pFogSim.Radix.DistRadix;

import java.util.ArrayList;
import java.util.LinkedList;

import org.cloudbus.cloudsim.core.CloudSim;
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
		return getHost(task).getId();
	}

	@Override
	public EdgeVM getVmToOffload(Task task) {
		return ((EdgeVM) getHost(task).getVmList().get(0));
	}
	
	private Puddle getNearest0Pud(Task task) {
		NetworkTopology network = ((MM1Queue) SimManager.getInstance().getNetworkModel()).getNetworkTopology();
		Puddle puddle = null;
		EdgeHost host;
		Location loc = task.getSubmittedLocation();
		double distance = Double.MAX_VALUE;
		double newDist;
		ArrayList<Puddle> pud0s = new ArrayList<Puddle>();
		for (Puddle pud : network.getPuddles()) {
			if (pud.getLevel() == 0) {
				pud0s.add(pud);
			}
		}
		for (Puddle pud : pud0s) {
			host = pud.getClosestNodes(loc).getFirst();
			newDist = Math.sqrt((Math.pow(loc.getXPos() - host.getLocation().getXPos(), 2) + Math.pow(loc.getYPos() - host.getLocation().getYPos(), 2)));
			if(newDist < distance) {
				distance = newDist;
				puddle = pud;
			}
		}
		return puddle;
	}
	
	private boolean goodHost(EdgeHost host, Task task) {
		double hostCap = host.getVmList().get(0).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
		double taskCap = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(((EdgeVM)host.getVmList().get(0)).getVmType());
		return hostCap >= taskCap;
	}
	
	private EdgeHost getHost(Task task) {
		Puddle puddle = getNearest0Pud(task);
		ArrayList<Puddle> puds = new ArrayList<Puddle>();
		ArrayList<EdgeHost> hosts = new ArrayList<EdgeHost>();
		LinkedList<EdgeHost> candidates;
		EdgeHost host;
		DistRadix radix;
		while(!puddle.canHandle(task)) {
			puddle = puddle.getParent();
			if (puddle == null) {
				throw new IllegalArgumentException();
			}
		}
		while(puddle != null) {
			puds.add(puddle);
			puddle = puddle.getParent();
		}
		for (Puddle pud : puds) {
			hosts.addAll(pud.getMembers());
		}
		radix = new DistRadix(hosts, new Pair<Integer, Integer>(task.getSubmittedLocation().getXPos(), task.getSubmittedLocation().getYPos()));
		candidates = radix.sortPuddleNodes();
		host = candidates.poll();
		while(!goodHost(host, task)) {
			host = candidates.poll();
		}
		return host;
	}

}
