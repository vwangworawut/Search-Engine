import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class QueryHelper {

	private final TreeMap<String, List<QueryObject>> query;

	private final InvertedIndex index;

	public QueryHelper(InvertedIndex index) {
		this.query = new TreeMap<>();
		this.index = index;
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
				parseLine(line, search);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if (search == false) {
			query.put(sortedLine, index.partialSearch(split));
		}

		if (search == true) {
			query.put(sortedLine, index.exactSearch(split));
		}
	}

	/**
	 * 
	 * @param line
	 *            line to be cleaned
	 * @return cleaned line
	 */
	public static String cleanLine(String line) {
		return line.toLowerCase().replaceAll("\\p{Punct}+", "").trim();
	}

	/**
	 * 
	 * @param path
	 *            path to write file to
	 */
	public void toJSON(Path path) {
		IndexWriter.queryOutput(path, query);
	}
}
