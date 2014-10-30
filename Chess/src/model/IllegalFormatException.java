package model;

public class IllegalFormatException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	/**
	 * default constructor
	 */
	public IllegalFormatException(){
		super();
	}
	
	/**
	 * @param message a message detailing what went wrong
	 */
	public IllegalFormatException(String message){
		super(message);
	}
	
	/**
	 * @param cause the cause of this exception
	 */
	public IllegalFormatException(Throwable cause){
		super(cause);
	}
	
	/**
	 * 
	 * @param message a message detailing what went wrong
	 * @param cause the cause of this exception
	 */
	public IllegalFormatException(String message, Throwable cause){
		super(message, cause);
	}
}
