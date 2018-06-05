/**
 * Node Class for modeling a particular location in the network
 * @author jih0007
 */
package edu.auburn.pFogSim.netsim;

import java.util.ArrayList;
import java.util.List;
import edu.auburn.pFogSim.netsim.Link;
import javafx.util.Pair;

public class Node {
	
	private ArrayList<Link> edges;
	private Pair<Integer, Integer> myLocation;
	/**
	 * Constructor
	 * @param inputEdges
	 * @param coord
	 */
	public Node (List<Link> inputEdges, Pair<Integer, Integer> coord) {
		myLocation = coord;
		for (Link edge : inputEdges) {
			addLink(edge);
		}
	}
	/**
	 * tests to make sure that at least on of the endpoints for the given link is at this node
	 * @param in
	 * @return true/false
	 */
	public boolean validateLink(Link in) {
		if (in.getLeftLink().equals(myLocation) || in.getRightLink().equals(myLocation)) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * set myLocation by coords
	 * @param xin
	 * @param yin
	 */
	public void setLocation(int xin, int yin) {
		myLocation = new Pair<Integer, Integer>(xin, yin);
	}
	/**
	 * set myLocation by designating a coordinate
	 * @param in
	 */
	public void setLocation(Pair<Integer, Integer> in) {
		myLocation = in;
	}
	/**
	 * get the coordinate for this node
	 * @return
	 */
	public Pair<Integer, Integer> getLocation() {
		return myLocation;
	}
	/**
	 * get the edges for this node
	 * @return
	 */
	public ArrayList<Link> getEdges() {
		return edges;
	}
	/**
	 * make sure that all edges on this node are connected to this node
	 * @return the offending invalid link if one is found<br>
	 * returns null if all links are valid
	 */
	public Link validateNode() {
		for (int i = 0; i < edges.size(); i++) {
			if (!validateLink(edges.get(i))) {
				return edges.get(i);
			}
		}
		return null;
	}
	/**
	 * remove a link from this node
	 * @param victim
	 * @return true if the link was removed<br>
	 * false if the input is null
	 * throw IllegalArgumentException if the link does not exist
	 */
	public boolean removeLink(Link victim) {
		if (victim == null) {
			return false;
		}
		for (Link edge : edges) {
			if (edge.equals(victim)) {
				edges.remove(victim);
				return true;
			}
		}
		throw new IllegalArgumentException();
	}
	/**
	 * add a link to this node<br>
	 * if the link is already on this node do nothing<br>
	 * if there exists a link on this node with the same exact endpoints but different latencies throw IllegalArgumentException<br>
	 * if the link is invalid for this node throw IllegalArgumentException
	 * @param in
	 */
	public void addLink(Link in) {
		if (validateLink(in)) {
			for (int i = 0; i < edges.size(); i++) {
				if (in.equals(edges.get(i))) {
					return;//link already exists
				}
				else if (in.equalEndPoints(edges.get(i))) {
					//adding a link whose endpoints exist but with different latencies
					throw new IllegalArgumentException();
				}
			}
			edges.add(in);
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
}
