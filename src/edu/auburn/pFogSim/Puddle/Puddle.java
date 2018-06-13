
package edu.auburn.pFogSim.Puddle;

import edu.auburn.pFogSim.Exceptions.*;
import java.util.ArrayList;
import java.util.List;

import edu.boun.edgecloudsim.edge_client.CpuUtilizationModel_Custom;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.utils.Location;
import javafx.util.Pair;
import edu.auburn.pFogSim.Radix.DistRadix;
import java.util.LinkedList;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
/**
 * @author Jacob I Hall and Clayton Johnson
 * puddle class for separating nodes into logical hierarchies
 */
public class Puddle {
	
	//head = some head
	//list = list of nodes on level
	//node up = next puddle head location
	//list of nodes downward puddle heads
	
	private EdgeHost head;//head itself is not contained in the members list of a puddle
	private ArrayList<EdgeHost> members;
	private Puddle up;
	private ArrayList<Puddle> down;
	private int availRam;
	private double availMIPS;
	private int availPES;
	private int maxRam;
	private double maxMIPS;
	private int maxPES;
	private double totalCapacity;
	private double maxCapacity;
	private int level;

	
	public Puddle() {
		members = new ArrayList<EdgeHost>();
		down = new ArrayList<Puddle>();
	}
	/**
	 * get the puddle head of the parent puddle
	 * @return
	 */
	public Puddle getParent() {
		return up;
	}
	/**
	 * get the head of this puddle
	 * @return
	 */
	public EdgeHost getHead() {
		return head;
	}
	/**
	 * get the heads of the children puddles
	 * @return
	 */
	public ArrayList<Puddle> getChildren() {
		return down;
	}
	/**
	 * get the members of this puddle
	 * @return
	 */
	public ArrayList<EdgeHost> getMembers() {
		return members;
	}
	/**
	 * choose next available puddle member as the new puddle head<br>
	 */
	public void chooseNewHead() {
		EdgeHost newHead = head;
		if (head == null) {
			head = members.get(0);
			//members.remove(head);
			return;
		}
		for (int i = 0; newHead.equals(head) && i < members.size(); i++) {
			newHead = members.get(i);
		}
		
		if (newHead.equals(head)) {
			throw new EmptyPuddleException();
		}
		members.add(head);
		head = newHead;
		//members.remove(head);
	}
	/**
	 * set the puddle head, the head should be a current member of the puddle<br>
	 * if the input host is not a member of the puddle throw an IllegalArgumentException
	 * @param _head
	 */
	public void setHead(EdgeHost _head) {
		if (members.contains(_head)) {
			head = _head;
			//members.remove(head);
		}
		else {
			throw new IllegalArgumentException();
		}
		
	}
	/**
	 * set the puddle members, all puddle members must be of the same layer as this puddle
	 * @param _members
	 */
	public void setMembers(List<EdgeHost> _members) {
		members = new ArrayList<EdgeHost>();
		for (EdgeHost child : _members) {
			if (child.getLevel() == getLevel()) {
				members.add(child);
			}
			else {
				//throw new BadPuddleParentageException("Child: " + child.getLevel() + ", member: " + getLevel());
			}
		}
	}
	/**
	 * set the children of the puddle, all children must be exactly on layer outwards 
	 * @param _down
	 */
	public void setDown(List<Puddle> _down) {
		down = new ArrayList<Puddle>();
		for (Puddle child : _down) {
			if (child.getLevel() == getLevel() - 1) {
				down.add(child);
			}
			else {
				throw new BadPuddleParentageException("Child: " + child.getLevel() + ", Parent: " + getLevel());
			}
		}
	}
	
	public void addDown(Puddle puddle) {
		down.add(puddle);
	}
	/**
	 * set the parent of the puddle. parent must be exactly one layer inwards
	 * @param _up
	 */
	public void setUp(Puddle _up) {
		if (_up.getLevel() != getLevel() + 1) {
			throw new BadPuddleParentageException("Child: " + getLevel() + ", Parent: " + _up.getLevel());
		}
		up = _up;
		up.addDown(this);
	}
	/**
	 * update the total number of available resources in this puddle<br>
	 * as well as the max resources available on a single instance
	 */
	public void updateResources() {
		availRam = 0;
		availMIPS = 0;
		availPES = 0;
		maxRam = 0;
		maxMIPS = 0;
		maxPES = 0;
		for (EdgeHost node : members) {
			availRam += node.getRam();
			availMIPS += node.getAvailableMips();
			availPES += node.getNumberOfFreePes();
			if (node.getRam() > maxRam) {
				maxRam = node.getRam();
			}
			if (node.getAvailableMips() > maxMIPS) {
				maxMIPS = node.getAvailableMips();
			}
			if (node.getNumberOfFreePes() > maxPES) {
				maxPES = node.getNumberOfFreePes();
			}
		}
	}
	/**
	 * return whether this puddle can handle the input task
	 * @param app
	 * @return
	 */
	public boolean canHandle(Task app) {
		updateCapacity();
		double appCapacity = ((CpuUtilizationModel_Custom)app.getUtilizationModelCpu()).predictUtilization(((EdgeVM)head.getVmList().get(0)).getVmType());
		if (appCapacity > totalCapacity || appCapacity > maxCapacity) {
			return false;
		}
		return true;
	}
	
	public void updateCapacity() {
		double tempCap = 0;
		totalCapacity = 0;
		maxCapacity = 0;
		for (EdgeHost node : members) {
			tempCap = 100.0 - node.getVmList().get(0).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
			totalCapacity += tempCap;
			if (maxCapacity < tempCap) {
				maxCapacity = tempCap;
			}
		}
	}
	/**
	 * get the layer that this puddle belongs to
	 * @return
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * get the list nodes sorted by distance from the reference point
	 * @param ref
	 * @return
	 */
	public LinkedList<EdgeHost> getClosestNodes(Pair<Integer, Integer> ref) {
		DistRadix rad = new DistRadix(members, ref);
		LinkedList<EdgeHost> nodes = rad.sortPuddleNodes();
		return nodes;
	}
	/**
	 * get the list nodes sorted by distance from the reference point
	 * @param ref
	 * @return
	 */
	public LinkedList<EdgeHost> getClosestNodes(Location ref) {
		return getClosestNodes(new Pair<Integer, Integer>(ref.getXPos(), ref.getYPos()));
	}
	
	public void setLevel(int lvl) {
		level = lvl;
	}
}