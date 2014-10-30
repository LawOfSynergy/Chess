package model;

/**
 * a type of move. Not Yet Implemented
 * @author kstimson
 *
 */
public class CastleMove extends Move{
	private Location from1, to1, from2, to2;
	
	public CastleMove(MoveHandler handler, Piece piece, Location from1, Location to1, Location from2, Location to2) {
		super(handler, piece);
		this.from1 = from1;
		this.to1 = to1;
		this.from2 = from2;
		this.to2 = to2;
	}

	@Override
	public void execute() {
		handler.movePiece(from1, to1);
		handler.movePiece(from2, to2);
	}
	
	@Override
	public void revert(){
		handler.movePiece(to1, from1);
		handler.movePiece(to2, from2);
	}
}
