/*
 * CANProtocol.java
 * 
 * Copyright 2017 Ophelia <ophelia@ophelia-VirtualBox>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;
import java.util.*;


public class CANProtocol implements EDProtocol, Cloneable {
	
/*----------------------------------------------------------------------
 * Attributs
 * ---------------------------------------------------------------------
 */
	
	//Attributs propres au protocole
	private int CANID;
	private static String prefix = null;
	
	//Attributs propres aux noeuds
	private long nodeID;
	private CANInterval[] zone;
	private Hashtable<long,Node> neighbors; //IP remplacé par ID dans Peersim, zone accessible dans Node
	
	public Object clone(){
		System.out.println("CANProtocol : Clonage");
        CANProtocol dolly = new CANProtocol(CANProtocol.prefix);
        dolly.zone = (CANInterval[])this.zone.clone();
        dolly.neighbors = (Hashtable<long,Node>)this.neighbors.clone();
        return dolly;
    }
    
/*----------------------------------------------------------------------
 * Constructeur
 * ---------------------------------------------------------------------
 */    
    
	public CANProtocol(String prefix){
		System.out.println("CANProtocol : Constructeur");
		this.nodeID = -1;
		this.neighbors = new Hashtable<long,Node>();
		this.zone = new CANInterval[2];
		CANProtocol.prefix = prefix;
	}
	
/*----------------------------------------------------------------------
 * Méthodes
 * ---------------------------------------------------------------------
 */
	
	public void join(){}
	
	public boolean isNeighbor(Node n){
		System.out.println("CANProtocol : Test isNeighbor");
		CANProtocol p = getCAN(n);
		if (((this.zone[0].isOverlapped(p.zone[0]))&&(this.zone[1].isAbutted(p.zone[1]))) || ((this.zone[1].isOverlapped(p.zone[0]))&&(this.zone[0].isAbutted(p.zone[1])))){
			return true;
		} else {
			return false;
		}
	}
	
	public void addNeighbor(Node n){
		System.out.println("CANProtocol : addNeighbor");
		long nID = getCAN(n).getNodeID;
		if(this.neighbors.containsKey(nID)){
			System.out.println("CANProtocol : addNeighbor : this node is already a neighbor");
		} else {
			this.neighbors.put(nID,n);
			System.out.println("CANProtocol : addNeighbor : new neighbor added");
		}
	}
	
	public void showNeighbors(){
		System.out.println("CANProtocol : showNeighbors");
		Enumeration elements = this.neighbors.elements();
		while(elements.hasMoreElements())
			System.out.println(elements.nextElement());
	}
	
	public void processEvent(Node node, int protocolID, Object event){}
	
	
    
/*----------------------------------------------------------------------
 * Getters et setters
 * ---------------------------------------------------------------------
 */
 
	public long getNodeID(){
		return this.nodeID;
	}
	public void setNodeID(long newID){
		System.out.println("ID "+newID+" attribué");
		this.nodeID = newID;
	}
	
	public CANInterval[] getZone(){
		return this.zone;
	}
	public void setZone(float x1, float x2, float y1, float y2){
		this.zone[0] = new CANInterval(x1,x2);
		this.zone[1] = new CANInterval(y1,y2);
	}
	
	public void setZone(CANInterval i1, CANInterval i2){
		this.zone[0]=i1;
		this.zone[1]=i2;
	}
	
	public Hashtable<long,Node> getNeighbors() {
        return this.neighbors;
    }
	
	// Récupérer le protocole CAN d'un noeud
	public final CANProtocol getCAN(Node n) {
		int index = n.getIndex();
        return ((CANProtocol) (Network.get(index)).getProtocol(CANID));
    }
    
    
	
}

