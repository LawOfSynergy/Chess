package model;

import java.io.FileReader;
import java.io.IOException;

import io.ChessReader;

/**
 * Driver. Takes a file path to read a game in from.
 * @author kstimson
 *
 */
public class Driver {
	public static void main(String[] args){
		if(args.length == 0)throw new IllegalArgumentException("Must input a file to load from.");
		try{
			ChessReader reader = new ChessReader(new FileReader(args[0]));
			reader.readGame();
			reader.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
