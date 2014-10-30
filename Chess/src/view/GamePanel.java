package view;

import io.Communicator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.Board;
import model.CastleMove;
import model.Location;
import model.Move;
import model.MoveHandler;
import model.Piece;
import model.Player;
import model.Type;

/**
 * Pretty much the entire gui. This is the entire view for the program.
 * @author kstimson
 *
 */
public class GamePanel extends JPanel{
	public static final int IMAGE_LOAD_EXCEPTION = 1;
	
	private static final int CONNECTION_TYPE_HOST = 0;
	private static final int CONNECTION_TYPE_CONNECT = 1;
	private static final long serialVersionUID = 1L;
	private static final String EXTENSION = ".png";
	private static final String PARENT_FOLDER = "/view/images/";
	static final int DIVISOR = 8;
	private static final int PADDING = 2;
	private static EnumMap<Player, EnumMap<Type, BufferedImage>> pieceImages = new EnumMap<Player, EnumMap<Type, BufferedImage>>(Player.class);
	
	/**
	 * Static initializer to load all of the images
	 */
	static{
		try{
			
			for(Player p : Player.values()){
				EnumMap<Type, BufferedImage> map = new EnumMap<Type, BufferedImage>(Type.class);
				for(Type t : Type.values()){
					InputStream input = GamePanel.class.getResourceAsStream(PARENT_FOLDER + p + "/" + t + EXTENSION);
					map.put(t, ImageIO.read(input));
				}
				pieceImages.put(p, map);
			}
			
		}catch(IOException ex){
			ex.printStackTrace();
			System.exit(IMAGE_LOAD_EXCEPTION);
		}
	}
	
	private MoveHandler model;
	private Color[] boardColors = {Color.lightGray, Color.white};
	private LinkedHashMap<Location, Overlay> overlays = new LinkedHashMap<Location, Overlay>();
	
	/**
	 * Only constructor. Takes a MoveHandler and displays the contents.
	 * @param model the model to be displayed
	 */
	public GamePanel(MoveHandler model){
		this.model = model;
		model.forceUpdate();
		gamePlay();
		MouseAdapter listener = new SelectListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		
	}
	
	/**
	 * cycles through until it succeeds at entering a game. Valid 
	 */
	private void gamePlay(){
		boolean success = false;
		while(!success){
			int gameplay = gameplayType();
			if(gameplay == 0){
				success = true;
			}else{
				int connectionType = connectionType();
				Socket connection = connect(connectionType);
				if(connection != null){
					success = true;
					model.setComHandler(new Communicator(connection, Player.values()[Player.values().length - 1 - connectionType], model, this));
				}
			}
		}
	}
	
	/**
	 * asks the user for gameplay preference
	 * @return int indicating the user's choice
	 */
	private int gameplayType(){
		Object[] options = {
				"Single Player",
				"Multi-Player"};
		int gameplay = JOptionPane.showOptionDialog(this,
				"What type of game do you want to play?",
				"Game Play",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
		return gameplay;
	}
	
	/**
	 * asks the user whether they want to host
	 * the game or connect to a game
	 * @return int indicating the user's choice
	 */
	private int connectionType(){
		Object[] options = new Object[]{
				"Host",
				"Connect"};
		int connectionType = JOptionPane.showOptionDialog(this,
				"How do you want to connect?",
				"Connection",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);
		return connectionType;
	}
	
	/**
	 * returns a socket base in the 
	 * user's connection type choice
	 * @param connectionType
	 * @return Socket
	 */
	private Socket connect(int connectionType){
		Socket connection = null;
		//if connectionType is host
		if(connectionType == CONNECTION_TYPE_HOST){
			try(ServerSocket server = new ServerSocket(1234)){
				server.setSoTimeout(300000);
				connection = server.accept();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		//if connectionType is connect
		if(connectionType == CONNECTION_TYPE_CONNECT){
			String ipAddress = JOptionPane.showInputDialog("What Address do you want to connect to.");
			try{
				connection = new Socket(ipAddress, 1234);
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		return connection;
	}
	
	/**
	 * Paints the board, pieces, and overlays in that order.
	 */
	public void paintComponent(Graphics g){
		paintBackground(g);
		paintBoard(g);
		paintPieces(g);
		paintOverlays(g);
	}
	
	private void paintBackground(Graphics g){
		g.setColor((model.getTurn() == Player.white) ? Color.white : Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	/**
	 * paints the board
	 * @param g the graphics object to draw to
	 */
	private void paintBoard(Graphics g){
		int cellSize = Math.min(getWidth(), getHeight())/DIVISOR;
		for(int y = Location.SIZE - 1; y >= 0; y--){
			for(int x = 0; x < Location.SIZE; x++){
				int i = (x+y)%2;
				g.setColor(boardColors[i]);
				g.fillRect(x*cellSize, ((DIVISOR-1 - y)*cellSize), cellSize, cellSize);
			}
		}
	}
	
	/**
	 * Adds an overlay to the corresponding location
	 * @param loc the location to add an overlay to
	 * @param o the overlay to add to the location
	 */
	private void addOverlay(Location loc, Overlay o){
		overlays.put(loc, o);
	}
	
	/**
	 * remove all overlays from the board
	 */
	private void clearOverlays(){
		overlays = new LinkedHashMap<Location, Overlay>();
	}
	
	/**
	 * paint the overlays
	 * @param g the graphics object to paint it to
	 */
	private void paintOverlays(Graphics g){
		int cellSize = Math.min(getWidth(), getHeight())/DIVISOR;
		for(Map.Entry<Location, Overlay> e : overlays.entrySet()){
			e.getValue().paint(g, e.getKey(), cellSize);
		}
	}
	
	/**
	 * paints all of the pieces on the board
	 * @param g the graphics object to which to paint the pieces.
	 */
	private void paintPieces(Graphics g){
		LinkedHashMap<Location, Board.Tile> map = model.getTiles();

		int cellSize = Math.min(getWidth(), getHeight())/DIVISOR;
		
		for(Map.Entry<Location, Board.Tile> e : map.entrySet()){
			if(e.getValue().isOccupied()){
				Piece p = e.getValue().getPiece();
				Location loc = e.getKey();
				Image image = pieceImages.get(p.getPlayer()).get(p.getType()).getScaledInstance(cellSize - (2*PADDING), cellSize - (2*PADDING), BufferedImage.SCALE_DEFAULT);
				g.drawImage(image, loc.getX()*cellSize + PADDING, ((DIVISOR-1-loc.getY())*cellSize) + PADDING, null);
			}
		}
	}

	
	/**
	 * the mouse adapter used to capture mouse input
	 * @author kstimson
	 *
	 */
	private class SelectListener extends MouseAdapter{
		private Location mouseOver = null;
		private Location selected = null;
		private LinkedHashMap<Location, Move> moves = null;
		
		/**
		 * displays all legal moves the piece can make. This gets locked when a piece is selected and unlocked when deselected.
		 * @param loc
		 */
		private void setMouseOver(Location loc){
			if(selected != null)return;
			clearOverlays();
			mouseOver = loc;
			moves = model.getLegalMoves(loc);
			if(moves == null){
				repaint();
				return;
			}
			for(Map.Entry<Location, Move> e : moves.entrySet()){
				Overlay o = Overlay.move;
				if(e.getValue() instanceof CastleMove){
					o = Overlay.castle;
				}
				if(model.getTile(e.getKey()).isOccupied()){
					o = Overlay.attack;
				}
				addOverlay(e.getKey(), o);
			}
			repaint();
		}
		
		/**
		 * invoked when a tile is clicked on. It will either select the piece currently there, deselect the currently selected piece, or make the piece take a legal move.
		 * @param loc the location that was clicked.
		 */
		private void select(Location loc){
			if(selected == null && model.getTile(loc).isOccupied() && model.getTile(loc).getPiece().getPlayer() == model.getTurn()){
				selected = loc;
				addOverlay(loc, Overlay.select);
			}else if(selected == loc){
				selected = null;
				overlays.remove(loc);
			}else if(moves != null && moves.containsKey(loc)){
				model.makeMove(moves.get(loc), selected, loc);
				selected = null;
				moves = null;
			}
			repaint();
			
		}
		
		/**
		 * when mouse is clicked, invokes select()
		 */
		@Override
		public void mousePressed(MouseEvent e){
			int divisor = Math.min(getWidth(), getHeight())/DIVISOR;
			try{
				Location loc = Location.valueOf(e.getX()/divisor, DIVISOR-1 - e.getY()/divisor);
				select(loc);
			}catch(IllegalArgumentException ex){
				return;
			}
		}
		
		/**
		 * when mouse is moved, invokes mouseOver();
		 */
		@Override
		public void mouseMoved(MouseEvent e){
			int divisor = Math.min(getWidth(), getHeight())/DIVISOR;
			try{
				Location loc = Location.valueOf(e.getX()/divisor, DIVISOR-1 - e.getY()/divisor);
				if(loc != mouseOver){
					setMouseOver(loc);
				}
				
			}catch(IllegalArgumentException ex){
				return;
			}
		}
	}
}
