/**
 * StringUtils.java
 */
package takahashi.spellchecker;

/**
 * StringUtils
 * 
 * @author TAKAHASHI Ryosuke
 */
public abstract class StringUtils {
	/**
	 * Constructor.
	 */
	public StringUtils() {
		super();
	}

	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}
}
