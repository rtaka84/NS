/**
 * InputFileCreater.java
 */
package takahashi.spellchecker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

import takahashi.spellchecker.Log;

/**
 * InputFileCreater
 * @author TAKAHASHI Ryosuke
 */
public class InputFileCreater {
	/**
	 * Constructor.
	 */
	public InputFileCreater() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final long maxFileSize = 107374182400L; // 100GB
			final File file = new File("bar.txt");
			final BufferedWriter bw = new BufferedWriter(new FileWriter(file), 512 * 1024);
			for (int i = 0; file.length() < maxFileSize; i++) {
				bw.write(UUID.randomUUID().toString());
				bw.write("\n");
				if (i % 1000000 == 0) {
					Log.info("InputFileCreater", "main", "file.length=" + file.length());
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
