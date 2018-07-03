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
	private static String[][] nodeSpecs = new String[MAX_LEVELS][14];// the specs for all layers of the fog devices
	
	public static void readFile(String inputfile, String outputfile) throws IOException {
		File dataFile = new File(inputfile);
		FileReader dataFR = null;
		BufferedReader dataBR = null;
		
		File xmlFile = new File(outputfile);
		FileWriter xmlFW = new FileWriter(xmlFile);
		BufferedWriter xmlBR = new BufferedWriter(xmlFW);
		
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
		readFile("scripts/sample_application/config/Chicago_Schools.csv", "schoolNode.xml");
	}
	
	public static void initialize() {
		nodeSpecs[MAX_LEVELS - 1][0] = "Cloud";
		nodeSpecs[MAX_LEVELS - 1][1] = "Linux";
		nodeSpecs[MAX_LEVELS - 1][2] = "Xen";
		nodeSpecs[MAX_LEVELS - 1][3] = "0.1";
		nodeSpecs[MAX_LEVELS - 1][4] = "3.0";
		nodeSpecs[MAX_LEVELS - 1][5] = "0.05";
		nodeSpecs[MAX_LEVELS - 1][6] = "0.1";
		nodeSpecs[MAX_LEVELS - 1][7] = "0";
		nodeSpecs[MAX_LEVELS - 1][8] = "false";
		nodeSpecs[MAX_LEVELS - 1][9] = "false";
		nodeSpecs[MAX_LEVELS - 1][10] = "8000";
		nodeSpecs[MAX_LEVELS - 1][11] = "500000000";
		nodeSpecs[MAX_LEVELS - 1][12] = "40000000";
		nodeSpecs[MAX_LEVELS - 1][13] = "100000000000";
	}
}
