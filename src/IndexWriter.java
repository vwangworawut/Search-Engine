import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class IndexWriter {

	/** Tab character used for pretty JSON output. */
	public static final char TAB = '\t';

	/** End of line character used for pretty JSON output. */
	public static final char END = '\n';

	/**
	 * 
	 * @param text
	 *            text to wrap in " "
	 * @return text in quotes
	 */
	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	public static String tab(int n) {
		char[] tabs = new char[n];
		Arrays.fill(tabs, TAB);
		return String.valueOf(tabs);
	}

	/**
	 * 
	 * @param path
	 *            path to write to
	 * @param index
	 *            Treemap with word, path and positions
	 * @return
	 */
	public static boolean output(Path path, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index) {

		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF8"))) {
			writer.write('{');
			writer.write(END);
			if (!index.isEmpty()) {
				String tab1 = tab(1);
				String tab2 = tab(2);
				String tab3 = tab(3);
				for (String s : index.keySet()) {
					writer.write(tab1 + quote(s) + ": {" + END);
					if (!index.get(s).isEmpty()) {
						String last = index.get(s).lastKey();

						for (String file : index.get(s).keySet()) {
							writer.write(tab2 + quote(file.toString()) + ": [" + END);
							int last_pos = index.get(s).get(file).last();

							for (int pos : index.get(s).get(file)) {
								if (pos == last_pos) {
									writer.write(tab3 + Integer.toString(pos) + END);
								} else {
									writer.write(tab3 + Integer.toString(pos) + "," + END);
								}
							}

							if (file.equals(last)) {
								writer.write(tab2 + "]" + END);
							} else {
								writer.write(tab2 + "]," + END);
							}

						}
						if (index.lastKey().equals(s)) {
							writer.write(tab1 + "}" + END);
						} else {
							writer.write(tab1 + "}," + END);
						}
					}
				}
			}
			writer.write('}');
			writer.write(END);
			writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param path
	 * file to write to
	 * @param query
	 * list of query objects
	 * @return true or false if successful or not
	 */
	public static boolean queryOutput(Path path, TreeMap<String, List<QueryObject>> query) {
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF8"))) {
			writer.write('{');
			writer.write(END);
			if (!query.isEmpty()) {
				String tab1 = tab(1);
				String tab2 = tab(2);
				String tab3 = tab(3);

				for (String word : query.keySet()) {
					writer.write(tab1 + quote(word) + ": [" + END);
					String last = query.lastKey();

					if (!query.get(word).isEmpty()) {
						List<QueryObject> o = query.get(word);

						for (int i = 0; i < o.size(); i++) {
							writer.write(tab2 + "{" + END);
							int lastPos = o.size() - 1;
							writer.write(tab3 + quote("where") + ": " + quote(o.get(i).getLocation().toString()) + ","
									+ END);
							writer.write(tab3 + quote("count") + ": " + Integer.toString(o.get(i).getFrequency()) + ","
									+ END);
							writer.write(tab3 + quote("index") + ": " + Integer.toString(o.get(i).getPosition()) + END);

							if (i == lastPos) {
								writer.write(tab2 + "}" + END);
							} else {
								writer.write(tab2 + "}," + END);
							}
						}
					}
					if (word.equals(last)) {
						writer.write(tab1 + "]" + END);
					} else {
						writer.write(tab1 + "]," + END);
					}
				}
			}
			writer.write('}');
			writer.write(END);
			writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
