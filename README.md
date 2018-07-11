# pFogSim : A Simulator For Evaluating Dynamic and Layered Fog-Computing Environments
This is still a work in progress as of 7/11/2018

## **What is pFogSim?**

 - pFogSim (/p/-fôg-/sɪm/) is a play off iFogSim (another popular simulator built on CloudSim)
	- **p** is for **P**uddles, the HAFA (Hierarchical Autonomous Fog Architecture) representation of Fog Networks found here (insert link)
	- **Fog** is from i**Fog**Sim (insert link) since it provided a lot of inspiration for this project
	- **Sim** is from EdgeCloud**Sim** (insert link) since it provides a significant back-bone to make the project off of
	- All of these are from the popular CloudSim (insert link)
 - A simulator made to handle large-scale FOG networks with the HAFA Puddle Strategy to help evaluate the potential advantages/disadvantages within user-customizable scenarios
 - Simulator is still in progress but what is seen here should already be present and tested in the simulator

## **Quick Summary**
 - General Outline of Classes
[Class Diagram]()
 - Details given below

## **How to Run** 
(May have to change some of the files mentioned to tailor for your stuff)
 - 2 ways: 
	- Scripts (Linux stuff) (Not too different from EdgeCloudSim)
		- tail output files
	- mainApp (IDEs)
 - What you should have:
	- Customization Files:
		- Network Topology (Node/Link XML Files)
			- I hope you have scripts or something available to make it faster for you
		- Applications
		- General Config files

General: 
Link to main

DataInterpreter -> EdgeServerManager -> VectorMobility -> NetworkTopology -> Clustering -> Puddles -> SimManager -> SimLogger

DataInterpreter:
 - Hard-coded
 - Made to take CSV files from City of Chicago and translate to XML 
 - Need to change if want to make any large files
 - Defines the MIN/MAX space of simulation (So mobile devices don't leave the simulation space)
 - Describe our network
  
EdgeServerManager:
 - Reads links and nodes XML files -> Creates respective objects
 - Constructs network topology 
 
VectorMobility:
 - Creates each mobile device starting at a random wireless access point (WAP)
 - Moves them according to random vectors that have been approximated to be around walking speed of 5km/hr
 - Creates all of the mobile devices and all of their positions throughout the entire simulator
 - Also updates which WAP connected to based on proximity

NetworkTopology:
 - Defines network and has all static links in network
 - Links don't actually have to be static:
 - FogNodes may move
 	- FogNodes may be removed
	- All will update in SimManager
 - Lets clustering be created
 
Clustering:
 - Goes through each level and clusters local nodes together to allow for local nodes to share puddles
 - Hierarchical Clustering Algorithm
 - Creates ability for Puddles
 
Puddles:
 - Takes local puddles and attaches pieces to each other
 
SimManager: 
 - Creates all the tasks
 - Schedules the tasks that are waiting
 - Can update network topology -> clustering -> puddles if changes occur
	- Moving FogNodes or FogNode removal can be implemented here
 - Schedules end of simulation -> Lots -> SimLogger
 
SimLogger: 
 - Prints all of the information to output files/console
 - Info gets stored here throughout simulation and gets executed here
