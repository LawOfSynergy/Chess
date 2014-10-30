package io;

import java.io.Serializable;

import model.Location;

/**
 * represents a movement that has been made. Is used to transfer information of the network
 * @author kstimson
 *
 */
public class MovementPacket implements Serializable{
	private static final long serialVersionUID = 1L;
	private int fromX, toX;
	private int fromY, toY;
	
	public MovementPacket(int fromX, int fromY, int toX, int toY){
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}
	
	/**
	 * gets the starting location of the move
	 * @return the starting location of the move
	 */
	public Location getFrom(){
		return Location.valueOf(fromX, fromY);
	}
	
	/**
	 * gets the terminating location of the move
	 * @return the terminating location of the move
	 */
	public Location getTo(){
		return Location.valueOf(toX, toY);
	}
	
	/**
	 * returns a string representation of this move
	 */
	public String toString(){
		return getFrom() + " " + getTo();
	}
}
