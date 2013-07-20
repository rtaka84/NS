/**
 * Dictionary.java
 */
package takahashi.spellchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Dictionary
 * 
 * @author TAKAHASHI Ryosuke
 */
public class Dictionary {
	/** キャッシュ */
	private final ConcurrentSkipListMap<Integer, Set<String>> wordsCache = new ConcurrentSkipListMap<Integer, Set<String>>();

	/**
	 * Constructor.
	 */
	public Dictionary() {
		super();
	}

	/**
	 * テスト用
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final Dictionary dictionary = new Dictionary();
		dictionary.load(ClassLoader.getSystemResource("words"));
		final Map<Integer, Set<String>> resultWordsMap = dictionary.search("mango");
		Log.info("Dictionary", "main", "resultWordsMap=" + resultWordsMap);
	}

	/**
	 * ファイルから単語をロードします。
	 * 
	 * @param filePath ファイルのパス。
	 */
	public void load(URL filePath) {
		try {
			load(new File(filePath.toURI()));
		} catch (Exception e) {
			Log.error(getClass().getSimpleName(), "load", "Cannot close " + filePath + ".");
			ExceptionUtils.throwRuntimeException(e);
		}
	}

	/**
	 * ファイルから単語をロードします。
	 * 
	 * @param filePath ファイルのパス。
	 */
	public void load(String filePath) {
		load(new File(filePath));
	}

	/**
	 * ファイルから単語をロードします。
	 * 
	 * @param file ファイル。
	 */
	public void load(File file) {
		try {
			load(new FileReader(file));
		} catch (IOException e) {
			ExceptionUtils.throwRuntimeException(e);
		}
	}

	/**
	 * 入力ストリームから単語をロードします。
	 * 
	 * @param isr 入力ストリーム。
	 */
	public void load(InputStreamReader isr) {
		final long start = System.currentTimeMillis();
		try {
			final BufferedReader br = new BufferedReader(isr);
			for (int i = 1; br.ready(); i++) {
				final String word = br.readLine();
				Log.debug(getClass().getSimpleName(), "load", "Line" + i + "=" + word);
				if (StringUtils.isEmpty(word)) {
					Log.error(getClass().getSimpleName(), "load", "Line" + i + " is empty.");
					continue;
				}
				final Integer wordLength = word.length();
				Set<String> words = wordsCache.get(wordLength);
				if (words == null) {
					words = new LinkedHashSet<String>();
					wordsCache.put(wordLength, words);
				}
				words.add(word);
			}
			Log.debug(getClass().getSimpleName(), "load", "wordCache=" + wordsCache);
			br.close();
		} catch (IOException e) {
			ExceptionUtils.throwRuntimeException(e);
		} finally {
			final long end = System.currentTimeMillis();
			Log.info(getClass().getSimpleName(), "load", "Running time=" + (end - start));
		}
	}

	/**
	 * キーワード検索します。
	 * 
	 * @param keyword キーワード。
	 * @return 検索結果。
	 */
	@SuppressWarnings("unused")
	public Map<Integer, Set<String>> search(String keyword) {
		final long start = System.currentTimeMillis();
		final Map<Integer, Set<String>> hitWordsMap = new LinkedHashMap<Integer, Set<String>>();
		try {
			if (StringUtils.isEmpty(keyword)) {
				throw new IllegalArgumentException("Keyword(" + keyword + ") is empty.");
			}
			final Integer wordLength = keyword.length();
			final ConcurrentNavigableMap<Integer, Set<String>> wordsMap = wordsCache
					.tailMap(wordLength);
			if (MapUtils.isEmpty(wordsMap)) {
				return hitWordsMap;
			}
			final List<SearchCommand> searchCommands = new ArrayList<Dictionary.SearchCommand>(
					wordsMap.size());
			for (Map.Entry<Integer, Set<String>> wordsMapEntry : wordsMap.entrySet()) {
				searchCommands.add(new SearchCommand(wordsMapEntry.getKey(), wordsMapEntry
						.getValue(), keyword));
			}
			Log.debug(getClass().getSimpleName(), "search",
					"searchCommands.size=" + searchCommands.size());
			//TODO OutOfMemoryError 対策
			//final List<Future<Map<Integer, Set<String>>>> futures = Executors.newCachedThreadPool()
			//for (Future<Map<Integer, Set<String>>> future : futures) {
				//final Map<Integer, Set<String>> hitWordsSubMap = future.get();
			for (SearchCommand searchCommand : searchCommands) {
				final Map<Integer, Set<String>> hitWordsSubMap = searchCommand.call();
				if (!MapUtils.isEmpty(hitWordsSubMap)) {
					hitWordsMap.putAll(hitWordsSubMap);
				}
			}
		} catch (Exception e) {
			ExceptionUtils.throwRuntimeException(e);
		} finally {
			final long end = System.currentTimeMillis();
			//Log.info(getClass().getSimpleName(), "search", "Running time=" + (end - start));
		}
		return hitWordsMap;
	}

	/**
	 * SearchCommand
	 * 
	 * @author TAKAHASHI Ryosuke
	 */
	private static class SearchCommand implements Callable<Map<Integer, Set<String>>> {
		private final Integer wordLength;
		private final Set<String> words;
		private final String keyword;

		/**
		 * Constructor.
		 * 
		 * @param wordLength
		 * @param words
		 * @param keyword
		 */
		public SearchCommand(Integer wordLength, Set<String> words, String keyword) {
			super();
			this.wordLength = wordLength;
			this.words = words;
			this.keyword = keyword.toLowerCase();
		}

		/**
		 * {@inheritDoc}
		 */
		public Map<Integer, Set<String>> call() throws Exception {
			//TODO OutOfMemoryError 対策
			if (Runtime.getRuntime().freeMemory() < 33554432) { // メモリの空き容量が32MB以下になった場合
				System.gc();
			}
			if (CollectionUtils.isEmpty(words)) {
				return Collections.emptyMap();
			}
			final Set<String> hitWords = new LinkedHashSet<String>();
			for (String word : words) {
				if (word.toLowerCase().contains(keyword)) {
					hitWords.add(word);
				}
			}
			if (CollectionUtils.isEmpty(hitWords)) {
				return Collections.emptyMap();
			}
			final Map<Integer, Set<String>> hitWordsMap = new LinkedHashMap<Integer, Set<String>>();
			hitWordsMap.put(wordLength, hitWords);
			return Collections.unmodifiableMap(hitWordsMap);
		}
	}
}
