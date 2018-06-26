
package edu.boun.edgecloudsim.utils;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.auburn.pFogSim.Puddle.Puddle;
import edu.auburn.pFogSim.netsim.*;

//This is testing for a visualization of NetworkTopology
public class LinesComponent extends JComponent{

	public LinesComponent()
	{
		drawNetworkTopology();
	}
		
	private class Line{
	    final double x1; 
	    final double y1;
	    final double x2;
	    final double y2;   
	    final Color color;
	
	    public Line(double x12, double x22, double x3, double x4, Color color) {
	        this.x1 = x12;
	        this.y1 = x22;
	        this.x2 = x3;
	        this.y2 = x4;
	        this.color = color;
	    }               
	}

	private final LinkedList<Line> lines = new LinkedList<Line>();
	public void addLine(double x1, double x2, double x3, double x4) {
	    addLine(x1, x2, x3, x4, Color.black);
	}
	public void addLine(double x1, double x2, double x3, double x4, Color color) {
	    lines.add(new Line(x1,x2,x3,x4, color));        
	    repaint();
	}

	public void clearLines() {
	    lines.clear();
	    repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    for (Line line : lines) {
	        g.setColor(line.color);
	        g.drawLine((int)line.x1, (int)line.y1, (int)line.x2, (int)line.y2);
	    }
	}

	public void drawNetworkTopology() {
	    JFrame testFrame = new JFrame();
	    testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    final LinesComponent comp = new LinesComponent();
	    comp.setPreferredSize(new Dimension(1000, 1000));
	    testFrame.getContentPane().add(comp, BorderLayout.CENTER);
		    
	    //Go through layer and find all heads while adding their constituents 
		    
	    NetworkTopology network = ((MM1Queue) SimManager.getInstance().getNetworkModel()).getNetworkTopology();
	    network.getPuddles();
	    double hx, hy, cx, cy;
	    for(int level = 1; level < 6; level++)
	    {
		    for(Puddle pud : network.getPuddles())
		    {
		    	if(pud.getLevel() == level)
		    	{
			    	hx = pud.getHead().getLocation().getXPos();
			    	hy = pud.getHead().getLocation().getYPos();
			    	for(EdgeHost child : pud.getMembers())
				   	{
				   		cx = child.getLocation().getXPos();
				   		cy = child.getLocation().getYPos();
				   		
				   		//Draw from head to child
				   		addLine(hx, hy, cx, cy);
				   		
				   	}
			    }
			}
		}

	    testFrame.pack();
	    testFrame.setVisible(true);
	}
}