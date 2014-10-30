package model;

public final class Piece {
	private Player player;
	private Type type;
	private boolean hasMoved;
	
	/**
	 * Constructor that takes a type and a player
	 * @param type the type that this piece represents
	 * @param player the player who owns this piece
	 */
	public Piece(Type type, Player player){
		this(type, player, false);
	}
	
	/**
	 * Constructor that takes a type, player, and a boolean representing whether or not the piece has previously moved.
	 * @param type the type that this piece represents
	 * @param player the player who owns this piece
	 * @param hasMoved flag indicating whether or not this piece has already moved.
	 */
	public Piece(Type type, Player player, boolean hasMoved){
		this.type = type;
		this.player = player;
		this.hasMoved = hasMoved;
	}
	
	/**
	 * @return this piece's player
	 */
	public Player getPlayer(){
		return player;
	}
	
	/**
	 * @return this piece's type
	 */
	public Type getType(){
		return type;
	}
	
	/**
	 * promotes this piece into a higher level piece.
	 * @param type the type that this piece will be promoted into
	 * @throws IllegalArgumentException if type is a king
	 * @throws IllegalStateException if the type of this piece is not currently Type.pawn
	 */
	public void promote(Type type){
		if(type == Type.king)throw new IllegalArgumentException("Cannot promote to King");
		if(this.type != Type.pawn)throw new IllegalStateException("Can only promote Pawns");
		this.type = type;
	}
	
	/**
	 * @return whether or not this piece has already moved
	 */
	public boolean hasMoved(){
		return hasMoved;
	}
	
	/**
	 * sets whether or not this piece has previously moved.
	 * @param b
	 */
	public void setMoved(boolean b){
		hasMoved = b;
	}
	
	/**
	 * returns a string representation of this object.
	 */
	public String toString(){
		return type.toFileString() + player.toFileString();
	}
}
