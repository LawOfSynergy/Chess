package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Board;
import model.Location;
import model.MoveHandler;
import model.Piece;
import model.Player;
import model.Type;

/**
 * This is an IO class for reading text commands to initialize a Chess board.
 * 
 * @author kstimson
 *
 */
public class ChessReader extends Reader{
	private static final String PLACEMENT = "([rnbqkp])([ld])(" + Location.REGEX + ")";
	private static final String SINGLE = "(" + Location.REGEX + ") (" + Location.REGEX + ")(\\*??)";
	private static final String DOUBLE = "(" + Location.REGEX + ") (" + Location.REGEX + ") (" + Location.REGEX + ") (" + Location.REGEX + ")";
	
	/**
	 * The underlying input Reader. 
	 */
	private BufferedReader in;
	
	/**
	 * the board that will be populated, modified, and returned.
	 */
	private MoveHandler handler;
	
	/**
	 * Only Constructor. Automatically wraps the underlying reader in a BufferedReader for efficiency.
	 * @param r
	 */
	public ChessReader(Reader r){
		in = new BufferedReader(r);
		handler = new MoveHandler(new Board(false));
	}

	/**
	 * Closes this input stream and all resources associated with it.
	 */
	@Override
	public void close() throws IOException {
		in.close();
	}

	/**
	 * See {@code Reader.read()}
	 */
	@Override
	public int read(char[] arg0, int arg1, int arg2) throws IOException {
		return in.read(arg0, arg1, arg2);
	}
	
	/**
	 * Invoking this method will return a Board object (not yet implemented) that will be initialized according to the commands stored in the file. This method will read every line in the 
	 * file then close itself unless an exception is thrown. All invalid commands will be skipped and printed to the standard error stream.
	 * 
	 * TODO implement return type
	 * 
	 * @throws IOException if an IOException occurs
	 */
	public MoveHandler readGame()throws IOException{
		Pattern placement = Pattern.compile(PLACEMENT);
		Pattern single = Pattern.compile(SINGLE);
		Pattern complex = Pattern.compile(DOUBLE);
		Matcher m = null;
		
		String s = null;
		int i = 0;
		
		while((s = in.readLine()) != null){
			i++;
			m = placement.matcher(s);
			if(m.matches()){
				processPlacement(m.group(1), m.group(2), m.group(3));
				continue;
			}
			
			m = single.matcher(s);
			if(m.matches()){
				processSingle(m.group(1), m.group(2));
				continue;
			}
			
			m = complex.matcher(s);
			if(m.matches()){
				processDouble(m.group(1), m.group(2), m.group(3), m.group(4));
				continue;
			}
			
			System.err.println("Invalid command \"" + s + "\" at line " + i);
		}
		
		//makes sure that the reader is closed after completely reading in the file.
		close();
		
		return handler;
	}
	
	/**
	 * This will actually process the placement information for manipulating the board prior to it being returned.
	 * @param type the type of the piece to be placed.
	 * @param player the player to whom the piece belongs.
	 * @param location the location the piece is going to be placed.
	 */
	private void processPlacement(String type, String player, String location){
		handler.addPiece(Location.valueOf(location), new Piece(Type.getByFileString(type), Player.getByFileString(player)));
	}
	
	/**
	 * This will actually process the single piece movement information for manipulating the board prior to it being returned.
	 * @param start the starting location.
	 * @param end the ending location.
	 * @param capture whether or not a piece was captured.
	 */
	private void processSingle(String start, String end){
		try{
			handler.makeMove(Location.valueOf(start), Location.valueOf(end));
			handler.display();
		}catch(Exception ex){
			ex.printStackTrace();
			System.err.println("Invalid Move");
		}
	}
	
	/**
	 * This will actually process the two piece movement information for manipulating the board prior to it being returned.
	 * @param start1 the starting location of the first piece.
	 * @param end1 the ending location of the first piece.
	 * @param start2 the starting location of the second piece.
	 * @param end2 the ending location of the second piece.
	 */
	private void processDouble(String start1, String end1, String start2, String end2){
		System.out.println("Double Piece Movement: " + start1 + " to " + end1 + ", " + start2 + " to " + end2);
	}
}
