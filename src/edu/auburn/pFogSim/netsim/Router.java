package edu.auburn.pFogSim.netsim;

import edu.auburn.pFogSim.netsim.Node;
import java.util.LinkedList;

public class Router {
	LinkedList<Node> travelQueue;
	
	
	public Router(LinkedList<Node> in) {
		travelQueue = in;
	}
	public double getLatency() {
		double latency = 0.0;
		Node current;
		Node next;
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
}
