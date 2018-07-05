package edu.auburn.pFogSim.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.auburn.pFogSim.netsim.NodeSim;
import edu.boun.edgecloudsim.utils.SimLogger;



public class DataInterpreter {
	private static int MAX_LEVELS = 7;
	private static String[] files= {
			"Google_Cloud_DC.csv", 
			"Chicago_CityHall.csv", 
			"Chicago_Universities.csv", 
			"Chicago_Wards.csv", 
			"Chicago_Libraries.csv", 
			"Chicago_Connect.csv", 
			"Chicago_Schools.csv"};
	private static String[][] nodeSpecs = new String[MAX_LEVELS][13];// the specs for all layers of the fog devices
	private static ArrayList<Double[]> nodeList = new ArrayList<Double[]>();
	private static ArrayList<Float[]> tempList = new ArrayList<Float[]>();

	private File xmlFile = null;
	private FileWriter xmlFW = null;
	private BufferedWriter xmlBR = null;
	
	public static double measure(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
	    double R = 6378.137; // Radius of earth in KM
	    double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
	    double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
	    //Haversine Formula
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon/2) * Math.sin(dLon/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double d = R * c;
	    return d * 1000; // meters
	}
	
	public static void readFile() throws IOException {
		FileReader dataFR = null;
		BufferedReader dataBR = null;
		PrintWriter node = new PrintWriter("node_test.xml", "UTF-8");
	    PrintWriter links = new PrintWriter("links_test.xml", "UTF-8");
		
	    node.println("<?xml version=\"1.0\"?>");
	    links.println("<?xml version=\"1.0\"?>");
	    node.println("<edge_devices>");
	    links.println("<links>");
	    
		String rawNode = null;
		String[] nodeLoc = new String[3];
		Float[] temp = new Float[3];
		int counter = 0;
		int prevCounter = 0;
		for(int i = 0; i < MAX_LEVELS; i++)
		{
			
			try {
				dataFR = new FileReader(files[i]);
				dataBR = new BufferedReader(dataFR);
			}
			catch (FileNotFoundException e) {
				SimLogger.printLine("Bad File Name");
			}
			dataBR.readLine(); //Gets rid of title data
			while(dataBR.ready()) {

				//SimLogger.printLine("Importing " + files[i]);
				rawNode = dataBR.readLine();
				nodeLoc = rawNode.split(",");
				temp[0] = (float)counter; //ID
				temp[2] = Float.parseFloat(nodeLoc[1]); //Y Coord
				temp[1] = Float.parseFloat(nodeLoc[2]); //X Coord
				
				//Add to output file		    
			    node.println(String.format("<datacenter arch=\"%s\" os=\"%s\" vmm=\"%s\">\n", nodeSpecs[MAX_LEVELS - i - 1][0], nodeSpecs[MAX_LEVELS - i - 1][1], nodeSpecs[MAX_LEVELS - i - 1][2]));
			    node.println(String.format("<costPerBw>%s</costPerBw>\n\t<costPerSec>%s</costPerSec>\n\t<costPerMem>%s</costPerMem>\n\t<costPerStorage>%s</costPerStorage>", nodeSpecs[MAX_LEVELS - i - 1][3], nodeSpecs[MAX_LEVELS - i - 1][4], nodeSpecs[MAX_LEVELS - i - 1][5], nodeSpecs[MAX_LEVELS - i - 1][6]));
			    node.println(String.format("<location>\n\t<x_pos>%s</x_pos>\n\t<y_pos>%s</y_pos>\n\t<level>%s</level>\t<wlan_id>%s</wlan_id>\n\t<wap>%s</wap>\n\t<moving>%s</moving>\n\t</location>", nodeLoc[1], nodeLoc[2], MAX_LEVELS - i - 1, counter, nodeSpecs[MAX_LEVELS - i - 1][7], nodeSpecs[MAX_LEVELS - i - 1][8]));
			    node.println(String.format("<hosts>\n\t<host>\n\t<core>%s</core>\n\t<mips>%s</mips>\n\t<ram>%s</ram>\n\t<storage>%s</storage>\n", nodeSpecs[MAX_LEVELS - i - 1][9], nodeSpecs[MAX_LEVELS - i - 1][10], nodeSpecs[MAX_LEVELS - i - 1][11], nodeSpecs[MAX_LEVELS - i - 1][12]));
			    node.println(String.format("\t<VMs>\n\t\t<VM vmm=\"%s\">\n\t\t\t<core>%s</core>\n\t\t\t<mips>%s</mips>\n\t\t\t<ram>%s</ram>\n\t\t\t<storage>%s</storage>\n\t\t</VM>\n\t</VMs>\n</host></hosts>\n</datacenter>", nodeSpecs[MAX_LEVELS - i - 1][2], nodeSpecs[MAX_LEVELS - i - 1][9], nodeSpecs[MAX_LEVELS - i - 1][10], nodeSpecs[MAX_LEVELS - i - 1][11], nodeSpecs[MAX_LEVELS - i - 1][12]));
	
				
			    
				//Make link to previous closest node on higher level
				if(!nodeList.isEmpty())
				{
					double minDistance = 1000000;
					int index = -1;
					double distance = 0;
					//Go through all nodes one level up and find the closest
					for(int j = 0; j < nodeList.size(); j++)
					{
						//SimLogger.printLine("nodeList.size = " + nodeList.size());

						distance = measure(nodeList.get(j)[2], nodeList.get(j)[1], temp[2], temp[1]);
						if(distance < minDistance)
						{
							minDistance = distance;
							index = j;
						}
					}
					if(index >= 0)
					{
						if(nodeList.get(index).equals(temp)) 
						{
							SimLogger.printLine("Yep, they're the same thing");
							System.exit(0);
						}
						links.println("<link>\n" + 
					    		"		<name>L" + nodeList.get(index)[0] + "_" + temp[0] + "</name>\n" + 
					    		"		<left>\n" + 
					    		"			<x_pos>" + (double)temp[1] + "</x_pos>\n" + 
					    		"			<y_pos>" + (double)temp[2] + "</y_pos>\n" + 
					    		"		</left>\n" + 
					    		"		<right>\n" + 
					    		"			<x_pos>" + nodeList.get(index)[1] + "</x_pos>\n" + 
					    		"			<y_pos>" + nodeList.get(index)[2] + "</y_pos>\n" + 
					    		"		</right>\n" + 
					    		"		<left_latency>0.5</left_latency>\n" + 
					    		"		<right_latency>0.5</right_latency>\n" + 
					    		"	</link>");
					}
				}
				
				tempList.add(temp);
				counter++;
			}
			
			//SimLogger.printLine("Level : " + i + "\n\t" + prevCounter + " -> " + counter);
			prevCounter = counter;
			//SimLogger.printLine("nodeList" + nodeList.toString());
			//SimLogger.printLine("tempList" + tempList.toString());
			//move tempList to nodeList
			nodeList.clear();
			//nodeList.addAll(tempList);
			for(Float[] input : tempList)
			{
				nodeList.add(new Double[] {(double)input[0], (double)input[1], (double)input[2]});
			}
			tempList.clear();
			
			//SimLogger.printLine("nodeList" + nodeList.toString());
			//SimLogger.printLine("tempList" + tempList.toString());
		}
		
		node.println("</edge_devices>");
		links.println("</links>");
		node.close();
		links.close();
		return;
	}
	
	public DataInterpreter() throws IOException {
		initialize();
		readFile();
	}
	/**
	 * the great beast...<br><br>
	 * 
	 * this is where we define the specs for the machines on the network.<br>
	 * if you have a file to read these from go ahead, we, however, are defining them by hand.<br>
	 * the first dimension of the array represents the fog layer<br>
	 * the second dimension represents the particular attribute<br><br>
	 * 
	 *   0 - architecture<br>
	 *   1 - operating systems<br>
	 *   2 - virtual machine manager<br>
	 *   3 - cost per Bandwidth<br>
	 *   4 - cost per second<br>
	 *   5 - cost per memory<br>
	 *   6 - cost per storage<br>
	 *   7 - is node a wifi access point<br>
	 *   8 - is fog node moving<br>
	 *   9 - number of cores for the machine<br>
	 *  10 - million instructions per second (mips)<br>
	 *  11 - ram<br>
	 *  12 - storage 
	 */
	public static void initialize() {
		nodeSpecs[MAX_LEVELS - 1][0] = "Cloud";
		nodeSpecs[MAX_LEVELS - 1][1] = "Linux";
		nodeSpecs[MAX_LEVELS - 1][2] = "Xen";
		nodeSpecs[MAX_LEVELS - 1][3] = "0.1";
		nodeSpecs[MAX_LEVELS - 1][4] = "3.0";
		nodeSpecs[MAX_LEVELS - 1][5] = "0.05";
		nodeSpecs[MAX_LEVELS - 1][6] = "0.1";
		nodeSpecs[MAX_LEVELS - 1][7] = "false";
		nodeSpecs[MAX_LEVELS - 1][8] = "false";
		nodeSpecs[MAX_LEVELS - 1][9] = "8000";
		nodeSpecs[MAX_LEVELS - 1][10] = "500000000";
		nodeSpecs[MAX_LEVELS - 1][11] = "40000000";
		nodeSpecs[MAX_LEVELS - 1][12] = "100000000000";
		nodeSpecs[MAX_LEVELS - 2][0] = "x86";
		nodeSpecs[MAX_LEVELS - 2][1] = "Linux";
		nodeSpecs[MAX_LEVELS - 2][2] = "Xen";
		nodeSpecs[MAX_LEVELS - 2][3] = "0.15";
		nodeSpecs[MAX_LEVELS - 2][4] = "3.0";
		nodeSpecs[MAX_LEVELS - 2][5] = "0.05";
		nodeSpecs[MAX_LEVELS - 2][6] = "0.1";
		nodeSpecs[MAX_LEVELS - 2][7] = "true";
		nodeSpecs[MAX_LEVELS - 2][8] = "false";
		nodeSpecs[MAX_LEVELS - 2][9] = "2";
		nodeSpecs[MAX_LEVELS - 2][10] = "5520";
		nodeSpecs[MAX_LEVELS - 2][11] = "2000";
		nodeSpecs[MAX_LEVELS - 2][12] = "512";
		nodeSpecs[MAX_LEVELS - 3][0] = "x86";
		nodeSpecs[MAX_LEVELS - 3][1] = "Linux";
		nodeSpecs[MAX_LEVELS - 3][2] = "Xen";
		nodeSpecs[MAX_LEVELS - 3][3] = "0.15";
		nodeSpecs[MAX_LEVELS - 3][4] = "3.0";
		nodeSpecs[MAX_LEVELS - 3][5] = "0.05";
		nodeSpecs[MAX_LEVELS - 3][6] = "0.1";
		nodeSpecs[MAX_LEVELS - 3][7] = "false";
		nodeSpecs[MAX_LEVELS - 3][8] = "false";
		nodeSpecs[MAX_LEVELS - 3][9] = "2";
		nodeSpecs[MAX_LEVELS - 3][10] = "5520";
		nodeSpecs[MAX_LEVELS - 3][11] = "1000";
		nodeSpecs[MAX_LEVELS - 3][12] = "256";
		nodeSpecs[MAX_LEVELS - 4][0] = "x86";
		nodeSpecs[MAX_LEVELS - 4][1] = "Linux";
		nodeSpecs[MAX_LEVELS - 4][2] = "Xen";
		nodeSpecs[MAX_LEVELS - 4][3] = "0.15";
		nodeSpecs[MAX_LEVELS - 4][4] = "3.0";
		nodeSpecs[MAX_LEVELS - 4][5] = "0.05";
		nodeSpecs[MAX_LEVELS - 4][6] = "0.1";
		nodeSpecs[MAX_LEVELS - 4][7] = "false";
		nodeSpecs[MAX_LEVELS - 4][8] = "false";
		nodeSpecs[MAX_LEVELS - 4][9] = "2";
		nodeSpecs[MAX_LEVELS - 4][10] = "5520";
		nodeSpecs[MAX_LEVELS - 4][11] = "1000";
		nodeSpecs[MAX_LEVELS - 4][12] = "128";
		nodeSpecs[MAX_LEVELS - 5][0] = "x86";
		nodeSpecs[MAX_LEVELS - 5][1] = "Linux";
		nodeSpecs[MAX_LEVELS - 5][2] = "Xen";
		nodeSpecs[MAX_LEVELS - 5][3] = "0.15";
		nodeSpecs[MAX_LEVELS - 5][4] = "3.0";
		nodeSpecs[MAX_LEVELS - 5][5] = "0.05";
		nodeSpecs[MAX_LEVELS - 5][6] = "0.1";
		nodeSpecs[MAX_LEVELS - 5][7] = "true";
		nodeSpecs[MAX_LEVELS - 5][8] = "false";
		nodeSpecs[MAX_LEVELS - 5][9] = "2";
		nodeSpecs[MAX_LEVELS - 5][10] = "5520";
		nodeSpecs[MAX_LEVELS - 5][11] = "256";
		nodeSpecs[MAX_LEVELS - 5][12] = "64";
		nodeSpecs[MAX_LEVELS - 6][0] = "x86";
		nodeSpecs[MAX_LEVELS - 6][1] = "Linux";
		nodeSpecs[MAX_LEVELS - 6][2] = "Xen";
		nodeSpecs[MAX_LEVELS - 6][3] = "0.15";
		nodeSpecs[MAX_LEVELS - 6][4] = "3.0";
		nodeSpecs[MAX_LEVELS - 6][5] = "0.05";
		nodeSpecs[MAX_LEVELS - 6][6] = "0.1";
		nodeSpecs[MAX_LEVELS - 6][7] = "true";
		nodeSpecs[MAX_LEVELS - 6][8] = "false";
		nodeSpecs[MAX_LEVELS - 6][9] = "2";
		nodeSpecs[MAX_LEVELS - 6][10] = "5520";
		nodeSpecs[MAX_LEVELS - 6][11] = "128";
		nodeSpecs[MAX_LEVELS - 6][12] = "64";
		nodeSpecs[MAX_LEVELS - 7][0] = "Mobile Device";
		nodeSpecs[MAX_LEVELS - 7][1] = "Mobile Device";
		nodeSpecs[MAX_LEVELS - 7][2] = "Mobile Device";
		nodeSpecs[MAX_LEVELS - 7][3] = "0";
		nodeSpecs[MAX_LEVELS - 7][4] = "0";
		nodeSpecs[MAX_LEVELS - 7][5] = "0";
		nodeSpecs[MAX_LEVELS - 7][6] = "0";
		nodeSpecs[MAX_LEVELS - 7][7] = "false";
		nodeSpecs[MAX_LEVELS - 7][8] = "true";
		nodeSpecs[MAX_LEVELS - 7][9] = "0";
		nodeSpecs[MAX_LEVELS - 7][10] = "0";
		nodeSpecs[MAX_LEVELS - 7][11] = "0";
		nodeSpecs[MAX_LEVELS - 7][12] = "0";
	}
}
