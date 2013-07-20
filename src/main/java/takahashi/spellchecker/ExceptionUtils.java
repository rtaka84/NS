/**
 * ExceptionUtils.java
 */
package takahashi.spellchecker;

/**
 * ExceptionUtils
 * @author TAKAHASHI Ryosuke
 */
public abstract class ExceptionUtils {
	/**
	 * Constructor.
	 */
	public ExceptionUtils() {
		super();
	}

	/**
	 * @param e
	 */
	public static void throwRuntimeException(Exception e) {
		if (e == null) {
			throw new IllegalArgumentException("Exception is null.");
		} else if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else {
			throw new RuntimeException(e);
		}
	}
}
