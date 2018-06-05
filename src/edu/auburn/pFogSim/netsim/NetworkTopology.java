/**
 * NetworkTopology Class for encapsulating a set of nodes and edges in a network
 * @author jih0007
 */
package edu.auburn.pFogSim.netsim;

import edu.auburn.pFogSim.netsim.Link;
import edu.auburn.pFogSim.netsim.NodeSim;
import java.util.HashSet;
import java.util.List;
import javafx.util.Pair;

public class NetworkTopology {
	private HashSet<Link> links;
	private HashSet<NodeSim> nodes;
	private HashSet<Pair<Integer, Integer>> coords;
	/**
	 * Constructor
	 * @param inNodes
	 * @param inLinks
	 */
	public NetworkTopology(List<NodeSim> inNodes, List<Link> inLinks) {
		links = new HashSet<Link>();
		nodes = new HashSet<NodeSim>();
		coords = new HashSet<Pair<Integer, Integer>>();
		for (NodeSim node : inNodes) {
			addNode(node);
		}
		for (Link link : inLinks) {
			addLink(link);
		}
	}
	/**
	 * add a node<br>
	 * if the node is null throw an IllegalArgumentException
	 * @param in
	 */
	public void addNode(NodeSim in) {
		if (in == null) {
			throw new IllegalArgumentException();
		}
		nodes.add(in);
		coords.add(in.getLocation());
	}
	/**
	 * add a link<br>
	 * link must be associated to 2 nodes to be added (always add nodes first!)<br>
	 * if the link is null throw an IllegalArgumentException
	 * @param in
	 * @return
	 */
	public boolean addLink(Link in) {
		if (in == null) {
			throw new IllegalArgumentException();
		}
		int counter = 0;
		for (NodeSim node : nodes) {
			if (in.validateCoords() && node.validateLink(in)) {
				node.addLink(in);
				counter++;
			}
		}
		if (counter == 2) {
			return links.add(in);
		}
		else {
			return false;
		}
	}
	/**
	 * run after all nodes and links have been added<br>
	 * all nodes must have at least one link<br>
	 * all links must be associated with 2 nodes
	 * @return
	 */
	public boolean validateTopology() {
		if (nodes == null || links == null) {
			return false;
		}
		try {
			for (NodeSim node : nodes) {
				if (node.getEdges().size() == 0) {
					return false;
				}
			}
			for (Link link : links) {
				if (!coords.contains(link.getRightLink()) || !coords.contains(link.getLeftLink()) 
						|| !link.validateCoords() || !link.validateLat()) {
					return false;
				}
			}
			return true;
		}
		catch (NullPointerException e) {
			return false;
		}
	}
	/**
	 * get the list of nodes
	 * @return
	 */
	public HashSet<NodeSim> getNodes() {
		return nodes;
	}
	/**
	 * get the list of links
	 * @return
	 */
	public HashSet<Link> getLinks() {
		return links;
	}
	/**
	 * cleans any bad links out of the topology
	 * @return
	 */
	public boolean cleanNodes() {
		try {
			for (NodeSim node : nodes) {
				while (node.removeLink(node.validateNode()));
			}
			
			return validateTopology();
		}
		catch (Exception e) {
			return false;
		}
	}
}
