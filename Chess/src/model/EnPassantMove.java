package model;

/**
 * A type of move
 * @author kstimson
 *
 */
public class EnPassantMove extends Move{
	private Location from, to, enemyLoc;
	private Piece captured;
	
	public EnPassantMove(MoveHandler handler, Piece p, Location from, Location to, Location enemyLoc){
		super(handler, p);
		this.from = from;
		this.to = to;
		this.enemyLoc = enemyLoc;
		captured = handler.getTile(enemyLoc).getPiece();
	}
	
	/**
	 * executes this movement. Do not invoke this directly. It should be left up to the MoveHandler
	 */
	@Override
	public void execute() {
		handler.movePiece(from, to);
		handler.getTile(enemyLoc).setPiece(null);
	}
	
	@Override
	public void revert(){
		handler.movePiece(to, from);
		handler.getTile(enemyLoc).setPiece(captured);
	}
	
	public Location getFrom(){
		return from;
	}
	
	public Location getTo(){
		return to;
	}
	
}
