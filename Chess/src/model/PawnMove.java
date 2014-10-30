package model;

/**
 * @author kstimson
 * 
 * A move representing a 2-space forward movement by a pawn. Requires special conditions so it has its own class. This class is mainly for use in conjunction with En Passant.
 */
public class PawnMove extends SimpleMove {
	private Location skipped;
	
	public PawnMove(MoveHandler handler, Piece p, Location from, Location to, Location skipped){
		super(handler, p, from, to);
		this.skipped = skipped;
	}
	
	/**
	 * gets the location that was skipped. 
	 * @return
	 */
	public Location getSkipped(){
		return skipped;
	}
	
	/**
	 * executes this movement. Do not invoke this directly. It should be left up to the MoveHandler
	 */
	@Override
	public void execute() {
		super.execute();
	}

	@Override
	public void revert(){
		super.revert();
	}
}
