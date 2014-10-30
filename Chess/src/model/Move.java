package model;

public abstract class Move {
	protected MoveHandler handler;
	protected Piece piece;
	
	public Move(MoveHandler handler, Piece piece){
		this.handler = handler;
		this.piece = piece;
	}
	
	/**
	 * executes the move to its full extent. moving all relevant pieces.
	 */
	public abstract void execute();
	
	/**
	 * reverses the effect of this move.
	 */
	public abstract void revert();
}
