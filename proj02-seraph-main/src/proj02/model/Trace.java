package proj02.model;

/**
 * Manage tracing for Pippin simulations.
 * @author cs140
 */
public class Trace {

	/**
	 * No constructor required - all fields and methods static.
	 */
	private Trace() {} // Make it private to keep it out of javadoc!
	private static boolean trace=false;

	/**
	 * Start tracing.
	 */
	static public void startTrace() { trace=true; }
	/**
	 * Stop tracing.
	 */
	static public void stopTrace() { trace=false; }

	/**
	 * Print the parameter message if tracing.
	 * @param msg message to print
	 */
	public static void message(String msg) {
		if (trace) System.out.println("Trace: " + msg);
	}

	/**
	 * Is tracing on?
	 * @return true if tracing, false if not
	 */
	public static boolean getTrace() { return trace; }

}
