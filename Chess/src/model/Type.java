package model;

import java.util.HashMap;
import java.util.LinkedHashMap;

import model.Board.Tile;

public enum Type{
		pawn{
			//nonstatic initializer
			{
				this.fileString = "p";
			}

			@Override
			public void getLegalMoves(LinkedHashMap<Location, Move> moves, MoveHandler handler, Location loc) {
				LinkedHashMap<Location, Board.Tile> tiles = handler.getTiles();
				Piece currentPiece = tiles.get(loc).getPiece();
				int yOffset = (currentPiece.getPlayer() == Player.white) ? 1 : -1;
				Location to = null;
				
				//Standard Move
				try{
					to = loc.getLocationByOffset(0, yOffset);
					if(!tiles.get(to).isOccupied()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				//Standard Attack Moves
				try{
					to = loc.getLocationByOffset(1, yOffset);
					
					if(tiles.get(to).isOccupied() && tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
					
					//En Passant
					if(handler.getLastMove() instanceof PawnMove){
						PawnMove m = (PawnMove)handler.getLastMove();
						if(m.getSkipped() == to){
							moves.put(m.getSkipped(), new EnPassantMove(handler, currentPiece, loc, m.getSkipped(), m.getTo()));
						}
					}
				}catch(IllegalArgumentException ex){
					//do nothing
				}
				
				try{
					to = loc.getLocationByOffset(-1, yOffset);
					
					if(tiles.get(to).isOccupied() && tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
					
					//En Passant
					if(handler.getLastMove() instanceof PawnMove){
						PawnMove m = (PawnMove)handler.getLastMove();
						if(m.getSkipped() == to){
							moves.put(m.getSkipped(), new EnPassantMove(handler, currentPiece, loc, m.getSkipped(), m.getTo()));
						}
					}
				}catch(IllegalArgumentException ex){
					//do nothing
				}
				
				//PawnMove
				if(currentPiece.hasMoved())return;
				try{
					Location skipped = loc.getLocationByOffset(0, yOffset);
					to = skipped.getLocationByOffset(0, yOffset);
					
					if(!tiles.get(skipped).isOccupied()){
						if(!tiles.get(to).isOccupied()){
							moves.put(to, new PawnMove(handler, currentPiece, loc, to, skipped));
						}
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				
			}

			@Override
			public void applyThreat(HashMap<Location, Tile> map, Location loc) {
				HashMap<Location, Tile> tiles = map;
				Piece currentPiece = tiles.get(loc).getPiece();
				int yOffset = (currentPiece.getPlayer() == Player.white) ? 1 : -1;
				Location to = null;
				
				try{
					to = loc.getLocationByOffset(1, yOffset);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-1, yOffset);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
			}
		},
		rook{
			//nonstatic initializer
			{
				this.fileString = "r";
			}

			@Override
			public void getLegalMoves(LinkedHashMap<Location, Move> moves, MoveHandler handler, Location loc) {
				addLineOfMoves(moves, handler, loc, 0, 1);
				addLineOfMoves(moves, handler, loc, 0,-1);
				addLineOfMoves(moves, handler, loc, 1, 0);
				addLineOfMoves(moves, handler, loc,-1, 0);
			}

			@Override
			public void applyThreat(HashMap<Location, Tile> map, Location loc) {
				applyThreatLine(map, loc, 0, 1);
				applyThreatLine(map, loc, 0,-1);
				applyThreatLine(map, loc, 1, 0);
				applyThreatLine(map, loc,-1, 0);
			}
			
			
		},
		knight{
			//nonstatic initializer
			{
				this.fileString = "n";
			}

			@Override
			public void getLegalMoves(LinkedHashMap<Location, Move> moves, MoveHandler handler, Location loc) {
				LinkedHashMap<Location, Board.Tile> tiles = handler.getTiles();
				Piece currentPiece = tiles.get(loc).getPiece();
				Location to = null;
				
				try{
					to = loc.getLocationByOffset(-1, 2);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, 2);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(2, 1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(2, -1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-1, -2);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, -2);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-2, 1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-2, -1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
			}

			@Override
			public void applyThreat(HashMap<Location, Tile> map, Location loc) {
				HashMap<Location, Board.Tile> tiles = map;
				Location to = null;
				
				try{
					to = loc.getLocationByOffset(-1, 2);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}try{
					to = loc.getLocationByOffset(1, 2);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(2, 1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(2, -1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-1, -2);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, -2);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-2, 1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-2, -1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
			}
			
		},
		bishop{
			//nonstatic initializer
			{
				this.fileString = "b";
			}

			@Override
			public void getLegalMoves(LinkedHashMap<Location, Move> moves, MoveHandler handler, Location loc) {
				addLineOfMoves(moves, handler, loc, 1, 1);
				addLineOfMoves(moves, handler, loc, 1,-1);
				addLineOfMoves(moves, handler, loc,-1, 1);
				addLineOfMoves(moves, handler, loc,-1,-1);
			}

			@Override
			public void applyThreat(HashMap<Location, Tile> map, Location loc) {
				applyThreatLine(map, loc, 1, 1);
				applyThreatLine(map, loc, 1,-1);
				applyThreatLine(map, loc,-1, 1);
				applyThreatLine(map, loc,-1,-1);
			}
			
		},
		queen{
			//nonstatic initializer
			{
				this.fileString = "q";
			}

			@Override
			public void getLegalMoves(LinkedHashMap<Location, Move> moves, MoveHandler handler, Location loc) {
				addLineOfMoves(moves, handler, loc, 0, 1);
				addLineOfMoves(moves, handler, loc, 0,-1);
				addLineOfMoves(moves, handler, loc, 1, 0);
				addLineOfMoves(moves, handler, loc,-1, 0);
				addLineOfMoves(moves, handler, loc, 1, 1);
				addLineOfMoves(moves, handler, loc, 1,-1);
				addLineOfMoves(moves, handler, loc,-1, 1);
				addLineOfMoves(moves, handler, loc,-1,-1);
			}

			@Override
			public void applyThreat(HashMap<Location, Tile> map, Location loc) {
				applyThreatLine(map, loc, 0, 1);
				applyThreatLine(map, loc, 0,-1);
				applyThreatLine(map, loc, 1, 0);
				applyThreatLine(map, loc,-1, 0);
				applyThreatLine(map, loc, 1, 1);
				applyThreatLine(map, loc, 1,-1);
				applyThreatLine(map, loc,-1, 1);
				applyThreatLine(map, loc,-1,-1);
			}
		},
		king(){
			//nonstatic initializer
			{
				this.fileString = "k";
			}

			@Override
			public void getLegalMoves(LinkedHashMap<Location, Move> moves, MoveHandler handler, Location loc) {
				LinkedHashMap<Location, Board.Tile> tiles = handler.getTiles();
				Piece currentPiece = tiles.get(loc).getPiece();
				Location to = null;
				
				//Normal Moves
				try{
					to = loc.getLocationByOffset(-1, 1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(0, 1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, 1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, 0);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, -1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(0, -1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-1, -1);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-1, -0);
					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
					}
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				//TODO Castling
				//Castling
//				if(currentPiece.hasMoved())return;
//				
//				try{
//					to = loc.getLocationByOffset(-4, 0);
//					if(!tiles.get(to).isOccupied() || tiles.get(to).getPiece().getPlayer() != currentPiece.getPlayer()){
//						moves.put(to, new SimpleMove(handler, currentPiece, loc, to));
//					}
//				}catch(IllegalArgumentException ex){
//					//ignore it. That simply means it attempted to grab a location that is out of bounds.
//				}
			}

			@Override
			public void applyThreat(HashMap<Location, Tile> map, Location loc) {
				HashMap<Location, Board.Tile> tiles = map;
				Location to = null;
				
				try{
					to = loc.getLocationByOffset(-1, 1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(0, 1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, 1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, 0);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(1, -1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(0, -1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-1, -1);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
				
				try{
					to = loc.getLocationByOffset(-1, -0);
					map.get(to).setThreatened(true);
				}catch(IllegalArgumentException ex){
					//ignore it. That simply means it attempted to grab a location that is out of bounds.
				}
			}
			
		};
		
		protected String fileString;
		
		public String toFileString(){
			return fileString;
		}
		
		public void act(){
			
		}
		
		public static Type getByFileString(String type){
			Type t = null;
			for(Type temp : values()){
				if(type.equals(temp.fileString)){
					t = temp;
					break;
				}
			}
			if(t == null)throw new IllegalArgumentException("Does not match the regex: \"[prnbqk]\"");
			return t;
		}
		
		/**
		 * gets all moves that can be done by this type of piece.
		 * @param moves the map to which to add the moves
		 * @param handler the model upon which to evaluate possible moves
		 * @param loc the location of the piece who is calculating its moves
		 */
		public abstract void getLegalMoves(LinkedHashMap<Location, Move> moves, MoveHandler handler, Location loc);
		
		/**
		 * convenience method. Ignore this.
		 * @param moves
		 * @param handler
		 * @param loc
		 * @param xOffset
		 * @param yOffset
		 */
		private static void addLineOfMoves(LinkedHashMap<Location, Move> moves, MoveHandler handler, Location loc, int xOffset, int yOffset){
			Piece currentPiece = handler.getTiles().get(loc).getPiece();
			
			for(int y = loc.getY() + yOffset, x = loc.getX() + xOffset; y < Location.SIZE && y >= 0 && x >= 0 && x < Location.SIZE ; y+= yOffset, x+= xOffset){
				Location l = Location.valueOf(x, y);
				if(handler.getTiles().get(l).isOccupied()){
					if(handler.getTiles().get(l).getPiece().getPlayer() != currentPiece.getPlayer()){
						moves.put(l, new SimpleMove(handler, currentPiece, loc, l));
					}
					return;
				}
				moves.put(l, new SimpleMove(handler, currentPiece, loc, l));
			}
		}
		
		/**
		 * Applys threat to the map.
		 * @param map
		 * @param loc
		 */
		public abstract void applyThreat(HashMap<Location, Tile> map, Location loc);
		
		/**
		 * convenience method. Ignore this.
		 * @param map
		 * @param loc
		 * @param xOffset
		 * @param yOffset
		 */
		private static void applyThreatLine(HashMap<Location, Tile> map, Location loc, int xOffset, int yOffset){
			for(int y = loc.getY() + yOffset, x = loc.getX() + xOffset; y < Location.SIZE && y >= 0 && x >= 0 && x < Location.SIZE ; y+= yOffset, x+= xOffset){
				Location l = Location.valueOf(x, y);
				if(map.get(l).isOccupied()){
					map.get(l).setThreatened(true);
					return;
				}
				map.get(l).setThreatened(true);
			}
		}
	}