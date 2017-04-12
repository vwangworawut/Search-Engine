import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class WebCrawler {

	private final LinkedList<String> queue;
	private final HashSet<String> unique;
	private int counter;
	private final InvertedIndex index;
	

	public WebCrawler(InvertedIndex index) {
		this.index = index;
		queue = new LinkedList<>();
		unique = new HashSet<>();
		counter = 0;
	}

	public void startCrawl(String seed, InvertedIndex index) {
		queue.add(seed);
		unique.add(seed);
		while (counter != 50 && !queue.isEmpty()) {
			try {
				crawl(queue.removeFirst(), index);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public void crawl(String seed, InvertedIndex index) throws MalformedURLException {

		String head = seed;
		counter++;

		String html = HTMLCleaner.fetchHTML(head);

		ArrayList<String> links = LinkParser.listLinks(html);
		
		
		String words[] = HTMLCleaner.parseWords(HTMLCleaner.cleanHTML(html));
		
		for (int i = 0; i < links.size(); i++) {
			if (counter >= 50) {
				break;
			} else {
				if (!links.get(i).startsWith("http")) {
					URL base = new URL(head);
					URL absolute = new URL(base, links.get(i));
					String link = absolute.getProtocol() + "://" + absolute.getHost() + absolute.getFile();
					links.set(i, link);
				}
				
				if (!unique.contains(links.get(i)) && unique.size() <= 50) {
					queue.add(links.get(i));
					unique.add(links.get(i));
				}
			}
		}
		for (int i = 0; i < words.length; i++) {
			URL url = new URL(head);
			index.add(words[i], url.toString(), i + 1);
		}
	}
	
	public InvertedIndex getIndex() {
		return this.index;
	}
}