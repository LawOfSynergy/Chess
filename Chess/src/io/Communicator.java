package io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import model.MoveHandler;
import model.Player;
import view.GamePanel;

/**
 * Handles all of the communication whther acting server or client
 * @author kstimson
 *
 */
public class Communicator{
	private Socket sock;
	private Player player;
	private volatile boolean kill = false;
	private LinkedBlockingQueue<MovementPacket> outQueue = new LinkedBlockingQueue<MovementPacket>();
	private Thread inThread = new Thread(new Input());
	private Thread outThread = new Thread(new Output());
	private MoveHandler model;
	private GamePanel view;

	/**
	 * creates a Communicator that sends information through the given socket.
	 * @param sock the socket that is used to pass information over the network
	 * @param p the player that this communicator stands in for
	 * @param model the model for the program
	 * @param view the view for the program
	 */
	public Communicator(Socket sock, Player p, MoveHandler model, GamePanel view){
		this.player = p;
		this.sock = sock;
		this.model = model;
		this.view = view;
		inThread.start();
		outThread.start();
	}

	/**
	 * implementation detail. Do not worry about this. The only important thing is that this thread is what executes the move
	 * then updates the view.
	 * @author kstimson
	 *
	 */
	private class Input implements Runnable{
		public void run(){
			try(ObjectInputStream in = new ObjectInputStream(sock.getInputStream())){
				while(!kill){
					try {
						Object obj = in.readObject();
						System.out.println(obj);
						MovementPacket pack = (MovementPacket)obj;
						model.makeMove(pack.getFrom(), pack.getTo());
						view.repaint();
					} catch (ClassNotFoundException ex) {
						ex.printStackTrace();
					}
				}
			}catch(IOException ex){
				ex.printStackTrace();
				kill();
			}
		}
	}
	
	/**
	 * writes whatever is put into it to the socket.
	 * @author kstimson
	 *
	 */
	private class Output implements Runnable{
		public void run(){
			try(ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream())){
				while(!kill){
					try {
						out.writeObject(outQueue.take());
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}catch(IOException ex){
				ex.printStackTrace();
				kill();
			}
		}
	}

	/**
	 * kills all threads that this communicator uses.
	 */
	public void kill(){
		kill = true;
		inThread.interrupt();
		outThread.interrupt();
	}

	/**
	 * indicates whether or not the threads used by this object have been killed.
	 * @return see above.
	 */
	public boolean isDead(){
		return kill;
	}
	
	/**
	 * @return the player represented by this communicator
	 */
	public Player getPlayer(){
		return player;
	}
	
	/**
	 * Puts a MovementPacket into the queue to be sent whenever able.
	 * @param packet the packet to be sent across the network.
	 */
	public void sendPacket(MovementPacket packet){
		try {
			outQueue.put(packet);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}