package cz.sevrjukov.tictactoe.ai;

public class Logger {

	private static final boolean ENABLED = true;
	//private static final boolean ENABLED = false;
	
	

	//private static final boolean DEBUG = true;
	private static final boolean DEBUG = true;
	
	public static synchronized void info(String message) {
		if (ENABLED) System.out.println(message);
	}
	
	public static synchronized void info (Object o) {
		info(o.toString());
	}
	
	
	public static synchronized void debug(String message) {
		if (DEBUG) System.out.println(message);
	}
	
	public static synchronized void debug (Object o) {
		info(o.toString());
	}
	
	
	
}
