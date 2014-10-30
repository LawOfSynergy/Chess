package model;

import io.ChessReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Board{
	private LinkedHashMap<Location, Tile> tiles = new LinkedHashMap<Location, Tile>();
	
	/**
	 * @param initPieces flag indicating whther or not this board should automatically populate the board with pieces in the default configuration
	 */
	public Board(boolean initPieces){
		initTiles();
		if(initPieces)initPieces();
	}
	
	/**
	 * Constructs a board and automatically populates the board with pieces in the default configuration.
	 */
	public Board(){
		this(true);
	}
	
	/**
	 * Constructs a board by reading in a file and initializing the board accordingly.
	 * @param initialFile the path to the file to read in.
	 * @throws IOException if an IOException occurs
	 */
	public Board(String initialFile)throws IOException{
		initTiles();
		
		try(ChessReader in = new ChessReader(new FileReader(initialFile))){
			Board temp = in.readGame().getBoard();
			tiles = temp.tiles;
		}catch(IOException ex){
			throw ex;
		}
	}
	
	private void initTiles(){
		for(Location loc : Location.getAllLocations()){
			tiles.put(loc, new Tile());
		}
	}
	
	private void initPieces(){
		addPiece(Location.valueOf("a1"), new Piece(Type.rook, Player.white));
		addPiece(Location.valueOf("b1"), new Piece(Type.knight, Player.white));
		addPiece(Location.valueOf("c1"), new Piece(Type.bishop, Player.white));
		addPiece(Location.valueOf("d1"), new Piece(Type.queen, Player.white));
		addPiece(Location.valueOf("e1"), new Piece(Type.king, Player.white));
		addPiece(Location.valueOf("f1"), new Piece(Type.bishop, Player.white));
		addPiece(Location.valueOf("g1"), new Piece(Type.knight, Player.white));
		addPiece(Location.valueOf("h1"), new Piece(Type.rook, Player.white));
		for(int i = 0; i < Location.SIZE; i++){
			addPiece(Location.valueOf(i, 1), new Piece(Type.pawn, Player.white));
			addPiece(Location.valueOf(i, 6), new Piece(Type.pawn, Player.black));
		}
		addPiece(Location.valueOf("a8"), new Piece(Type.rook, Player.black));
		addPiece(Location.valueOf("b8"), new Piece(Type.knight, Player.black));
		addPiece(Location.valueOf("c8"), new Piece(Type.bishop, Player.black));
		addPiece(Location.valueOf("d8"), new Piece(Type.queen, Player.black));
		addPiece(Location.valueOf("e8"), new Piece(Type.king, Player.black));
		addPiece(Location.valueOf("f8"), new Piece(Type.bishop, Player.black));
		addPiece(Location.valueOf("g8"), new Piece(Type.knight, Player.black));
		addPiece(Location.valueOf("h8"), new Piece(Type.rook, Player.black));
	}
	
	/**
	 * adds the corresponding piece to the corresponding location
	 * @param loc the location to which to add the piece
	 * @param p the piece to add to the corresponding location.
	 */
	public void addPiece(Location loc, Piece p){
		if(loc == null)throw new IllegalArgumentException("loc cannot be null");
		if(p == null)throw new IllegalArgumentException("p cannot be null");
		
		Tile tile = tiles.get(loc);
		
		Piece temp = tile.setPiece(p);
		if(temp != null){
			System.err.println(temp);
			System.err.println(p);
			tile.setPiece(temp);
			throw new IllegalArgumentException("There is already a piece in location: " + loc);
		}
		
		if(p.getType() == Type.king){
			for(Tile t : tiles.values()){
				if(t.getPiece() == null)continue;
				if(t != tile  && t.getPiece().getType() == Type.king && t.getPiece().getPlayer() == p.getPlayer()){
					t.setPiece(null);
					break;
				}
			}
		}
	}
	
	/**
	 * removes the piece located at the corresponding location.
	 * @param loc
	 * @return
	 */
	public boolean removePiece(Location loc){
		return tiles.get(loc).setPiece(null) != null;
	}
	
	/**
	 * moves a piece from one location to another.
	 * @param from the starting location of the piece
	 * @param to the ending location of the piece
	 * @return the piece that was previously inhabiting the ending location, null if there wasn't one there.
	 */
	public Piece movePiece(Location from, Location to){
		return tiles.get(to).setPiece(tiles.get(from).setPiece(null));
	}
	
	/**
	 * Gets the piece at the corresponding location
	 * @param loc the location of the piece to get.
	 * @return the piece located at the corresponding position.
	 */
	public Piece getPiece(Location loc){
		return tiles.get(loc).getPiece();
	}

	/**
	 * gets the tile representing the corresponding location
	 * @param loc the location of the tile to get
	 * @return the tile that represents the corresponding location.
	 */
	public Tile getTile(Location loc) {
		return tiles.get(loc);
	}
	
	/**
	 * returns a semi-deep copy of this board.
	 * @return
	 */
	public LinkedHashMap<Location, Tile> getTiles(){
		LinkedHashMap<Location, Tile> tiles = new LinkedHashMap<Location, Tile>();
		for(Map.Entry<Location, Tile> entry : this.tiles.entrySet()){
			tiles.put(entry.getKey(), entry.getValue().copy());
		}
		return tiles;
	}
	
	/**
	 * returns a string representation of this board.
	 */
	public String toString(){
		String result = "";
		for(int y = 0; y < Location.SIZE; y++){
			for(int x = 0; x < Location.SIZE; x++){
				Tile t = tiles.get(Location.valueOf(x, y));
				result += (t.isOccupied()) ? t.getPiece().getType().toFileString() + " ": "X ";
			}
			result+="\n";
		}
		return result;
	}
	
	/**
	 * represents a square on the chess board.
	 * @author kstimson
	 *
	 */
	public static class Tile {
		private Piece piece = null;
		private boolean threatened = false;
		
		private Tile(){}
		
		/**
		 * gets the piece at this location
		 * @return a piece
		 */
		public Piece getPiece(){
			return piece;
		}
		
		/**
		 * sets the piece at this location
		 * @param piece the piece to put in this location
		 * @return the piece that previously inhabited this tile
		 */
		public Piece setPiece(Piece piece){
			Piece previous = this.piece;
			this.piece = piece;
			return previous;
		}
		
		/**
		 * @return whether or not this location is occupied
		 */
		public boolean isOccupied(){
			return piece != null;
		}
		
		/**
		 * @return whether or not this tile is threatened.
		 */
		public boolean isThreatened(){
			return threatened;
		}
		
		/**
		 * sets whether or not this tile is threatened
		 * @param threatened whether or not this tile is threatened
		 */
		public void setThreatened(boolean threatened){
			this.threatened = threatened;
		}
		
		/**
		 * copies this tile
		 * @return a copy of this tile
		 */
		private Tile copy(){
			Tile temp = new Tile();
			temp.piece = piece;
			temp.threatened = threatened;
			return temp;
		}
		
		/**
		 * returns a string representation of this tile
		 */
		public String toString(){
			return "Piece: " + piece + "; Threatened: " + threatened;
		}
	}
}
