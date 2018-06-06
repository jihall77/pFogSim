package edu.auburn.pFogSim.netsim;

import edu.auburn.pFogSim.netsim.NodeSim;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import javafx.util.Pair;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Set;

public class Router {
	
	public static LinkedList<NodeSim> findPath(NetworkTopology network, NodeSim src, NodeSim dest ) {
		LinkedList<NodeSim> travelQueue;
		Dijkstra router = Router.getAPathFinder();
		router.runDijkstra((Set<NodeSim>) network.getNodes(), src);
		travelQueue = router.getPath(dest);
		return travelQueue;
		//return router.getLatency(dest);
	}
	
	public static Dijkstra getAPathFinder() {
		Router rout = new Router();
		return rout.getDijkstra();
	}
	
	public Dijkstra getDijkstra() {
		return new Dijkstra();
	}
	
	/*used for early testing only, we want the router to just provide the path not calculate latency
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
	*/
	
	/*used for early testing only, we want the router to just provide the path not calculate latency
	public double findRoute(NetworkTopology network, NodeSim src, NodeSim dest ) {
		Dijkstra router = new Dijkstra();
		router.runDijkstra((Set<NodeSim>) network.getNodes(), src);
		travelQueue = router.getPath(dest);
		return getLatency();
		//return router.getLatency(dest);
	}
	*/
	
	private class Dijkstra {
		HashMap<Pair<NodeSim, Pair<Double, NodeSim>>, ArrayList<Pair<Double, NodeSim>>> verts;
		HashMap<NodeSim, Pair<NodeSim, Pair<Double, NodeSim>>> getMap;
		HashMap<Pair<Integer, Integer>, NodeSim> index;
		PriorityQueue<Pair<NodeSim, Pair<Double, NodeSim>>> queue;
		HashSet<Pair<NodeSim, Pair<Double, NodeSim>>> completed;
		NodeSim src;
		public Dijkstra() {
			verts = new HashMap<Pair<NodeSim, Pair<Double, NodeSim>>, ArrayList<Pair<Double, NodeSim>>>();
			getMap = new HashMap<NodeSim, Pair<NodeSim, Pair<Double, NodeSim>>>();
			index = new HashMap<Pair<Integer, Integer>, NodeSim>();
			queue = new PriorityQueue<Pair<NodeSim, Pair<Double, NodeSim>>>(25, new dijkstrasComparator());
			completed = new HashSet<Pair<NodeSim, Pair<Double, NodeSim>>>();
		}
		
		private void initialize (Set<NodeSim> nodes, NodeSim source) {
			ArrayList<Pair<Double, NodeSim>> edges;
			for (NodeSim node : nodes) {
				index.put(node.getLocation(), node);
			}
			for (NodeSim node : nodes) {
				if (node.equals(source)) {
					src = node;
					edges = new ArrayList<Pair<Double, NodeSim>>();
					for (Link edge : node.getEdges()) {
						edges.add(new Pair<Double, NodeSim>(edge.getOutgoingLat(node.getLocation()), index.get(edge.getOutgoingLink(node.getLocation()))));
					}
					verts.put(new Pair<NodeSim, Pair<Double, NodeSim>>(node, new Pair<Double, NodeSim>(0.0, null)), edges);
					getMap.put(node, new Pair<NodeSim, Pair<Double, NodeSim>>(node, new Pair<Double, NodeSim>(0.0, null)));
					continue;
				}
				edges = new ArrayList<Pair<Double, NodeSim>>();
				for (Link edge : node.getEdges()) {
					edges.add(new Pair<Double, NodeSim>(edge.getOutgoingLat(node.getLocation()), index.get(edge.getOutgoingLink(node.getLocation()))));
				}
				verts.put(new Pair<NodeSim, Pair<Double, NodeSim>>(node, new Pair<Double, NodeSim>(Double.MAX_VALUE, null)), edges);
				getMap.put(node, new Pair<NodeSim, Pair<Double, NodeSim>>(node, new Pair<Double, NodeSim>(Double.MAX_VALUE, null)));
			}
		}
		
		private void relax(Pair<NodeSim, Pair<Double, NodeSim>> u, Pair<NodeSim, Pair<Double, NodeSim>> v, Double w) {
			Pair<NodeSim, Pair<Double, NodeSim>> temp;
			if (v == null) {
				return;
			}
			if (v.getValue().getKey() > (u.getValue().getKey() + w)) {
				temp = new Pair<NodeSim, Pair<Double, NodeSim>>(v.getKey(), new Pair<Double, NodeSim>(u.getValue().getKey() + w, u.getKey()));
				queue.add(temp);
				queue.remove(v);
				getMap.put(temp.getKey(), temp);
				getMap.remove(v);
				verts.put(temp, verts.get(v));
				verts.remove(v);
			}
		}
		
		public void runDijkstra(Set<NodeSim> nodes, NodeSim source) {
			initialize(nodes, source);
			Pair<NodeSim, Pair<Double, NodeSim>> u;
			ArrayList<Pair<NodeSim, Pair<Double, NodeSim>>> adj = new ArrayList<Pair<NodeSim, Pair<Double, NodeSim>>>();
			ArrayList<Double> w = new ArrayList<Double>();
			for (Pair<NodeSim, Pair<Double, NodeSim>> vertice : verts.keySet()) {
				queue.add(vertice);
			}
			while (!queue.isEmpty()) {
				u = queue.poll();
				completed.add(u);
				w = new ArrayList<Double>();
				adj = new ArrayList<Pair<NodeSim, Pair<Double, NodeSim>>>();
				for (Pair<Double, NodeSim> d : verts.get(u)) {
					w.add(d.getKey());
					adj.add(getMap.get(d.getValue()));
				}
				for (int i = 0; i < w.size(); i++) {
					relax(u, adj.get(i), w.get(i));
				}
			}
		}
		
		public double getLatency(NodeSim dest) {
			for (Pair<NodeSim, Pair<Double, NodeSim>> node : completed) {
				if (node.getKey().equals(dest)) {
					return node.getValue().getKey();
				}
			}
			return -1;
		}
		
		public LinkedList<NodeSim> getPath(NodeSim dest) {
			LinkedList<NodeSim> reversed = new LinkedList<NodeSim>();
			LinkedList<NodeSim> result = new LinkedList<NodeSim>();
			Pair<NodeSim, Pair<Double, NodeSim>> temp = null;
			NodeSim current;
			reversed.add(dest);
			current = dest;
			while (!current.equals(src)) {
				for (Pair<NodeSim, Pair<Double, NodeSim>> node : completed) {
					if (node.getKey() == current) {
						current = node.getValue().getValue();
						reversed.add(current);
						temp = node;
						break;
					}
				}
				completed.remove(temp);
			}
			
			while(!reversed.isEmpty()) {
				result.addFirst(reversed.poll());
			}
			return result;
		}
		
	}
	
	private class dijkstrasComparator implements Comparator<Pair<NodeSim, Pair<Double, NodeSim>>> {
		public int compare(Pair<NodeSim, Pair<Double, NodeSim>> x, Pair<NodeSim, Pair<Double, NodeSim>> y) {
			return (int)((x.getValue().getKey() - y.getValue().getKey()) * 1000);
		}
		
		
	}
}


