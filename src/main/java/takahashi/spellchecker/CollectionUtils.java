/**
 * CollectionUtils.java
 */
package takahashi.spellchecker;

import java.util.Collection;

/**
 * CollectionUtils
 * 
 * @author TAKAHASHI Ryosuke
 */
public class CollectionUtils {
	/**
	 * Constructor.
	 */
	public CollectionUtils() {
		super();
	}

	public static boolean isEmpty(Collection<?> c) {
		return (c == null || c.isEmpty());
	}
}
