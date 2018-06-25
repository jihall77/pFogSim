package edu.auburn.pFogSim.Radix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import javafx.util.Pair;
import java.util.LinkedList;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
/**
 * Class for implementing Radix sort for find the closest nodes
 * @author Jacob I Hall jih0007@auburn.edu
 */
public class DistRadix {
	
	private ArrayList<EdgeHost> input;
	private HashMap<Pair<Double, Double>, EdgeHost> coordMap;
	private HashMap<Double, Pair<Double, Double>> distMap;
	private Pair<Double, Double> ref;
	private ArrayList<Pair<Double, Double>> coords;
	private ArrayList<Integer> distances;
	private int[] arrgs;
	/**
	 * constructor
	 * @param in
	 * @param pair
	 */
	public DistRadix(List<EdgeHost> in, Pair<Double, Double> pair) {
		input = new ArrayList<EdgeHost>();
		coordMap = new HashMap<Pair<Double, Double>, EdgeHost>();
		distMap = new HashMap<Double, Pair<Double, Double>>();
		coords = new ArrayList<Pair<Double, Double>>();
		distances = new ArrayList<Integer>();
		for (EdgeHost node : in) {
			input.add(node);
		}
		ref = pair;
	}
	/**
	 * map coords to nodes
	 */
	private void buildCoords() {
		for(EdgeHost node : input) {
			coordMap.put(new Pair<Double, Double>(node.getLocation().getXPos(), node.getLocation().getYPos()), node);
			coords.add(new Pair<Double, Double>(node.getLocation().getXPos(), node.getLocation().getYPos()));
		}
	}
	/**
	 * map distances to coords
	 */
	private void buildDist() {
		double dist = 0;
		for (Pair<Double, Double> loc : coords) {
			dist = Math.sqrt((Math.pow(ref.getKey() - loc.getKey(), 2) + Math.pow(ref.getValue() - loc.getValue(), 2)));
			dist = Math.floor(dist);
			while(distMap.keySet().contains(dist)) {
				dist += 0.001;
			}
			distMap.put(dist, loc);
			distances.add((int) (dist * 1000));
		}
	}
	/**
	 * set the arrgs array
	 */
	private void setArrgs() {
		arrgs = new int[distances.size()];
		for (int i = 0; i < distances.size(); i++) {
			arrgs[i] = distances.get(i);
		}
	}
	/**
	 * find the max of the arrgs array
	 * @return
	 */
	private int maxArrg() {
		int max = 0;
		for (int i = 0; i < arrgs.length; i++) {
			if (arrgs[i] > max) {
				max = arrgs[i];
			}
		}
		return max;
	}
	/**
	 * perform counting sort
	 * @param arr
	 * @param n
	 * @param exp
	 */
	private void countSort(int arr[], int n, int exp)
    {
        int output[] = new int[n];//i don't have time to explain counting sort to you
        int i;					  //this is a private class fam... let it be
        int count[] = new int[10];
        Arrays.fill(count, 0);
 
        for (i = 0; i < n; i++) {
            count[ (arr[i]/exp)%10 ]++;
        }
 
        for (i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }
 
        for (i = n - 1; i >= 0; i--){
            output[count[ (arr[i]/exp)%10 ] - 1] = arr[i];
            count[ (arr[i]/exp)%10 ]--;
        }
 
        for (i = 0; i < n; i++) {
            arr[i] = output[i];
        }
    }
	/**
	 * perform radix sort
	 */
	private void radixSort() {
		int max = maxArrg();
		for (int i = 1; max/i > 0; i*=10) {
			countSort(arrgs, arrgs.length, i);
		}
	}
	/**
	 * get the sorted list
	 * @return
	 */
	private LinkedList<EdgeHost> getList() {
		LinkedList<EdgeHost> output = new LinkedList<EdgeHost>();
		double dist = 0.0;
		Pair<Double, Double> loc;
		EdgeHost node;
		for (int i = 0; i < arrgs.length; i++) {
			dist = arrgs[i]/1000;
			loc = distMap.get(dist);
			node = coordMap.get(loc);
			output.add(node);
		}
		return output;
	}
	/**
	 * public facing method to get the list of sorted nodes
	 * @return
	 */
	public LinkedList<EdgeHost> sortPuddleNodes() {
		buildCoords();
		buildDist();
		setArrgs();
		radixSort();
		return getList();
	}
}
