/*
 * CANInterval.java
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

/*Classe utilitaire qui définit un intervalle [x;y]*/

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.util.*;
import java.io.*;


public class CANInterval {
	
/*----------------------------------------------------------------------
 * Attributs
 * ---------------------------------------------------------------------
 */	
	
	//En 2 dimensions
	private float a;
	private float b;
	
/*----------------------------------------------------------------------
 * Constructeur
 * ---------------------------------------------------------------------
 */
	
	public CANInterval(float x, float y){
		this.a=x;
		this.b=y;
	}

/*----------------------------------------------------------------------
 * Méthodes
 * ---------------------------------------------------------------------
 */
	// Return true if two intervals abut
	public boolean isAbutted(CANInterval interval){
		if((this.b == interval.a) || (interval.b == this.a)){
			return true;
		} else {
			return false;
		}
	}
	
	//Return true if two intervals overlap
	public boolean isOverlapped(CANInterval interval){
		//si interval contenu dans this ou this contenu dans interval
		if(((this.a <= interval.a) && (this.b >= interval.b)) || ((interval.a <= this.a) && (interval.b >= this.b))){
			return true;
		} else {
			return false;
		}
	}
	
	//Return true if this (considered as a simple point with coordinates [a;b]) belongs to a zone
	public boolean belongsToZone(CANInterval zoneX, CANInterval zoneY){
		System.out.println("belongsToZone : beginning of function");
		if((this.getA()>=zoneX.getA())&&(this.getA()<=zoneX.getB())&&(this.getB()>=zoneY.getA())&&(this.getB()<=zoneY.getB())){
			return true;
		}else{
			return false;
		}
	}
	
	
	public String toString(){
		return ("["+this.a+";"+this.b+"]");
	}
	
/*----------------------------------------------------------------------
 * Getters et setters
 * ---------------------------------------------------------------------
 */
	
	public float getA(){
		return this.a;
	}
	public void setA(float x){
		this.a = x;
	}
	public float getB(){
		return this.b;
	}
	public void setB(float y){
		this.b = y;
	}
	
	
}

