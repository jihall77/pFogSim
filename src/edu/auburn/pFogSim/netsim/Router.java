package edu.auburn.pFogSim.netsim;

import edu.auburn.pFogSim.netsim.NodeSim;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import javafx.util.Pair;
import java.util.List;

public class Router {
	LinkedList<NodeSim> travelQueue;
	
	
	public Router(LinkedList<NodeSim> in) {
		travelQueue = in;
	}
	public double getLatency() {
		double latency = 0.0;
		NodeSim current;
		NodeSim next;
		while (!travelQueue.isEmpty()) {
			current = travelQueue.pollFirst();
			next = travelQueue.peekFirst();
			if (next == null) {
				break;
			}
			if (current.traverse(next) >= 0) {
				latency += current.traverse(next);
			}
			else {
				throw new IllegalArgumentException();
			}
		}
		return latency;
	}
	
	private class Dijkstra {
		HashMap<Pair<NodeSim, Pair<Double, NodeSim>>, ArrayList<Pair<Double, NodeSim>>> verts;
		HashSet<Pair<NodeSim, Pair<Double, NodeSim>>> completed;
		NodeSim src;
		public Dijkstra() {
			verts = new HashMap<Pair<NodeSim, Pair<Double, NodeSim>>, ArrayList<Pair<Double,NodeSim>>>();
			completed = new HashSet<Pair<NodeSim, Pair<Double, NodeSim>>>();
		}
		
		public void initialize (List<NodeSim> nodes, NodeSim source) {
			ArrayList<Pair<Double, NodeSim>> edges;
			for (NodeSim node : nodes) {
				if (node.equals(source)) {
					src = node;
					continue;
				}
				edges = new ArrayList<Pair<Double, NodeSim>>();
				
				verts.put(new Pair<NodeSim, Pair<Double, NodeSim>>(node, new Pair<Double, NodeSim>(Double.MAX_VALUE, null)), edges);
			}
		}
		
		public void relax(Pair<NodeSim, Pair<Double, NodeSim>> u) {
			
		}
		
	}
}


