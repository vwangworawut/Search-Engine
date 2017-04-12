import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class MultiWebCrawler extends WebCrawler {

	private final WorkQueue workers;
	private final HashSet<String> unique;
	private ThreadSafeInvertedIndex index;

	private volatile int limit;

	/**
	 * 
	 * @param index
	 *            index to add to
	 * @param workers
	 *            Work queue for minions
	 */
	public MultiWebCrawler(ThreadSafeInvertedIndex index, WorkQueue workers) {
		super(index);
		this.index = index;
		this.workers = workers;
		this.unique = new HashSet<>();
	}

	/**
	 * 
	 * @param seed
	 *            link to start crawling
	 * @param index
	 *            index to add to
	 */
	public void startCrawl(String seed, ThreadSafeInvertedIndex index) {
		limit += 50;
		unique.add(seed);
		workers.execute(new minion(seed, index));
		workers.finish();
	}
	
	public ThreadSafeInvertedIndex getIndex() {
		return this.index;
	}

	private class minion implements Runnable {

		private final String link;
		private final ThreadSafeInvertedIndex index;
		private final ThreadSafeInvertedIndex localIndex;

		/**
		 * 
		 * @param link
		 *            link to crawl
		 * @param index
		 *            index to add to
		 */
		public minion(String link, ThreadSafeInvertedIndex index) {
			this.link = link;
			this.index = index;
			this.localIndex = new ThreadSafeInvertedIndex();
		}

		@Override
		public void run() {
			String head = link;

			String html = HTMLCleaner.fetchHTML(head);

			ArrayList<String> links = LinkParser.listLinks(html);

			String words[] = HTMLCleaner.parseWords(HTMLCleaner.cleanHTML(html));

			for (int i = 0; i < links.size(); i++) {
				if (limit == 0) {
					break;
				} else {
					if (!links.get(i).startsWith("http")) {
						URL base;
						try {
							base = new URL(head);
							URL absolute = new URL(base, links.get(i));
							String link = absolute.getProtocol() + "://" + absolute.getHost() + absolute.getFile();
							links.set(i, link);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}

					synchronized (unique) {
						if (!unique.contains(links.get(i)) && limit > 0) {
							limit--;
							unique.add(links.get(i));
							workers.execute(new minion(links.get(i), index));
						}
					}
				}
			}
			for (int i = 0; i < words.length; i++) {
				try {
					URL url = new URL(head);
					localIndex.add(words[i], url.toString(), i + 1);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			index.addAll(localIndex);
		}
	}
}