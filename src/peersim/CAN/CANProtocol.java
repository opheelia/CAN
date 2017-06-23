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
 * Attributes
 * ---------------------------------------------------------------------
 */
	
	//Protocol attributes
	public static CANInterval[] fullZone;
	private static String prefix = null;
	private static final String PAR_TRANSPORT = "transport";
	private static final String PAR_ENERGY = "energy";	
	private static final String PAR_ZONE = "zone";	
	private static boolean ALREADY_INSTALLED = false;
	
	//Node attributes
	private long nodeID;
	private CANInterval[] nodeZone;
	private Hashtable neighbors;
	private long energy;
	
    
/*----------------------------------------------------------------------
 * Builder and initialization
 * ---------------------------------------------------------------------
 */    
    
	public CANProtocol(String prefix){
		System.out.println("CANProtocol : Builder");
		CANProtocol.prefix = prefix;
		Random rnd = new Random();
		init();
		this.neighbors = new Hashtable();
		this.nodeZone = new CANInterval[2];
		long energymax = Configuration.getLong(prefix + "." + PAR_ENERGY, energy);
		this.energy = (long)(rnd.nextDouble()*energymax);
		//System.out.println("CANProtocol : New node energy = "+this.energy);
		
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
		//System.out.println("randomP : x="+p.getA()+", y="+p.getB());
		return p;
	}
	
	public Node whoHasP(CANInterval p){
		for(int i=0; i < Network.size();i++){
			CANInterval[] thisNodeZone = getCAN(Network.get(i)).getNodeZone();
			System.out.println("whoHasP : thisNodeZone = ["+thisNodeZone[0]+" , "+thisNodeZone[1]+"], p="+p.getA()+","+p.getB());
			if(p.belongsToZone(thisNodeZone[0],thisNodeZone[1])){
				return (Network.get(i));
			}
		}
		System.out.println("whoHasP : nobody has P, problem !!");
		return null;
	}
	
	public void giveNewID(Node n){
		Random rnd = new Random();
		long id = (long)(rnd.nextDouble()*100000);
		System.out.println("giveNewID : ID = "+id+", let's see if it's not already given");
		if(id<0) id=-id;
		for(int i=0; i < Network.size();i++){
			if(i!=n.getIndex()){
				long friendID = ((CANProtocol)((Network.get(i)).getProtocol(0))).getNodeID();
				System.out.println("Comparison : "+id+" and "+friendID);
				if(id == friendID){
					id = (long)(rnd.nextDouble()*100000);
					if(id<0) id=-id;
					//i=0;
				}
			}
		}
		setNodeID(id);
	}
	
	public static Node bootstrapNode(){
		if(Network.size()<2) return null;
		int r = CommonState.r.nextInt(Network.size());
		return (Node)(Network.get(r));
	}
	
	public static CANMessage joinMessage(Node newcomer, Node bootstrap){
		long id;
		
		//do {
			//bootstrap = bootstrapNode();
		//} while(id == getCAN(newcomer).getNodeID());
		CANMessage m = new CANMessage(newcomer, bootstrap, CANMessage.JOIN);
		return m;
	}
	
/**.....................................................................
 * Message methods
 * ...................................................................*/

	public void sendMessage(CANMessage m){
		System.out.println("*****************************************");
		System.out.println("sendMessage : node n°"+((CANProtocol)(m.getSender().getProtocol(0))).nodeID+" sends message (type="+m.getType()+") to node n°"+((CANProtocol)(m.getReceiver().getProtocol(0))).nodeID);
		System.out.println("*****************************************");
		//((CANProtocol)(m.sender.getProtocol(0))).receiveMessage(m);
	}
	
	public void receiveMessage(CANMessage m){
		System.out.println("******************* RECEIPT MESSAGE *************************");
		System.out.println("receiveMessage : node n°"+((CANProtocol)(m.getReceiver().getProtocol(0))).nodeID+" receives message (type="+m.getType()+") from node n°"+((CANProtocol)(m.getSender().getProtocol(0))).nodeID);
		System.out.println("*************************************************************");
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
	
	public void recoverZone(Node newcomer, Node leaver){
		CANProtocol pNew = getCAN(newcomer);
		CANProtocol pOld = getCAN(leaver);
		CANInterval[] theZone = pOld.getNodeZone();
		pNew.setNodeZone(theZone[0],theZone[1]);
		System.out.println("**********ZONE RECOVERING***********");
	}
	
	public Node whoHasLowEnergy(int notThisOne){
		for(int i=1;i<Network.size();i++){
			if(((getCAN(Network.get(i)).getEnergy()) < 5) && ((Network.get(i)).getIndex() != notThisOne) ){
				return (Network.get(i));
			}
		}
		return null;
	}
		
	//awful way to do that but no time to improve
	public CANInterval[] shareZone(CANInterval i1, CANInterval i2, CANInterval p){
		
		System.out.println("shareZone : at the beginning, i1="+i1.toString()+", i2="+i2.toString()+", p="+p.toString());
		
		CANInterval[] toReturn = new CANInterval[4];
		CANInterval[] newNodeZone = new CANInterval[2];
		CANInterval[] oldNodeZone = new CANInterval[2];
		float average;
		if((i1.getB() - i1.getA()) >= (i2.getB() - i2.getA())){ // if X width is bigger than Y one
			newNodeZone[1]=i2;
			oldNodeZone[1]=i2;
			average = (i1.getA() + i1.getB())/2;
			if(p.getA() <= average){
				newNodeZone[0]=new CANInterval(i1.getA(), average);
				oldNodeZone[0]=new CANInterval(average, i1.getB());
			} else {
				newNodeZone[0]=new CANInterval(average, i1.getB());
				oldNodeZone[0]=new CANInterval(i1.getA(),average);
			}
		} 
		else 
		{ //if Y width is bigger than X one
			newNodeZone[0]=i1;
			oldNodeZone[0]=i1;
			average = (i2.getA() + i2.getB())/2;
			if(p.getB() <= average) {
				newNodeZone[1]=new CANInterval(i2.getA(), average);
				oldNodeZone[1]=new CANInterval(average, i2.getB());
				//System.out.println("P at the left : i1="+i1.toString()+", i2="+i2.toString());
			} else {
				newNodeZone[1]=new CANInterval(average, i2.getB());
				oldNodeZone[1]=new CANInterval(i2.getA(), average);
				//System.out.println("P at the right : i1="+i1.toString()+", i2="+i2.toString());
			}
		}
		System.out.println("New node zone : [ "+newNodeZone[0].toString()+" , "+newNodeZone[1].toString()+" ]");
		System.out.println("Old node zone : [ "+oldNodeZone[0].toString()+" , "+oldNodeZone[1].toString()+" ]");
		toReturn[0]=newNodeZone[0];
		toReturn[1]=newNodeZone[1];
		toReturn[2]=oldNodeZone[0];
		toReturn[3]=oldNodeZone[1];
		return toReturn;
	}
	
/**.....................................................................
 * Event method
 * ...................................................................*/
 
	public void processEvent(Node node, int protocolID, Object event){
		if((event instanceof CANMessage) && (protocolID==0)){
			CANMessage m = (CANMessage) event;
			this.receiveMessage(m);
			switch (m.getType()) {
				case CANMessage.JOIN:
					System.out.println("");
					System.out.println("***************  processEvent : JOIN message  **************************");

					boolean replace = false;

					Node leaver = whoHasLowEnergy((m.getSender()).getIndex());
					if(leaver != null){
						replace = true;
						recoverZone(m.getSender(),leaver);
						Network.remove(leaver.getIndex());
						System.out.println("Node "+(getCAN(m.getSender())).getNodeID()+" takes node "+getCAN(leaver).getNodeID()+" zone (his energy was "+getCAN(leaver).getEnergy()+"), who is now DEAD");
						System.out.println("*****************************************");
					}
					
					if(replace == false){
						CANInterval p = randomP();
						Node pOwner = whoHasP(p);
						CANMessage shareMessage = new CANMessage(m.getSender(),pOwner,CANMessage.SHARE);
						shareMessage.setBody(p);
						EDSimulator.add(0, shareMessage, pOwner, protocolID);
					}					
					System.out.println("");
					
					break;
				case CANMessage.SHARE:
					System.out.println("");
					System.out.println("******************* processEvent : SHARE message ************************");
					CANInterval p = (CANInterval)m.getBody();
					CANInterval[] newZone = this.shareZone((this.getNodeZone())[0], (this.getNodeZone())[1], p);
					this.setNodeZone(newZone[2],newZone[3]);
					CANMessage answer = new CANMessage(m.getReceiver(),m.getSender(),CANMessage.SHARE_ANS);
					answer.setBody(newZone);
					EDSimulator.add(0,answer,m.getSender(),protocolID);
					System.out.println("");
					break;
				case CANMessage.SHARE_ANS:
					System.out.println("");
					System.out.println("**************** processEvent : SHARE ANSWER message *********************");
					CANInterval[] zone = (CANInterval[])m.getBody();
					System.out.println("New zone : ["+zone[0].toString()+" , "+zone[1].toString()+"]");
					this.setNodeZone(zone[0],zone[1]);
					System.out.println("");
			}
        }
	}
	
	
    
/*----------------------------------------------------------------------
 * Getter and setter
 * ---------------------------------------------------------------------
 */
 
	public long getNodeID(){
		return this.nodeID;
	}
	public void setNodeID(long newID){
		System.out.println("ID "+newID+" given");
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
	
	public static CANInterval[] getFullZone(){
		return fullZone;
	}
	
	public Hashtable getNeighbors() {
        return this.neighbors;
    }
    
    public long getEnergy(){
		return this.energy;
	}
	
	public static CANProtocol getCAN(Node n) {
        return ((CANProtocol) n.getProtocol(0));
    }
    
    
	
}

