package model;

import io.Communicator;
import io.MovementPacket;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is the interface between the board and any interested parties. This class handles all Piece Movement information and actions. It also applies check constraints, and looks for
 * checkmate. Also controls player turn flow.
 * @author kstimson
 *
 */
public class MoveHandler{
	private Board board;
	private Communicator com = null;
	private EnumMap<Player, Location> kings = new EnumMap<Player, Location>(Player.class);
	private Move lastMove;
	private Player turn = Player.white;
	private GameState state;
	private LinkedHashMap<Location, LinkedHashMap<Location, Move>> legalMoves;
	
	/**
	 * Constructs a MoveHandler with the corresponding board as its underlying data representation.
	 * @param board
	 */
	public MoveHandler(Board board){
		this.board = board;
	}
	
	/**
	 * @return the underlying Board object
	 */
	Board getBoard(){
		return board;
	}
	
	/**
	 * gets the communicator associated with this MoveHandler
	 * @return the com
	 */
	public Communicator getCom(){
		return com;
	}
	
	/**
	 * sets this CommunicationHandler
	 * @param com
	 */
	public void setComHandler(Communicator com){
		this.com = com;
	}
	/**
	 * see {@code Board.getTile(Location loc)}
	 */
	public Board.Tile getTile(Location loc){
		return board.getTile(loc);
	}
	
	/**
	 * adds a piece to the corresponding location.
	 * @param loc the location to which to add a piece.
	 * @param p the piece to add to the corresponding location
	 * @throws IllegalArgumentException if a piece already exists at that location.
	 */
	public void addPiece(Location loc, Piece p){
		board.addPiece(loc, p);
		if(p.getType() == Type.king){
			kings.put(p.getPlayer(), loc);
		}
	}
	
	/**
	 * Moves a piece from one location to another.
	 * @param from the location of the piece to be moved.
	 * @param to the location that the piece is to be moved to.
	 */
	public void movePiece(Location from, Location to){
		Piece p = board.getPiece(from);
		if(p == null)return;
		if(p.getType() == Type.king){
			kings.put(p.getPlayer(), to);
		}
		board.movePiece(from, to);
	}
	
	/**
	 * gets the player whose turn it currently is.
	 * @return
	 */
	public Player getTurn(){
		return turn;
	}
	
	/**
	 * ends this players turn and advances it to the next.
	 */
	public void endTurn(){
		turn = (turn == Player.white) ? Player.black : Player.white;
		state = GameState.ingame;
	}
	
	/**
	 * allows for file command translation into runtime movement command. Throws exceptions if the moves are unavailable.
	 * @param from see movePiece
	 * @param to see movePiece
	 */
	public void makeMove(Location from, Location to){
		update();
		LinkedHashMap<Location, Move> legals = legalMoves.get(from);
		if(legals == null){
			new IllegalArgumentException("There is no piece located at: " + from).printStackTrace();
			return;
		}
		makeMove(legals.get(to));
	}
	
	/**
	 * When a move has been selected, invoke this method with the desired move and it will be executed.
	 * @param m the move to execute.
	 */
	public void makeMove(Move m){
		if(m == null){
			new IllegalArgumentException("m is null").printStackTrace();
			return;
		}
		m.execute();
		lastMove = m;
		finishTurn();
	}
	
	/**
	 * When a move has been selected, invoke this method with the desired move and it will be executed. In addition, the move will be transferred to the opponent's client if in multiplayer
	 * mode.
	 * @param m the move to execute
	 * @param from the start location
	 * @param to the end location
	 */
	public void makeMove(Move m, Location from, Location to){
		if(m == null){
			new IllegalArgumentException("m is null").printStackTrace();
			return;
		}
		if(com != null){
			com.sendPacket(new MovementPacket(from.getX(), from.getY(), to.getX(), to.getY()));
		}
		makeMove(m);
	}
	
	/**
	 * finishes and finalizes the turn.
	 */
	private void finishTurn(){
		endTurn();
		update();
		check();
		applyCheckFilter();
		checkmates();
	}
	
	/**
	 * gets the last move made.
	 * @return the move that was last executed.
	 */
	public Move getLastMove(){
		return lastMove;
	}
	
	/**
	 * gets a map of all of the tiles for the underlying board.
	 * @return a map of locations to tiles.
	 */
	public LinkedHashMap<Location, Board.Tile> getTiles(){
		return board.getTiles();
	}
	
	/**
	 * forces all legal moves to be recalculated. Invoked internally every time a move is made or a piece added.
	 */
	private void update(){
		legalMoves = new LinkedHashMap<Location, LinkedHashMap<Location, Move>>();
		
		for(Map.Entry<Location, Board.Tile> e : getTiles().entrySet()){
			if(e.getValue().isOccupied() && e.getValue().getPiece().getPlayer() == turn){
				LinkedHashMap<Location, Move> moves = new LinkedHashMap<Location, Move>();
				legalMoves.put(e.getKey(), moves);
				e.getValue().getPiece().getType().getLegalMoves(moves, this, e.getKey());
			}
		}
	}
	
	public void forceUpdate(){
		update();
	}
	
	/**
	 * constructs the threatMap for the player whose turn it currently is and adjusts the game mode if the king is in threat.
	 */
	private void check(){
		HashMap<Location, Board.Tile> map = constructThreatMap(turn);
		
		if(kings.get(turn) == null){
			for(Map.Entry<Location, Board.Tile> e : map.entrySet()){
				Piece p = e.getValue().getPiece();
				if(p!= null && p.getType() == Type.king && p.getPlayer() == turn){
					kings.put(turn, e.getKey());
					break;
				}
			}
		}
		
		if(map.get(kings.get(turn)).isThreatened()){
			state = GameState.check;
		}
	}
	
	/**
	 * check for checkmate and stalemate
	 */
	private void checkmates(){
		for(Map.Entry<Location, LinkedHashMap<Location, Move>> entry : legalMoves.entrySet()){
			if(!entry.getValue().isEmpty()){
				return;
			}
		}
		HashMap<Location, Board.Tile> threatMap = constructThreatMap((turn == Player.white) ? Player.white : Player.black);
		state = (threatMap.get(kings.get(turn)).isThreatened()) ? GameState.checkmate : GameState.stalemate;
		System.out.println(state);
	}
	
	/**
	 * 
	 * @param p the player to whom the threat is being applied.
	 * @return
	 */
	private HashMap<Location, Board.Tile> constructThreatMap(Player p){
		HashMap<Location, Board.Tile> map = getTiles();
		
		for(Map.Entry<Location, Board.Tile> e : getTiles().entrySet()){
			if(e.getValue().isOccupied() && e.getValue().getPiece().getPlayer() != p){
				e.getValue().getPiece().getType().applyThreat(map, e.getKey());
			}
		}
		
		return map;
	}
	
	/**
	 * not in use yet
	 * @return the state that this game is in. Either normal, check, checkmate, or stalemate. Any stalemate and checkmate both indicate that the game has concluded.
	 */
	public GameState getState(){
		return state;
	}
	
	/**
	 * Removes all invalid moves.
	 */
	private void applyCheckFilter(){
		//local class. Don't worry about this it is never used anywhere other than this method.
		class LocationPair{
			Location from, to;
			
			public LocationPair(Location from, Location to){
				this.from = from;
				this.to = to;
			}
		}
		
		ArrayList<LocationPair> invalidMoves = new ArrayList<LocationPair>();
		for(Map.Entry<Location, LinkedHashMap<Location, Move>> entry : legalMoves.entrySet()){
			for(Map.Entry<Location, Move> e : entry.getValue().entrySet()){
				Move m = e.getValue();
				try{
					m.execute();
					HashMap<Location, Board.Tile> threatMap = constructThreatMap((turn == Player.white) ? Player.white : Player.black);
					if(threatMap.get(kings.get(turn)).isThreatened()){
						invalidMoves.add(new LocationPair(entry.getKey(), e.getKey()));
					}
					m.revert();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		for(LocationPair e : invalidMoves){
			legalMoves.get(e.from).remove(e.to);
			if(legalMoves.get(e.from).isEmpty()){
				legalMoves.remove(e.from);
			}
		}
	}
	
	/**
	 * gets all of the legal moves for the piece at the corresponding location
	 * @param loc
	 * @return
	 */
	public LinkedHashMap<Location, Move> getLegalMoves(Location loc){
		LinkedHashMap<Location, Move> moves = null;
		if(com == null || turn != com.getPlayer()){
				moves = legalMoves.get(loc);
		}
		return moves;
	}
	
	/**
	 * displays the board.
	 */
	public void display(){
		System.out.println(board);
	}
}
