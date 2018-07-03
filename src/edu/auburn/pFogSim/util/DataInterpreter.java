package edu.auburn.pFogSim.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.auburn.pFogSim.netsim.NodeSim;
import edu.boun.edgecloudsim.utils.SimLogger;

public class DataInterpreter {
	private static int MAX_LEVELS = 7;
	private static String[][] nodeSpecs = new String[MAX_LEVELS][13];// the specs for all layers of the fog devices
	private File xmlFile = null;
	private FileWriter xmlFW = null;
	private BufferedWriter xmlBR = null;
	
	public static void readFile(String inputfile, String outputfile, int level) throws IOException {
		File dataFile = new File(inputfile);
		FileReader dataFR = null;
		BufferedReader dataBR = null;
		
		
		
		String rawNode = null;
		String[] nodeLoc = new String[3];
		
		try {
			dataFR = new FileReader(dataFile);
			dataBR = new BufferedReader(dataFR);
		}
		catch (FileNotFoundException e) {
			SimLogger.printLine("Bad File Name");
		}
		dataBR.readLine();
		while(dataBR.ready()) {
			rawNode = dataBR.readLine();
			nodeLoc = rawNode.split(",");

		}
		return;
	}
	
	public static void main(String[] args) throws IOException {
		readFile("scripts/sample_application/config/Chicago_Schools.csv", "schoolNode.xml", 1);
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
