/**
 * MapUtils.java
 */
package takahashi.spellchecker;

import java.util.Map;

/**
 * MapUtils
 * 
 * @author TAKAHASHI Ryosuke
 */
public class MapUtils {
	/**
	 * Constructor.
	 */
	public MapUtils() {
		super();
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}
}
