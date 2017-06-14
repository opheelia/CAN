/*
 * TestInterval.java
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
import java.util.*;

public class TestInterval {
	
	public static void main (String args[]) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Intervalle 1 :");
		System.out.println("valeur de a :");
		float a = sc.nextFloat();
		System.out.println("valeur de b :");
		float b = sc.nextFloat();
		CANInterval i1 = new CANInterval(a,b);
		System.out.println("Intervalle 2 :");
		System.out.println("valeur de a :");
		a = sc.nextFloat();
		System.out.println("valeur de b :");
		b = sc.nextFloat();
		CANInterval i2 = new CANInterval(a,b);
		System.out.println("Intervalle inclus dans l'autre : " + i1.isOverlapped(i2) +", Intervalles contigues : "+ i1.isAbutted(i2));
	}
}

