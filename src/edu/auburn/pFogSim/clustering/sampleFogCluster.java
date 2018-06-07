public class sampleFogCluster {

	private Integer points[][] = null;
	private double[][] proximityMatrix = null;
	private int clusterNumber = 3; // Defines number of clusters to generate.
	
	/**
	 *  Method - csvInput
	 * 
	 */
	public void csvInput(){
		
		try
		{
			java.util.List points = new ArrayList();
			
			// Read data points from DataSet file 
			BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\szs0117\\workspace\\tia\\src\\KMCluster\\LocData-L4-10"));
			String line;
			while ((line = reader.readLine())!=null){
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
		
		int[] membership = hc.partition(clusterNumber);
		int[] clusterSize = new int[clusterNumber];
		for (int i=0; i< membership.length; i++){
			clusterSize[membership[i]]++;
		}
		
		for (int k=0; k<clusterNumber; k++){
			Integer[][] cluster = new Integer[clusterSize[k]][];
			
			for (int i=0,j=0; i<points.length; i++){
				if (membership[i] == k){
					cluster[j++] = points[i];
				}// end if				
			}// end for i,j
			
			/* These are classified as a cluster; print these separately. */
			System.out.println("\n\n Cluster Number: " + k +"\n");
			for (int i=0; i<clusterSize[k]; i++){
				System.out.println(cluster[i][0]+" , "+cluster[i][1]);
			}// end for i
			
		}// end for k
		
		
		
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
	public FogCluster() {
		super();
		csvInput();
		calcProximity();
		learn();		
		
	}// end Constructor FogHierCluster()


	public static void main(String[] args) {

		new FogCluster();
		
	}// End main

}// end class FogHierCluster
