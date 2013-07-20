/**
 * ArrayUtils.java
 */
package takahashi.spellchecker;

/**
 * ArrayUtils
 * 
 * @author TAKAHASHI Ryosuke
 */
public class ArrayUtils {
	/**
	 * Constructor.
	 */
	public ArrayUtils() {
		super();
	}

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}
}
