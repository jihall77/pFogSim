package edu.auburn.pFogSim.Exceptions;

import edu.auburn.pFogSim.netsim.NodeSim;

public class BlackHoleException extends RuntimeException {
	public NodeSim dest;
	public NodeSim src;
	
	public BlackHoleException(NodeSim _src, NodeSim _dest) {
		dest = _dest;
		src = _src;
	}
}
