/*
 * CANMessage.java
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

public class CANMessage {
	
/*----------------------------------------------------------------------
 * Attributes
 * ---------------------------------------------------------------------
 */	
 
	private long messID;
	private int type;
	private Node sender;
	private Node receiver;
	private Object body; 
	
	private static Random rnd;
	private boolean already_init = false;
	
	public static final int JOIN = 0;
	public static final int SHARE = 1;
	public static final int SHARE_ANS = 2;

	
/*----------------------------------------------------------------------
 * Builder
 * ---------------------------------------------------------------------
 */  
 
	public CANMessage(Node s, Node r, int type){
		this.sender = s;
		this.receiver = r;
		this.type = type;
		if(already_init=false){
			rnd = new Random();
			already_init=true;
		}
		this.messID = 0;
	}
	
	
/*----------------------------------------------------------------------
 * Methods
 * ---------------------------------------------------------------------
 */
 
	
		
	
/*----------------------------------------------------------------------
 * Getter and setter
 * ---------------------------------------------------------------------
 */
 
	public long getMessID(){
		return this.messID;
	}
	
	public void setMessID(long id){
		this.messID = id;
	}
	
	public Node getSender(){
		return this.sender;
	}
	public Node getReceiver(){
		return this.receiver;
	}
	
	public int getType(){
		return this.type;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public Object getBody(){
		return this.body;
	}
	
	public void setBody(Object body){
		this.body = body;
	}
	
	
	
	
}

