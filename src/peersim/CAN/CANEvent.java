/*
 * CANEvent.java
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
import peersim.core.Network;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;
import java.util.*;
import java.math.*;

public class CANEvent implements Control{
	
/*----------------------------------------------------------------------
 * Attributes
 * ---------------------------------------------------------------------
 */
	
	private final static String PAR_PROT = "protocol";
	private final int protocolID;
	
/*----------------------------------------------------------------------
 * Builder
 * ---------------------------------------------------------------------
 */
	
	public CANEvent (String prefix){
		protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
		System.out.println("CANEvent : protocol ID = "+protocolID);
	}
	
/*----------------------------------------------------------------------
 * Methods
 * ---------------------------------------------------------------------
 */
	
	public boolean execute(){
		System.out.println("CANEvent : execute");
		//affiche l'état du réseau
		createNewNode();
        return false;
	}
	
	public void createNewNode(){
		Node newcomer = (Node)Network.prototype.clone(); 
		Node bootstrap = Network.get(0);
		Network.add(newcomer);
		int index = Network.size();
		//System.out.println("CANEvent : index = "+index);
		((CANProtocol)(newcomer.getProtocol(protocolID))).giveNewID(newcomer);
		CANMessage m = CANProtocol.joinMessage(newcomer,bootstrap);
        EDSimulator.add(0, m, bootstrap, protocolID);
        System.out.println("CANEvent : Message JOIN added in queue");
    }
	
	
	
	
		
	
}

