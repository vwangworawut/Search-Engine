import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class MultiQueryHelper extends QueryHelper {

	private final TreeMap<String, List<QueryObject>> query;
	private final ThreadSafeInvertedIndex index;
	private final WorkQueue workers;

	public MultiQueryHelper(ThreadSafeInvertedIndex index, WorkQueue workers) {
		super(index);
		this.index = index;
		this.query = new TreeMap<>();
		this.workers = workers;
	}

	/**
	 * 
	 * @param path
	 *            File you're getting the query line from
	 * @param search
	 *            if true, search is exact search. If false, partial search
	 */
	public void parseQuery(Path path, boolean search) {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;
			while ((line = reader.readLine()) != null) {
				workers.execute(new Minion(line, search));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		workers.finish();
	}

	/**
	 * 
	 * @param line
	 *            line being parsed
	 * @param search
	 *            if true, search is exact search. If false, partial search
	 */
	public void parseLine(String line, boolean search) {
		line = cleanLine(line);
		String sortedLine = "";
		String[] split = line.trim().split("\\s+");
		Arrays.sort(split);
		for (String s : split) {
			if (s.equals(split[split.length - 1])) {
				sortedLine += s;
			} else {
				sortedLine += s + " ";
			}
		}

		ArrayList<QueryObject> searchList = new ArrayList<>();

		if (search == false) {
			searchList = index.partialSearch(split);
		} else {
			searchList = index.exactSearch(split);
		}

		synchronized (query) {
			query.put(sortedLine, searchList);
		}
	}

	/**
	 * 
	 * @param path
	 *            path to write file to
	 */
	public void toJSON(Path path) {
		synchronized (query) {
			IndexWriter.queryOutput(path, query);
		}
	}

	private class Minion implements Runnable {

		private final String queryLine;
		private final boolean search;

		/**
		 * 
		 * @param queryLine
		 *            line to parse and search from
		 * @param search
		 *            if true, search is exact search. If false, partial search
		 * 
		 */
		public Minion(String queryLine, Boolean search) {
			this.queryLine = queryLine;
			this.search = search;
		}

		@Override
		public void run() {
			parseLine(queryLine, search);
		}
	}
}
