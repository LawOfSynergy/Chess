package model;

/**
 * @author kstimson
 * 
 * The normal movement. This is used the vast majority of the time. The only other moves that should ever exist are CastleMove, EnPassantMove, and PawnMove.
 */
public class SimpleMove extends Move {
	private Location from, to;
	private boolean capture;
	private Piece captured;
	private boolean previouslyMoved;
	
	public SimpleMove(MoveHandler handler, Piece p, Location from, Location to){
		super(handler, p);
		this.from = from;
		this.to = to;
		capture = (captured = handler.getTile(to).getPiece()) != null;
		previouslyMoved = p.hasMoved();
	}
	
	@Override
	public void execute() {
		handler.movePiece(from, to);
		piece.setMoved(true);
	}
	
	@Override
	public void revert(){
		handler.movePiece(to, from);
		piece.setMoved(previouslyMoved);
		if(captured != null){
			handler.addPiece(to, captured);
		}
	}
	
	public Location getFrom(){
		return from;
	}
	
	public Location getTo(){
		return to;
	}
	
	public String toString(){
		return from + " " + to + ((capture) ? "*" : "");
	}
}
