package model;

public enum Player {
	white("l"), black("d");
	
	private final String fileString;
	
	private Player(String fileString){
		this.fileString = fileString;
	}
	
	/**
	 * @return the representation of this player when written to a file
	 */
	public String toFileString(){
		return fileString;
	}
	
	public static Player getByFileString(String s){
		Player p = (s.equals(white.fileString)) ? white : (s.equals(black.fileString)) ? black : null;
		if(p == null)throw new IllegalFormatException("Does not match the regex: \"[ld]\"");
		return p;
	}
}
