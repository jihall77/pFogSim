package edu.auburn.pFogSim.clustering;

import java.io.*;
import java.util.ArrayList;
import javafx.util.Pair;
import java.util.HashMap;

import edu.auburn.pFogSim.netsim.NodeSim;

public class FogCluster {
	private String[] lines = null;
	private Integer points[][] = null;
	private double[][] proximityMatrix = null;
	private int clusterNumber;// = 20; // Defines number of clusters to generate.
	private Integer[][][] cluster = new Integer[clusterNumber][][];
	
	/**
	 * @return the cluster
	 */
	public Integer[][][] getCluster() {
		return cluster;
	} // end getCluster


	/*
	 * Method - stdInput from EdgeServerManager
	 * 
	 */
	public void stdInput(ArrayList<NodeSim> nodes) 
	{
		//Passed args : ArrayList<NodeSim> nodes
		//We need the data and the total number of nodes in this cluster
		
		Pair<Integer, Integer> location = null;
		int x = -1, y = -1, level = -1;
		
		HashMap<Integer, ArrayList<Pair<Integer, Integer>>> levelMap = new HashMap<Integer, ArrayList<Pair<Integer, Integer>>>();
		//Pair<Integer, ArrayList<Pair<Integer, Integer>>> levelMap = new Pair<Integer, ArrayList<Pair<Integer, Integer>>>(null, null);
		
		
		for(NodeSim node : nodes) 
		{
			location = node.getLocation();
			if(location != null) 
			{   
				//System.out.println(location);
				x = location.getKey();
				y = location.getValue();
				level = node.getLevel();
				
				//System.out.println(String.format("\tx = %d\n\ty = %d\n\tlevel = %d", x, y, level));
				//Sort out all of the nodes by their levels
				ArrayList<Pair<Integer, Integer>> arr = levelMap.get(level);
				if(arr == null) {
					arr = new ArrayList<Pair<Integer, Integer>>();
				}
				arr.add(new Pair<Integer, Integer>(x, y));
				levelMap.put(level, arr);
			}
		}
		//int sum = 0;
		/*for(int the_level = 0; the_level < 8; the_level++)
		{
			if (levelMap.get(the_level) != null) {
				//System.out.println(String.format("There are %d nodes in level %d", levelMap.get(the_level).size(), the_level));
				//sum += levelMap.get(the_level).size();
				for(Pair<Integer, Integer> pair : levelMap.get(the_level))
				{
					//System.out.println(String.format("\n\tLevel = %d\n\tx = %d\n\ty = %d", the_level, pair.getKey(), pair.getValue()));
				}
			}
		}*/
		//System.out.println("Total number of nodes = " + sum);
		//SimLogger.printLine("stdInput reached");
	
		
		//CJ Clustering technique is implemented here

		int[] parentCluster;
		double clusterMaxDistance = 0 ;
		double minDistance = Double.MAX_VALUE;
		int parent = 0;
		double distance = 0;
		
		
		//Now, for each set of clusters in adjacent layers, repeat the following:
		//Say clusters in layer-3 & layer-4
		for(int leveliter = 0; leveliter < 8; leveliter++)
		{
			if(levelMap.get(leveliter) != null && levelMap.get(leveliter + 1) != null)
			{
				int clusterNumber3 = levelMap.get(leveliter).size();
				int clusterNumber4 = levelMap.get(leveliter + 1).size();
				parentCluster = new int[clusterNumber3];
				
				//For each cluster in lower layer, do the following
				for (int cLower=0; cLower<clusterNumber3; cLower++){
					minDistance = Double.MAX_VALUE;
					parent = 0;
					
					//For each cluster in upper layer, do the following
					for(int cUpper=0; cUpper<clusterNumber4; cUpper++){
						
						clusterMaxDistance = 0;
						//Calculate the ('max' for CompleteLink) distance between cluster from lower layer 'cLower'
						//and cluster from higher layer 'cUpper'
						// i.e. find the distance between each point of 'cLower' cluster 
						// and each point of 'cUpper' cluster
						// Note the maximum distance
						
						//From each point of 'cLower' cluster
						for (int cLoweri=0; cLoweri<levelMap.get(leveliter).size(); cLoweri++){
							// Get point coordinates
							//int x1 = clusterSet3[cLower][cLoweri][0];
							int x1 = levelMap.get(leveliter).get(cLower).getKey();
							int y1 = levelMap.get(leveliter).get(cLower).getValue();
							
							//To each point of 'cUpper' cluster
							for (int cUpperj=0; cUpperj<levelMap.get(leveliter + 1).size(); cUpperj++){
								// Get point coordinates
								int x2 = levelMap.get(leveliter + 1).get(cUpper).getKey();
								int y2 = levelMap.get(leveliter + 1).get(cUpper).getValue();
														
								//find the distance
								distance = Math.sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
								//System.out.println(distance);
								
								// Save the maximum distance
								if (distance > clusterMaxDistance){
									clusterMaxDistance = distance;
								}
								
							}// end for cUpperj
						}// end for cLoweri
		
						//If this is the closer upper layer cluster, then this is a better parent cluster
						if (clusterMaxDistance < minDistance){
							minDistance = clusterMaxDistance;
							parentCluster[cLower] = cUpper; 
						}
						
					}// end for cUpper
				}// end for cLower
				
				//Print Parent/Child relationships
				//System.out.println("ChildCluster"+"   "+"ParentCluster");
				//for (int cLower=0; cLower<clusterNumber3; cLower++){
					//System.out.println("         "+cLower+"   "+"         "+parentCluster[cLower]);
				}// end for cLower-Print
			}
		}
		
	
	
	/**
	 *  Method - csvInput
	 * 
	 */
	public void csvInput(String fn){
		try
		{
			java.util.List lines = new ArrayList(); 
			java.util.List points = new ArrayList();
			
			// Read data points from DataSet file 
			BufferedReader reader = new BufferedReader(new FileReader(fn));
			String line;
			while ((line = reader.readLine())!=null){
				lines.add(line);
				
				String[] pointString = line.split(",");
				Integer[] point = new Integer[2];
				point[0] = Integer.parseInt(pointString[0].trim());
				point[1] = Integer.parseInt(pointString[1].trim());
				
				//System.out.println(point[0]+","+point[1]);
				points.add(point);
				
			}// end while

			this.points = (Integer[][])points.toArray(new Integer[points.size()][]);
			
			reader.close();
		} catch (Exception e){
			e.printStackTrace(System.err);
		}
		
	}//end csvInput()
	
	
	/**
	 * @param clusterNumber the clusterNumber to set
	 */
	public void setClusterNumber(int clusterNumber) {
		this.clusterNumber = clusterNumber;
	}


	/**
	 * Calculate Proximity matrix
	 * Method - calcProximity
	 * 
	 */
	void calcProximity(){
		
		int x1=0, y1=0, x2=0, y2=0;
		double distance;
		
		// assume n data points ; n = size
		// declare an nxn array of double type
		
		int n = points.length;
		//System.out.println("Number of points: "+n);
		
		proximityMatrix = new double[n][n];
		
		for (int i=0; i<n; i++){
			// First point
			x1 = points[i][0];
			y1 = points[i][1];
			
			for (int j=0; j<n; j++){
				//Second point
				x2 = points[j][0];
				y2 = points[j][1];
				
				//Calculate distance
				distance = Math.sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
				//System.out.println(distance);
				
				//Update entry in proximityMatrix
				proximityMatrix[i][j] = distance;
				
			}// end for j
				
		}//end for i
		
	}//end calcProximity()

	
	public void learn(){
		
		//HierarchicalClustering hc = new HierarchicalClustering(new SingleLinkage(proximityMatrix));
		HierarchicalClustering hc = new HierarchicalClustering(new CompleteLinkage(proximityMatrix));
		//System.out.println("clusterNumber is: "+clusterNumber);
		int[] membership = hc.partition(clusterNumber);
		int[] clusterSize = new int[clusterNumber];
		//System.out.println("membership[] length: "+membership.length);
		for (int i=0; i< membership.length; i++){
			clusterSize[membership[i]]++;
			//System.out.println("i membership[i] clusterSize: "+i+"   "+membership[i]+"   "+clusterSize[membership[i]]);
		} 
		
		cluster = new Integer[clusterNumber][][];
		//System.out.println("clusterNumber is: "+clusterNumber);
		for (int k=0; k<clusterNumber; k++){
			//System.out.println("k clusterSize[k]: "+k+"   "+clusterSize[k]);
			cluster[k] = new Integer[clusterSize[k]][2];
			
			for (int i=0,j=0; i<points.length; i++){
				if (membership[i] == k){
					cluster[k][j++] = points[i];
				}// end if				
			}// end for i,j
			
			// These are classified as a cluster; print these separately. 
			//System.out.println("\n\n Cluster Number: " + k +"\n");
			for (int i=0; i<clusterSize[k]; i++){
				//System.out.println(cluster[k][i][0]+" , "+cluster[k][i][1]);
			}// end for i
			
		}// end for k
		
		/* code prior to change
		 * 		for (int k=0; k<clusterNumber; k++){
			Integer[][] cluster = new Integer[clusterSize[k]][];
			
			for (int i=0,j=0; i<points.length; i++){
				if (membership[i] == k){
					cluster[j++] = points[i];
				}// end if				
			}// end for i,j
			
			// These are classified as a cluster; print these separately. 
			//System.out.println("\n\n Cluster Number: " + k +"\n");
			for (int i=0; i<clusterSize[k]; i++){
				//System.out.println(cluster[i][0]+" , "+cluster[i][1]);
			}// end for i
			
		}// end for k
		
		 * */
		
		/** Copied code here
		 for (int k = 0; k < clusterNumber; k++) {
	            double[][] cluster = new double[clusterSize[k]][];
	            for (int i = 0, j = 0; i < dataset[datasetIndex].length; i++) {
	                if (membership[i] == k) {
	                    cluster[j++] = dataset[datasetIndex][i];
	                }
	            }

	            plot.points(cluster, pointLegend, Palette.COLORS[k % Palette.COLORS.length]);
	        }
		*/
		
				
	}// end learn()
	
	
	/**
	 * Constructor
	 */
	public FogCluster(String fn, int cNum) {
		super();
		//SimLogger.printLine("String and int constructor FogCluster() reached");
		setClusterNumber(cNum);
		csvInput(fn);
		calcProximity();
		learn();		
		
	}// end Constructor FogHierCluster()
	
	public FogCluster(ArrayList<NodeSim> nodes) {
		super();
		//SimLogger.printLine("Blank constructor FogCluster() reached");
		stdInput(nodes);
	}


	public static void main(String[] args) {
		/*
		int clusterNumber1 = 100;
		String fileName1 = new String("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L1-500");
		FogCluster fc1 = new FogCluster(fileName1, clusterNumber1);
		Integer[][][] clusters1 = fc1.getCluster(); 
		*//*
		int clusterNumber2 = 40;
		String fileName2 = new String("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L2-200");
		FogCluster fc2 = new FogCluster(fileName2, clusterNumber2);
		Integer[][][] clusters2 = fc2.getCluster();
		*//*
		int clusterNumber3 = 20;
		String fileName3 = new String("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L3-50");
		FogCluster fc3 = new FogCluster(fileName3, clusterNumber3);
		Integer[][][] clusters3 = fc3.getCluster();
	
		int clusterNumber4 = 3;
		String fileName4 = new String("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L4-10");
		FogCluster fc4 = new FogCluster(fileName4, clusterNumber4);
		Integer[][][] clusters4 = fc4.getCluster();
		
		*/
		
		
	}// End main

}// end class FogCluster


