package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * This class is immutable and as such is safe for concurrent use. Also, this class is instance controlled so the only way to retrieve a location is through one of the {@code valueOf()} 
 * methods or the ({@code getLocationByOffset()} method. This class represents a logical location according to tiles on a chess board. As such, there are exactly 64 possible 
 * Location instances.
 * 
 * @author kstimson
 * 
 */
public final class Location implements Comparable<Location>{
	public static final int SIZE = 8;
	public static final String REGEX = "[a-h][1-8]";
	private static final char START_CHAR = 'a';
	private static final TreeMap<String, Location> locations = new TreeMap<String, Location>();
	
	static{
		for(int x = 0; x < SIZE; x++){
			for(int y = 0; y < SIZE; y++){
				String string = "" + ((char)(START_CHAR + x)) + "" + (y + 1);
				locations.put(string, new Location(string, x, y));
			}
		}
	}

	private String asString;
	private int x;
	private int y;
	
	/**
	 * Private constructor for making location object. They are instance controlled by the {@code valueOf()} methods and {@code getLocationByOffset()}.
	 * @param asString the string representation of this location when written to a file
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	private Location(String asString, int x, int y){
		this.asString = asString;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return this location's x-coordinate.
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * @return this location's y-coordinate.
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * @return a String representation of this object.
	 */
	public String toString(){
		return asString;
	}
	
	/**
	 * Calculates a hashcode.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + y;
		result = prime * result + x;
		return result;
	}
	
	/**
	 * Compares this object with another one to see if they are equivalent.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	/**
	 * gets a location relative to this that is offset by x on the x-axis and y on the y-axis.
	 * @param x the x offset
	 * @param y the y offset
	 * @return a new location
	 */
	public Location getLocationByOffset(int x, int y){
		return valueOf(this.x + x, this.y + y);
	}
	
	/**
	 * gets a location represented by this string as long as it conforms to the Location regex.
	 * @param location the string representing the coordinates of the location to be returned
	 * @return the location represented by the string
	 * @throws IllegalArgumentException if location is null
	 * @throws IllegalFormatException if the string does not match the Location regex
	 */
	public static Location valueOf(String location){
		if(location == null)throw new IllegalArgumentException("location must not be null!");
		if(!location.matches(REGEX))throw new IllegalFormatException();
		return locations.get(location);
	}
	
	/**
	 * gets a location that represents the corresponding x- and y- coordinates
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the location representing the x- and y- coordinates
	 * @throws IllegalArgumentException if either of the following are untrue: 0 < x < 7; 0 < y < 7
	 */
	public static Location valueOf(int x, int y){
		if(x < 0 || x > SIZE-1)throw new IllegalArgumentException("X is out of bounds. Recieved " + x + ". Expected 0 <= x <= " + (SIZE-1));
		if(y < 0 || y > SIZE-1)throw new IllegalArgumentException("Y is out of bounds. Recieved " + y + ". Expected 0 <= y <= " + (SIZE-1));
		return locations.get("" + ((char)(START_CHAR + x)) + "" + (y + 1));
	}
	
	/**
	 * returns a defensive copy of all possible location objects.
	 * @return a collection of location objects
	 */
	public static Collection<Location> getAllLocations(){
		//Defensively make a copy just in case the values() method does not already do so.
		return new ArrayList<Location>(locations.values());
	}
	
	/**
	 * See contract for compareTo
	 * 
	 * compared in y- major order
	 * 
	 * @return 1 if this object is greater than l; -1 if this object is less than l; 0 otherwise
	 */
	@Override
	public int compareTo(Location l) {
		return (y > l.y) ? 1 : (y < l.y) ? -1 : (x > l.x) ? 1 : (x < l.x) ? -1 : 0;
	}
}
