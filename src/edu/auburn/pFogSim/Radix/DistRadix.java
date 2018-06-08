package edu.auburn.pFogSim.Radix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import javafx.util.Pair;
import java.util.LinkedList;
import edu.boun.edgecloudsim.edge_server.EdgeHost;

public class DistRadix {
	
	private ArrayList<EdgeHost> input;
	private HashMap<Pair<Integer, Integer>, EdgeHost> coordMap;
	private HashMap<Double, Pair<Integer, Integer>> distMap;
	private Pair<Integer, Integer> ref;
	private ArrayList<Pair<Integer, Integer>> coords;
	private ArrayList<Integer> distances;
	private int[] arrgs;
	
	public DistRadix(List<EdgeHost> in, Pair<Integer, Integer> reference) {
		for (EdgeHost node : in) {
			input.add(node);
		}
		ref = reference;
	}
	
	public void buildCoords() {
		for(EdgeHost node : input) {
			coordMap.put(new Pair<Integer, Integer>(node.getLocation().getXPos(), node.getLocation().getYPos()), node);
			coords.add(new Pair<Integer, Integer>(node.getLocation().getXPos(), node.getLocation().getYPos()));
		}
	}
	
	public void buildDist() {
		double dist = 0;
		for (Pair<Integer, Integer> loc : coords) {
			dist = Math.sqrt((Math.pow(ref.getKey() - loc.getKey(), 2) + Math.pow(ref.getValue() - loc.getValue(), 2)));
			dist = Math.floor(dist);
			while(distMap.keySet().contains(dist)) {
				dist += 0.001;
			}
			distMap.put(dist, loc);
			distances.add((int) (dist * 1000));
		}
	}
	
	public void setArrgs() {
		arrgs = new int[distances.size()];
		for (int i = 0; i < distances.size(); i++) {
			arrgs[i] = distances.get(i);
		}
	}
	
	private int maxArrg() {
		int max = 0;
		for (int i = 0; i < arrgs.length; i++) {
			if (arrgs[i] > max) {
				max = arrgs[i];
			}
		}
		return max;
	}
	
	private void countSort(int arr[], int n, int exp)
    {
        int output[] = new int[n];
        int i;
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
	
	public void radixSort() {
		int max = maxArrg();
		for (int i = 1; max/i > 0; i*=10) {
			countSort(arrgs, arrgs.length, i);
		}
	}
	
	public LinkedList<EdgeHost> getlist() {
		LinkedList<EdgeHost> output = new LinkedList<EdgeHost>();
		double dist = 0.0;
		Pair<Integer, Integer> loc;
		EdgeHost node;
		for (int i = 0; i < arrgs.length; i++) {
			dist = arrgs[i]/1000;
			loc = distMap.get(dist);
			node = coordMap.get(loc);
			output.add(node);
		}
		return output;
	}
	
}
