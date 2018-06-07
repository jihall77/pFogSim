package edu.auburn.pFogSim.clustering;

import java.io.*;
import java.util.ArrayList;
import java.lang.Math.*;

//public class FogCluster {
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
				
				System.out.println(point[0]+","+point[1]);
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
		System.out.println("Number of points: "+n);
		
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
				System.out.println(distance);
				
				//Update entry in proximityMatrix
				proximityMatrix[i][j] = distance;
				
			}// end for j
				
		}//end for i
		
	}//end calcProximity()

	
	public void learn(){
		
		//HierarchicalClustering hc = new HierarchicalClustering(new SingleLinkage(proximityMatrix));
		HierarchicalClustering hc = new HierarchicalClustering(new CompleteLinkage(proximityMatrix));
		System.out.println("clusterNumber is: "+clusterNumber);
		int[] membership = hc.partition(clusterNumber);
		int[] clusterSize = new int[clusterNumber];
		System.out.println("membership[] length: "+membership.length);
		for (int i=0; i< membership.length; i++){
			clusterSize[membership[i]]++;
			System.out.println("i membership[i]: "+i+"   "+membership[i]);
		} 
		
		//Integer[][][] cluster = new Integer[clusterNumber][][];
		System.out.println("clusterNumber is: "+clusterNumber);
		for (int k=0; k<clusterNumber; k++){
			cluster[k] = new Integer[clusterSize[k]][];
			
			for (int i=0,j=0; i<points.length; i++){
				if (membership[i] == k){
					cluster[k][j++] = points[i];
				}// end if				
			}// end for i,j
			
			// These are classified as a cluster; print these separately. 
			System.out.println("\n\n Cluster Number: " + k +"\n");
			for (int i=0; i<clusterSize[k]; i++){
				System.out.println(cluster[k][i][0]+" , "+cluster[k][i][1]);
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
			System.out.println("\n\n Cluster Number: " + k +"\n");
			for (int i=0; i<clusterSize[k]; i++){
				System.out.println(cluster[i][0]+" , "+cluster[i][1]);
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
		setClusterNumber(cNum);
		csvInput(fn);
		calcProximity();
		learn();		
		
	}// end Constructor FogHierCluster()


	public static void main(String[] args) {
/*
		int clusterNumber1 = 100;
		String fileName1 = new String("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L1-500");
		FogCluster fc1 = new FogCluster(fileName1, clusterNumber1);
		Integer[][][] clusters1 = fc1.getCluster(); 
		
		int clusterNumber2 = 200;
		String fileName2 = new String("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L2-200");
		FogCluster fc2 = new FogCluster(fileName2, clusterNumber2);
		Integer[][][] clusters2 = fc2.getCluster();
		
		int clusterNumber3 = 20;
		String fileName3 = new String("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L3-50");
		FogCluster fc3 = new FogCluster(fileName3, clusterNumber3);
		Integer[][][] clusters3 = fc3.getCluster();
	*/	
		int clusterNumber4 = 3;
		String fileName4 = new String("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L4-10");
		FogCluster fc4 = new FogCluster(fileName4, clusterNumber4);
		Integer[][][] clusters4 = fc4.getCluster();
		
		
		
		
	}// End main

}// end class FogHierCluster



/*
 * Now we have the clustering details of each layer in the data structures 
 * 		- points[x][y]	//Array of size nx2 specifying location of fog node i in row i
 * 		- membership[k] //Array of size clusterNumber specifying cluster membership of fog node i in row i
 * 		- clusterNumber //Number of clusters in this layer
 * Save clustering information of all layers in data structures.
 * For each set of clusters, identify and group points belonging to a cluster 
 * 		(do we need to save this information? may be yes, to not repeat the process, 
 * 		but need additional storage space.)
 * Now, for each set of clusters in adjacent layers, repeat the following:
 * 		For each cluster in lower layer, do the following
 * 			Calculate the ('max' for CompleteLink) distance between this cluster and a cluster from higher layer
 * 			Repeat the above step for each cluster from higher layer
 * 			Identify the higher layer cluster with least distance
 * 			Mark it as the parent for lower layer cluster
 * 			Save this information in parent data structure specifying cluster id of parent cluster 
 * 				from higher layer in row i identifying cluster i of lower layer. 
 *  
 * */
 

