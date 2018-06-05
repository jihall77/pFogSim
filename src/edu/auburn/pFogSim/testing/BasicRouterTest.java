package edu.auburn.pFogSim.testing;

import javafx.util.Pair;
import edu.auburn.pFogSim.netsim.*;
import java.util.*;

public class BasicRouterTest {
	public static void main(String[] args) {
		Pair<Integer, Integer> c1 = new Pair<Integer, Integer>(1,1);
		Pair<Integer, Integer> c2 = new Pair<Integer, Integer>(2,2);
		Pair<Integer, Integer> c3 = new Pair<Integer, Integer>(3,3);
		Pair<Integer, Integer> c4 = new Pair<Integer, Integer>(4,4);
		Pair<Integer, Integer> c5 = new Pair<Integer, Integer>(5,5);
		
		Node n1 = new Node();
		Node n2 = new Node();
		Node n3 = new Node();
		Node n4 = new Node();
		Node n5 = new Node();
		
		LinkedList<Node> path = new LinkedList<Node>();
		LinkedList<Link> edges = new LinkedList<Link>();
		
		n1.setLocation(c1);
		n2.setLocation(c2);
		n3.setLocation(c3);
		n4.setLocation(c4);
		n5.setLocation(c5);
		
		Link l12 = new Link(c1, c2, 0.5, 0.5);
		Link l23 = new Link(c2, c3, 0.5, 0.5);
		Link l34 = new Link(c3, c4, 0.5, 0.5);
		Link l45 = new Link(c4, c5, 0.5, 0.5);
		
		n1.addLink(l12);
		n2.addLink(l12);
		n2.addLink(l23);
		n3.addLink(l23);
		n3.addLink(l34);
		n4.addLink(l34);
		n4.addLink(l45);
		n5.addLink(l45);
		
		path.add(n1);
		path.add(n2);
		path.add(n3);
		path.add(n4);
		path.add(n5);
		
		edges.add(l12);
		edges.add(l23);
		edges.add(l34);
		edges.add(l45);
		
		NetworkTopology nTest = new NetworkTopology((List<Node>) path, (List<Link>) edges);
		if (nTest.validateTopology()) {
			System.out.println("Topology Works!");
		}
		
		Router router = new Router(path);
		
		System.out.println(router.getLatency());
	}
}
