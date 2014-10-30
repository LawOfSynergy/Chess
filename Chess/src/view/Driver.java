package view;

import javax.swing.JFrame;

import model.Board;
import model.MoveHandler;

public class Driver {
	private static GamePanel panel;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new GamePanel(new MoveHandler(new Board(true)));
		
		frame.add(panel);
		
		frame.setVisible(true);
	}
}
