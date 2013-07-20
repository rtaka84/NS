/**
 * Log.java
 */
package takahashi.spellchecker;

import java.util.logging.Level;

/**
 * Log
 * 
 * @author TAKAHASHI Ryosuke
 */
public abstract class Log {
	/** ƒƒOƒŒƒxƒ‹ */
	private final static int level = Level.INFO.intValue();

	/**
	 * Constructor.
	 */
	public Log() {
		super();
	}

	public static void error(String clazz, String method, Object message) {
		if (level > Level.SEVERE.intValue()) {
			return;
		}
		System.err.println("[ERROR][" + clazz + "][" + Thread.currentThread().getName() + "]"
				+ method + ": " + message);
	}

	public static void warn(String clazz, String method, Object message) {
		if (level > Level.WARNING.intValue()) {
			return;
		}
		System.err.println("[WARN][" + clazz + "][" + Thread.currentThread().getName() + "]"
				+ method + ": " + message);
	}

	public static void info(String clazz, String method, Object message) {
		if (level > Level.INFO.intValue()) {
			return;
		}
		System.out.println("[INFO][" + clazz + "][" + Thread.currentThread().getName() + "]"
				+ method + ": " + message);
	}

	public static void debug(String clazz, String method, Object message) {
		if (level > Level.FINE.intValue()) {
			return;
		}
		System.out.println("[DEBUG][" + clazz + "][" + Thread.currentThread().getName() + "]"
				+ method + ": " + message);
	}
}
