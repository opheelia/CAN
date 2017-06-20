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
	private final int CANID;
	public static CANInterval[] fullZone;
	private static String prefix = null;
	private static final String PAR_TRANSPORT = "transport";
	private static final String PAR_ENERGY = "energy";	
	private static final String PAR_ZONE = "zone";	
	private static boolean ALREADY_INSTALLED = false;
	
	//Attributs propres aux noeuds
	private long nodeID;
	private CANInterval[] nodeZone;
	private Hashtable neighbors; //IP remplacé par ID dans Peersim, zone accessible dans Node
	private long energy;
	
    
/*----------------------------------------------------------------------
 * Constructeur
 * ---------------------------------------------------------------------
 */    
    
	public CANProtocol(String prefix){
		System.out.println("CANProtocol : Constructeur");
		CANProtocol.prefix = prefix;
		Random rnd = new Random();
		CANID = rnd.nextInt();
		init();
		this.neighbors = new Hashtable();
		this.nodeZone = new CANInterval[2];
		this.energy = Configuration.getLong(prefix + "." + PAR_ENERGY, energy);
	}
	
	private void init() {
        if (ALREADY_INSTALLED) return;
        float coord = (float)Configuration.getInt(prefix + "." + PAR_ZONE);
        CANInterval vector = new CANInterval(0,coord);
        fullZone = new CANInterval[2];
        for(int i=0;i<2;i++){
			fullZone[i]=vector;
		}
        System.out.println("Zone initialisée : "+ vector.toString() +"x"+vector.toString());
        ALREADY_INSTALLED = true;
    }
    
    public Object clone(){
		System.out.println("CANProtocol : Clonage");
        CANProtocol dolly = new CANProtocol(CANProtocol.prefix);
        dolly.nodeZone = (CANInterval[])this.nodeZone.clone();
        dolly.neighbors = (Hashtable)this.neighbors.clone();
        return dolly;
    }
	
/*----------------------------------------------------------------------
 * Methods
 * ---------------------------------------------------------------------
 */
	
/** ....................................................................
 * Insert methods
 * ..................................................................**/	
	
	public static CANInterval randomP(){
		Random rnd = new Random();
		float max = (float)Configuration.getInt(prefix + "." + PAR_ZONE);
		float x = (float)(rnd.nextDouble()*max);
		float y = (float)(rnd.nextDouble()*max);
		CANInterval p = new CANInterval(x,y);
		System.out.println("randomP : x="+x+", y="+y);
		return p;
	}
	
	public Node whoHasP(CANInterval p){
		for(int i=0; i < Network.size();i++){
			CANInterval[] thisNodeZone = ((CANProtocol)((Network.get(i)).getProtocol(0))).getNodeZone();
			if(p.belongsToZone(thisNodeZone[0],thisNodeZone[1])){
				return (Network.get(i));
			}
		}
		System.out.println("whoHasP : nobody has P, problem !!");
		return null;
	}
	
	public static Node bootstrapNode(){
		if(Network.size()==0) return null;
		int r = CommonState.r.nextInt(Network.size());
		return (Node)(Network.get(r));
	}
	
	public void giveNewID(Node n){
		Random rnd = new Random();
		long id = (long)(rnd.nextDouble()*10000);
		System.out.println("giveNewID : ID = "+id);
		if(id<0) id=-id;
		for(int i=0; i < Network.size();i++){
			if(i!=n.getIndex()){
				System.out.println("i= "+i+" , index de n = "+n.getIndex());
				long friendID = ((CANProtocol)((Network.get(i)).getProtocol(0))).getNodeID();
				System.out.println("Comparaison : "+id+" et "+friendID);
				if(id == friendID){
					id = (long)(rnd.nextDouble()*10000);
					if(id<0) id=-id;
					//i=0;
				}
			}
		}
		setNodeID(id);
	}
	
	public static CANMessage joinMessage(Node newcomer){
		Node bootstrap = bootstrapNode();
		CANMessage m = new CANMessage(newcomer, bootstrap, 1);
		return m;
	}
	
/**.....................................................................
 * Routings methods
 * ...................................................................*/

	//Here we suppose that m type is JOIN
	public void sendMessage(CANMessage m){
		System.out.println("*****************************************");
		System.out.println("sendMessage : node n°"+(CANProtocol)(m.sender.getProtocol(0)).nodeID+" sends JOIN message to node n°"+(CANProtocol)(m.receiver.getProtocol(0)).nodeID);
		System.out.println("*****************************************");
		(CANProtocol)(m.sender.getProtocol(0)).receiveMessage(m);
	}
	
	public void receiveMessage(CANMessage m){
		System.out.println("receiveMessage : node n°"+(CANProtocol)(m.receiver.getProtocol(0)).nodeID+" receives JOIN message from node n°"+(CANProtocol)(m.sender.getProtocol(0)).nodeID);
		System.out.println("*****************************************");
	}
		

/**...................................................................
 * Neighborhood methods
 * ..................................................................*/
	
	public boolean isNeighbor(Node n){
		System.out.println("CANProtocol : Test isNeighbor");
		CANProtocol p = getCAN(n);
		if (((this.nodeZone[0].isOverlapped(p.nodeZone[0]))&&(this.nodeZone[1].isAbutted(p.nodeZone[1]))) || ((this.nodeZone[1].isOverlapped(p.nodeZone[0]))&&(this.nodeZone[0].isAbutted(p.nodeZone[1])))){
			return true;
		} else {
			return false;
		}
	}
	
	public void addNeighbor(Node n){
		System.out.println("CANProtocol : addNeighbor");
		long nID = getCAN(n).getNodeID();
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
	
	
	public void processEvent(Node node, int protocolID, Object event){
		System.out.println("CANProtocol : processEvent");
		if((event instanceof CANMessage) && (protocolID==CANID)){
			CANMessage m = (CANMessage) event;
			switch (m.getType()) {
				case CANMessage.JOIN:
					System.out.println("CANProtocol : JOIN message");
					//this.join(node);
					break;
				case CANMessage.LOOKUP:
					System.out.println("CANProtocol : LOOKUP message");
					//route()
					break;
				case CANMessage.UPDATE:
					System.out.println("CANProtocol : UPDATE message");
					break;
				case CANMessage.TAKEOVER:
					System.out.println("CANProtocol : TAKEOVER message");
					break;
			}
        }
	}
	
	
    
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
	
	public CANInterval[] getNodeZone(){
		return this.nodeZone;
	}
	public void setNodeZone(float x1, float x2, float y1, float y2){
		this.nodeZone[0] = new CANInterval(x1,x2);
		this.nodeZone[1] = new CANInterval(y1,y2);
	}
	
	public void setNodeZone(CANInterval i1, CANInterval i2){
		this.nodeZone[0]=i1;
		this.nodeZone[1]=i2;
	}
	
	public Hashtable getNeighbors() {
        return this.neighbors;
    }
	
	public final CANProtocol getCAN(Node n) {
		int index = n.getIndex();
        return ((CANProtocol) (Network.get(index)).getProtocol(CANID));
    }
    
    
	
}

