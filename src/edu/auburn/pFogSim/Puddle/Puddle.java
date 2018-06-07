
package edu.auburn.pFogSim.Puddle;

import edu.auburn.pFogSim.Exceptions.*;
import java.util.ArrayList;
import java.util.List;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
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
	private EdgeHost up;
	private ArrayList<EdgeHost> down;
	
	public Puddle() {
		
	}
	/**
	 * get the puddle head of the parent puddle
	 * @return
	 */
	public EdgeHost getParent() {
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
	public ArrayList<EdgeHost> getChildren() {
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
		
		for (int i = 0; newHead.equals(head) && i < members.size(); i++) {
			newHead = members.get(i);
		}
		
		if (newHead.equals(head)) {
			throw new EmptyPuddleException();
		}
		members.add(head);
		head = newHead;
		members.remove(head);
	}
	/**
	 * set the puddle head, the head should be a current member of the puddle<br>
	 * after being made the head, the host will be removed from the member list<br>
	 * if the input host is not a member of the puddle throw an IllegalArgumentException
	 * @param _head
	 */
	public void setHead(EdgeHost _head) {
		if (members.contains(_head)) {
			head = _head;
			members.remove(head);
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
			if (child.getLevel() == head.getLevel()) {
				down.add(child);
			}
			else {
				throw new BadPuddleParentageException();
			}
		}
	}
	/**
	 * set the children of the puddle, all children must be exactly on layer outwards 
	 * @param _down
	 */
	public void setDown(List<EdgeHost> _down) {
		down = new ArrayList<EdgeHost>();
		for (EdgeHost child : _down) {
			if (child.getLevel() == head.getLevel() + 1) {
				down.add(child);
			}
			else {
				throw new BadPuddleParentageException();
			}
		}
	}
	/**
	 * set the parent of the puddle. parent must be exactly one layer inwards
	 * @param _up
	 */
	public void setUp(EdgeHost _up) {
		up = _up;
		if (up.getLevel() != head.getLevel() - 1) {
			throw new BadPuddleParentageException();
		}
	}
}