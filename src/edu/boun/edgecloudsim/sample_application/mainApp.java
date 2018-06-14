/*
 * Title:        EdgeCloudSim - Sample Application
 * 
 * Description:  Sample application for EdgeCloudSim
 *               
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.sample_application;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import edu.boun.edgecloudsim.core.ScenarioFactory;
import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;

public class mainApp {
	
	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		//disable console output of cloudsim library
		/*
		 * @author CJ
		 * trying to output the test files we want
		 * 
		 */
		
		try{
		    PrintWriter node = new PrintWriter("large_node_test.xml", "UTF-8");
		    PrintWriter links = new PrintWriter("large_links_test.xml", "UTF-8");
		    node.println("<?xml version=\"1.0\"?>");
		    links.println("<?xml version=\"1.0\"?>");
		    int counter = 1;
		    
		    node.println("<edge_devices>");
		    links.println("<links>");
		    
		    //Make a the cloud
		    node.println("<datacenter arch=\"x86\" os=\"Linux\" vmm=\"Xen\">\n");
		    node.println("<costPerBw>0.1</costPerBw>\n" + 
		    		"		<costPerSec>3.0</costPerSec>\n" + 
		    		"		<costPerMem>0.05</costPerMem>\n" + 
		    		"		<costPerStorage>0.1</costPerStorage>");
		    node.println("<location>\n" + 
		    		"			<x_pos>500</x_pos>\n" + 
		    		"			<y_pos>500</y_pos>\n" + 
		    		"			<level>5</level>" +
		    		"			<wlan_id>0</wlan_id>\n" + 
		    		"			<attractiveness>1</attractiveness>\n" + 
		    		"			<wap>false</wap>\n" + 
		    		"		</location>");
		    node.println("<hosts>\n" + 
		    		"			<host>\n" + 
		    		"				<core>8</core>\n" + 
		    		"				<mips>4000</mips>\n" + 
		    		"				<ram>8000</ram>\n" + 
		    		"				<storage>200000</storage>\n" + 
		    		"				<VMs>\n" + 
		    		"					<VM vmm=\"Xen\">\n" + 
		    		"						<core>2</core>\n" + 
		    		"						<mips>1000</mips>\n" + 
		    		"						<ram>2000</ram>\n" + 
		    		"						<storage>50000</storage>\n" + 
		    		"					</VM>\n" + 
		    		"					<VM vmm=\"Xen\">\n" + 
		    		"						<core>2</core>\n" + 
		    		"						<mips>1000</mips>\n" + 
		    		"						<ram>2000</ram>\n" + 
		    		"						<storage>50000</storage>\n" + 
		    		"					</VM>\n" + 
		    		"				</VMs>\n" + 
		    		"			</host>\n" + 
		    		"		</hosts>\n" + 
		    		"	</datacenter>");
		    
		    int level;
		    for(int i = 0; i < 4; i++)
		    {
		    	level = 4;
		    	int x = (int) (Math.random()* 250);
		    	int y = (int) (Math.random()* 250);

		    	//Generate random x and y
		    	
		    	//Add node
		    	node.println("<datacenter arch=\"x86\" os=\"Linux\" vmm=\"Xen\">\n");
			    node.println("<costPerBw>0.1</costPerBw>\n" + 
			    		"		<costPerSec>3.0</costPerSec>\n" + 
			    		"		<costPerMem>0.05</costPerMem>\n" + 
			    		"		<costPerStorage>0.1</costPerStorage>");
			    node.println("<location>\n" + 
			    		"			<x_pos>" + x + "</x_pos>\n" + 
			    		"			<y_pos>" + y + "</y_pos>\n" + 
			    		"			<wlan_id>" + counter + "</wlan_id>\n" + 
			    		"			<level>" + level + "</level>" +
			    		"			<attractiveness>1</attractiveness>\n" + 
			    		"			<wap>true</wap>\n" + 
			    		"		</location>");
			    node.println("<hosts>\n" + 
			    		"			<host>\n" + 
			    		"				<core>8</core>\n" + 
			    		"				<mips>4000</mips>\n" + 
			    		"				<ram>8000</ram>\n" + 
			    		"				<storage>200000</storage>\n" + 
			    		"				<VMs>\n" + 
			    		"					<VM vmm=\"Xen\">\n" + 
			    		"						<core>2</core>\n" + 
			    		"						<mips>1000</mips>\n" + 
			    		"						<ram>2000</ram>\n" + 
			    		"						<storage>50000</storage>\n" + 
			    		"					</VM>\n" + 
			    		"					<VM vmm=\"Xen\">\n" + 
			    		"						<core>2</core>\n" + 
			    		"						<mips>1000</mips>\n" + 
			    		"						<ram>2000</ram>\n" + 
			    		"						<storage>50000</storage>\n" + 
			    		"					</VM>\n" + 
			    		"				</VMs>\n" + 
			    		"			</host>\n" + 
			    		"		</hosts>\n" + 
			    		"	</datacenter>");
			    //Add links from this to the cloud
			    links.println("<link>\n" + 
			    		"		<name>L" + i + "_" + 0 + "</name>\n" + 
			    		"		<left>\n" + 
			    		"			<x_pos>" + x + "</x_pos>\n" + 
			    		"			<y_pos>" + y + "</y_pos>\n" + 
			    		"		</left>\n" + 
			    		"		<right>\n" + 
			    		"			<x_pos>500</x_pos>\n" + 
			    		"			<y_pos>500</y_pos>\n" + 
			    		"		</right>\n" + 
			    		"		<left_latency>0.5</left_latency>\n" + 
			    		"		<right_latency>0.5</right_latency>\n" + 
			    		"	</link>");
			    //Add the sub nodes for this
			    counter++;
			    for(int j = 0; j < 3; j++)
			    {
			    	level = 3;
			    	int x2 = (int) (Math.random()* 1000);
			    	int y2 = (int) (Math.random()* 1000);

			    	//Add node
			    	node.println("<datacenter arch=\"x86\" os=\"Linux\" vmm=\"Xen\">\n");
				    node.println("<costPerBw>0.1</costPerBw>\n" + 
				    		"		<costPerSec>3.0</costPerSec>\n" + 
				    		"		<costPerMem>0.05</costPerMem>\n" + 
				    		"		<costPerStorage>0.1</costPerStorage>");
				    node.println("<location>\n" + 
				    		"			<x_pos>" + x2 + "</x_pos>\n" + 
				    		"			<y_pos>" + y2 + "</y_pos>\n" + 
				    		"			<wlan_id>" + counter + "</wlan_id>\n" + 
				    		"			<level>" + level + "</level>" +
				    		"			<attractiveness>1</attractiveness>\n" + 
				    		"			<wap>false</wap>\n" + 
				    		"		</location>");
				    node.println("<hosts>\n" + 
				    		"			<host>\n" + 
				    		"				<core>8</core>\n" + 
				    		"				<mips>4000</mips>\n" + 
				    		"				<ram>8000</ram>\n" + 
				    		"				<storage>200000</storage>\n" + 
				    		"				<VMs>\n" + 
				    		"					<VM vmm=\"Xen\">\n" + 
				    		"						<core>2</core>\n" + 
				    		"						<mips>1000</mips>\n" + 
				    		"						<ram>2000</ram>\n" + 
				    		"						<storage>50000</storage>\n" + 
				    		"					</VM>\n" + 
				    		"					<VM vmm=\"Xen\">\n" + 
				    		"						<core>2</core>\n" + 
				    		"						<mips>1000</mips>\n" + 
				    		"						<ram>2000</ram>\n" + 
				    		"						<storage>50000</storage>\n" + 
				    		"					</VM>\n" + 
				    		"				</VMs>\n" + 
				    		"			</host>\n" + 
				    		"		</hosts>\n" + 
				    		"	</datacenter>");
			    	//Add link to parent
				    links.println("<link>\n" + 
				    		"		<name>L" + j + "_" + i + "</name>\n" + 
				    		"		<left>\n" + 
				    		"			<x_pos>" + x2 + "</x_pos>\n" + 
				    		"			<y_pos>" + y2 + "</y_pos>\n" + 
				    		"		</left>\n" + 
				    		"		<right>\n" + 
				    		"			<x_pos>" + x + "</x_pos>\n" + 
				    		"			<y_pos>" + y + "</y_pos>\n" + 
				    		"		</right>\n" + 
				    		"		<left_latency>0.5</left_latency>\n" + 
				    		"		<right_latency>0.5</right_latency>\n" + 
				    		"	</link>");
			    	//Add the sub nodes
			    	counter++;
			    	for(int k = 0; k < 2; k++)
			    	{
			    		level = 2;
				    	int x3 = (int) (Math.random()* 1000);
				    	int y3 = (int) (Math.random()* 1000);

			    		//Add node
				    	node.println("<datacenter arch=\"x86\" os=\"Linux\" vmm=\"Xen\">\n");
					    node.println("<costPerBw>0.1</costPerBw>\n" + 
					    		"		<costPerSec>3.0</costPerSec>\n" + 
					    		"		<costPerMem>0.05</costPerMem>\n" + 
					    		"		<costPerStorage>0.1</costPerStorage>");
					    node.println("<location>\n" + 
					    		"			<x_pos>" + x3 + "</x_pos>\n" + 
					    		"			<y_pos>" + y3 + "</y_pos>\n" + 
					    		"			<wlan_id>" + counter + "</wlan_id>\n" + 
					    		"			<level>" + level + "</level>" +
					    		"			<attractiveness>1</attractiveness>\n" + 
					    		"			<wap>false</wap>\n" + 
					    		"		</location>");
					    node.println("<hosts>\n" + 
					    		"			<host>\n" + 
					    		"				<core>8</core>\n" + 
					    		"				<mips>4000</mips>\n" + 
					    		"				<ram>8000</ram>\n" + 
					    		"				<storage>200000</storage>\n" + 
					    		"				<VMs>\n" + 
					    		"					<VM vmm=\"Xen\">\n" + 
					    		"						<core>2</core>\n" + 
					    		"						<mips>1000</mips>\n" + 
					    		"						<ram>2000</ram>\n" + 
					    		"						<storage>50000</storage>\n" + 
					    		"					</VM>\n" + 
					    		"					<VM vmm=\"Xen\">\n" + 
					    		"						<core>2</core>\n" + 
					    		"						<mips>1000</mips>\n" + 
					    		"						<ram>2000</ram>\n" + 
					    		"						<storage>50000</storage>\n" + 
					    		"					</VM>\n" + 
					    		"				</VMs>\n" + 
					    		"			</host>\n" + 
					    		"		</hosts>\n" + 
					    		"	</datacenter>");
			    		//Add link to parent
					    links.println("<link>\n" + 
					    		"		<name>L" + k + "_" + j + "</name>\n" + 
					    		"		<left>\n" + 
					    		"			<x_pos>" + x3 + "</x_pos>\n" + 
					    		"			<y_pos>" + y3 + "</y_pos>\n" + 
					    		"		</left>\n" + 
					    		"		<right>\n" + 
					    		"			<x_pos>" + x2 + "</x_pos>\n" + 
					    		"			<y_pos>" + y2 + "</y_pos>\n" + 
					    		"		</right>\n" + 
					    		"		<left_latency>0.5</left_latency>\n" + 
					    		"		<right_latency>0.5</right_latency>\n" + 
					    		"	</link>");
			    		//Add the sub nodes
			    		counter++;
			    		for(int z = 0; z < 3; z++)
			    		{
			    			level =1;
					    	int x4 = (int) (Math.random()* 1000);
					    	int y4 = (int) (Math.random()* 1000);

			    			//Add node 
					    	node.println("<datacenter arch=\"x86\" os=\"Linux\" vmm=\"Xen\">\n");
						    node.println("<costPerBw>0.1</costPerBw>\n" + 
						    		"		<costPerSec>3.0</costPerSec>\n" + 
						    		"		<costPerMem>0.05</costPerMem>\n" + 
						    		"		<costPerStorage>0.1</costPerStorage>");
						    node.println("<location>\n" + 
						    		"			<x_pos>" + x4 + "</x_pos>\n" + 
						    		"			<y_pos>" + y4 + "</y_pos>\n" + 
						    		"			<wlan_id>" + counter + "</wlan_id>\n" + 
						    		"			<level>" + level + "</level>" +
						    		"			<attractiveness>1</attractiveness>\n" + 
						    		"			<wap>false</wap>\n" + 
						    		"		</location>");
						    node.println("<hosts>\n" + 
						    		"			<host>\n" + 
						    		"				<core>8</core>\n" + 
						    		"				<mips>4000</mips>\n" + 
						    		"				<ram>8000</ram>\n" + 
						    		"				<storage>200000</storage>\n" + 
						    		"				<VMs>\n" + 
						    		"					<VM vmm=\"Xen\">\n" + 
						    		"						<core>2</core>\n" + 
						    		"						<mips>1000</mips>\n" + 
						    		"						<ram>2000</ram>\n" + 
						    		"						<storage>50000</storage>\n" + 
						    		"					</VM>\n" + 
						    		"					<VM vmm=\"Xen\">\n" + 
						    		"						<core>2</core>\n" + 
						    		"						<mips>1000</mips>\n" + 
						    		"						<ram>2000</ram>\n" + 
						    		"						<storage>50000</storage>\n" + 
						    		"					</VM>\n" + 
						    		"				</VMs>\n" + 
						    		"			</host>\n" + 
						    		"		</hosts>\n" + 
						    		"	</datacenter>");
			    			//Add link to parent
						    links.println("<link>\n" + 
						    		"		<name>L" + z + "_" + k + "</name>\n" + 
						    		"		<left>\n" + 
						    		"			<x_pos>" + x4 + "</x_pos>\n" + 
						    		"			<y_pos>" + y4 + "</y_pos>\n" + 
						    		"		</left>\n" + 
						    		"		<right>\n" + 
						    		"			<x_pos>" + x3 + "</x_pos>\n" + 
						    		"			<y_pos>" + y3 + "</y_pos>\n" + 
						    		"		</right>\n" + 
						    		"		<left_latency>0.5</left_latency>\n" + 
						    		"		<right_latency>0.5</right_latency>\n" + 
						    		"	</link>");
						    //Add subnodes
					    	counter++;
					    	for(int v = 0; v < 3; v++)
				    		{
				    			level = 0;
						    	int x5 = (int) (Math.random()* 1000);
						    	int y5 = (int) (Math.random()* 1000);

				    			//Add node 
						    	node.println("<datacenter arch=\"x86\" os=\"Linux\" vmm=\"Xen\">\n");
							    node.println("<costPerBw>0.1</costPerBw>\n" + 
							    		"		<costPerSec>3.0</costPerSec>\n" + 
							    		"		<costPerMem>0.05</costPerMem>\n" + 
							    		"		<costPerStorage>0.1</costPerStorage>");
							    node.println("<location>\n" + 
							    		"			<x_pos>" + x5 + "</x_pos>\n" + 
							    		"			<y_pos>" + y5 + "</y_pos>\n" + 
							    		"			<wlan_id>" + counter + "</wlan_id>\n" + 
							    		"			<level>" + level + "</level>\n" +
							    		"			<attractiveness>3</attractiveness>\n" + 
							    		"			<wap>true</wap>\n" + 
							    		"		</location>");
							    node.println("<hosts>\n" + 
							    		"			<host>\n" + 
							    		"				<core>8</core>\n" + 
							    		"				<mips>4000</mips>\n" + 
							    		"				<ram>8000</ram>\n" + 
							    		"				<storage>200000</storage>\n" + 
							    		"				<VMs>\n" + 
							    		"					<VM vmm=\"Xen\">\n" + 
							    		"						<core>2</core>\n" + 
							    		"						<mips>1000</mips>\n" + 
							    		"						<ram>2000</ram>\n" + 
							    		"						<storage>50000</storage>\n" + 
							    		"					</VM>\n" + 
							    		"					<VM vmm=\"Xen\">\n" + 
							    		"						<core>2</core>\n" + 
							    		"						<mips>1000</mips>\n" + 
							    		"						<ram>2000</ram>\n" + 
							    		"						<storage>50000</storage>\n" + 
							    		"					</VM>\n" + 
							    		"				</VMs>\n" + 
							    		"			</host>\n" + 
							    		"		</hosts>\n" + 
							    		"	</datacenter>");
				    			//Add link to parent
							    links.println("<link>\n" + 
							    		"		<name>L" + v + "_" + z + "</name>\n" + 
							    		"		<left>\n" + 
							    		"			<x_pos>" + x5 + "</x_pos>\n" + 
							    		"			<y_pos>" + y5 + "</y_pos>\n" + 
							    		"		</left>\n" + 
							    		"		<right>\n" + 
							    		"			<x_pos>" + x4 + "</x_pos>\n" + 
							    		"			<y_pos>" + y4 + "</y_pos>\n" + 
							    		"		</right>\n" + 
							    		"		<left_latency>0.5</left_latency>\n" + 
							    		"		<right_latency>0.5</right_latency>\n" + 
							    		"	</link>");
						    	counter++;
				    		}
					    	
			    		}
			    	}
			    }
		    }
		    
		    
		    
		    
		    links.println("</links>");
		    node.println("</edge_devices>");
		    links.close();
		    node.close();
		} catch (Exception e) {
		   System.out.println("Files were not able to be made");
		}
		
		
		/*
		 * Try to get FogHierClust.java to run
		 * 
		 */
		
		
		
		Log.disable();
		//enable console output and file output of this application
		SimLogger.enablePrintLog();
		

		//CJ added linksFile to supply the link xml file, had to adjust all constructors that
		//	use these file to seamlessly use it
		int iterationNumber = 1;
		String configFile = "";
		String outputFolder = "";
		String edgeDevicesFile = "";
		String applicationsFile = "";
		//String linksFile = "scripts/sample_application/config/links_test.xml";
		String linksFile = "large_links_test.xml";
		if (args.length == 5){
			configFile = args[0];
			edgeDevicesFile = args[1];
			applicationsFile = args[2];
			outputFolder = args[3];
			iterationNumber = Integer.parseInt(args[4]);
		}
		else{
			SimLogger.printLine("Simulation setting file, output folder and iteration number are not provided! Using default ones...");
			configFile = "scripts/sample_application/config/default_config.properties";
			applicationsFile = "scripts/sample_application/config/applications.xml";
			//edgeDevicesFile = "scripts/sample_application/config/edge_devices_test.xml";
			edgeDevicesFile = "large_node_test.xml";
			outputFolder = "sim_results/ite" + iterationNumber;
		}

		//load settings from configuration file
		SimSettings SS = SimSettings.getInstance();
		if(SS.initialize(configFile, edgeDevicesFile, applicationsFile, linksFile) == false){
			SimLogger.printLine("cannot initialize simulation settings!");
			System.exit(0);
		}
		
		if(SS.getFileLoggingEnabled()){
			SimLogger.enableFileLog();
			SimUtils.cleanOutputFolder(outputFolder);
		}

		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date SimulationStartDate = Calendar.getInstance().getTime();
		String now = df.format(SimulationStartDate);
		SimLogger.printLine("Simulation started at " + now);
		SimLogger.printLine("----------------------------------------------------------------------");

		for(int j=SS.getMinNumOfMobileDev(); j<=SS.getMaxNumOfMobileDev(); j+=SS.getMobileDevCounterSize())
		{
			for(int k=0; k<SS.getSimulationScenarios().length; k++)
			{
				for(int i=0; i<SS.getOrchestratorPolicies().length; i++)
				{
					String simScenario = SS.getSimulationScenarios()[k];
					String orchestratorPolicy = SS.getOrchestratorPolicies()[i];
					Date ScenarioStartDate = Calendar.getInstance().getTime();
					now = df.format(ScenarioStartDate);
					
					SimLogger.printLine("Scenario started at " + now);
					SimLogger.printLine("Scenario: " + simScenario + " - Policy: " + orchestratorPolicy + " - #iteration: " + iterationNumber);
					SimLogger.printLine("Duration: " + SS.getSimulationTime()/3600 + " hour(s) - Poisson: " + SS.getTaskLookUpTable()[0][2] + " - #devices: " + j);
					SimLogger.getInstance().simStarted(outputFolder,"SIMRESULT_" + simScenario + "_"  + orchestratorPolicy + "_" + j + "DEVICES");
					
					try
					{
						// First step: Initialize the CloudSim package. It should be called
						// before creating any entities.
						int num_user = 2;   // number of grid users
						Calendar calendar = Calendar.getInstance();
						boolean trace_flag = false;  // mean trace events
				
						// Initialize the CloudSim library
						CloudSim.init(num_user, calendar, trace_flag, 0.01);
						SimLogger.printLine("CloudSim.init reached");
						// Generate EdgeCloudsim Scenario Factory
						ScenarioFactory sampleFactory = new SampleScenarioFactory(j,SS.getSimulationTime(), orchestratorPolicy, simScenario);
						SimLogger.printLine("ScenarioFactory reached");
						// Generate EdgeCloudSim Simulation Manager
						SimManager manager = new SimManager(sampleFactory, j, simScenario);
						SimLogger.printLine("SimManager reached");
						// Start simulation
						manager.startSimulation();
					}
					catch (Exception e)
					{
						SimLogger.printLine("The simulation has been terminated due to an unexpected error");
						e.printStackTrace();
						System.exit(0);
					}
					
					Date ScenarioEndDate = Calendar.getInstance().getTime();
					now = df.format(ScenarioEndDate);
					SimLogger.printLine("Scenario finished at " + now +  ". It took " + SimUtils.getTimeDifference(ScenarioStartDate,ScenarioEndDate));
					SimLogger.printLine("----------------------------------------------------------------------");
				}//End of orchestrators loop
			}//End of scenarios loop
		}//End of mobile devices loop

		Date SimulationEndDate = Calendar.getInstance().getTime();
		now = df.format(SimulationEndDate);
		SimLogger.printLine("Simulation finished at " + now +  ". It took " + SimUtils.getTimeDifference(SimulationStartDate,SimulationEndDate));
	}
}
