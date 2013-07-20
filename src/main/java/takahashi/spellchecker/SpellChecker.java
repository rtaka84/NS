/**
 * SpellChecker.java
 */
package takahashi.spellchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

/**
 * SpellChecker
 * 
 * @author TAKAHASHI Ryosuke
 */
public class SpellChecker {
	/** プログラム引数 */
	private final String[] args;
	/** 辞書 */
	private final Dictionary dictionary = new Dictionary();

	/**
	 * Constructor.
	 * 
	 * @param args プログラム引数。
	 */
	public SpellChecker(String[] args) {
		super();
		this.args = args;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Log.info("SpellChecker", "main", "----------------------------------------");
		Log.info("SpellChecker", "main", "Start.");
		int returnCode = 0;
		final long start = System.currentTimeMillis();
		try {
			final SpellChecker spellChecker = new SpellChecker(args);
			spellChecker.exec();
		} catch (Exception e) {
			Log.error("SpellChecker", "exec", "Error occurred.");
			e.printStackTrace();
			returnCode = -1;
		} finally {
			final long end = System.currentTimeMillis();
			Log.info("SpellChecker", "main", "End. Running time=" + (end - start));
			Log.info("SpellChecker", "main", "----------------------------------------");
		}
		System.exit(returnCode);
	}

	public void exec() {
		if (ArrayUtils.isEmpty(args)) {
			Log.warn(getClass().getSimpleName(), "exec", "One or more arguments must be specified.");
			Log.info(getClass().getSimpleName(), "exec",
					"Usage1: SpellChecker.exe (One or more keyword(s))");
			Log.info(getClass().getSimpleName(), "exec", "Usage2: SpellChecker.exe (A file path)");
			return;
		}
		final File dictionaryFile = new File("words");
		if (dictionaryFile.exists()) { // カレントディレクトリに words ファイルが存在する場合
			// カレントディレクトリの words ファイルをロードする。
			dictionary.load(dictionaryFile);
		} else {
			// クラスパスルートの words ファイルをロードする。
			dictionary.load(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(
					"words")));
			//dictionary.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("words"));
		}

		if (args.length == 1) {
			final File file = new File(args[0]);
			if (file.exists()) {
				FileReader fr;
				try {
					fr = new FileReader(file);
				} catch (FileNotFoundException e) {
					Log.error(getClass().getSimpleName(), "exec", "Cannot find or open " + args[0]
							+ ".");
					ExceptionUtils.throwRuntimeException(e);
					return;
				}
				final BufferedReader br = new BufferedReader(fr);
				try {
					for (int i = 1; br.ready(); i++) {
						final String keywordLine = br.readLine();
						final String[] keywords = keywordLine.split(" ");
						search(keywords, i);
					}
				} catch (IOException e) {
					Log.error(getClass().getSimpleName(), "exec", "Cannot read " + args[0] + ".");
					ExceptionUtils.throwRuntimeException(e);
					return;
				}
				try {
					br.close();
				} catch (IOException e) {
					Log.error(getClass().getSimpleName(), "exec", "Cannot close " + args[0] + ".");
					ExceptionUtils.throwRuntimeException(e);
					return;
				}
				return;
			}
		}
		search(args, 1);
	}

	private void search(String[] keywords, int lineNumber) {
		for (String keyword : keywords) {
			search(keyword, lineNumber);
		}
	}

	private void search(String keyword, int lineNumber) {
		final Map<Integer, Set<String>> hitWordsMap = dictionary.search(keyword);
		if (MapUtils.isEmpty(hitWordsMap)) {
			Log.info(getClass().getSimpleName(), "search", "Line" + lineNumber + ": " + keyword
					+ " (not in dictionary)");
			return;
		}
		final Set<String> matchWords = hitWordsMap.remove(keyword.length());
		if (!CollectionUtils.isEmpty(matchWords)) {
			Log.info(getClass().getSimpleName(), "search", "Line" + lineNumber + ": " + keyword
					+ " (match with " + matchWords + ")");
		}
		for (Map.Entry<Integer, Set<String>> hitWordsMapEntry : hitWordsMap.entrySet()) {
			Log.info(getClass().getSimpleName(), "search", "Line" + lineNumber + ": " + keyword
					+ " (diff by " + (hitWordsMapEntry.getKey() - keyword.length()) + " char from "
					+ hitWordsMapEntry.getValue() + ")");
		}
	}
}
