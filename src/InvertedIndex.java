import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	public InvertedIndex() {
		index = new TreeMap<>();
	}

	/**
	 * TODO: Fill this in (type /** and Enter in Eclipse before a method to get
	 * a template
	 * 
	 * @param word
	 *            word in text
	 * @param path
	 *            path of file
	 * @param position
	 *            positions of word in text
	 */
	public void add(String word, String path, int position) {
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<String, TreeSet<Integer>>());
		}

		if (!index.get(word).containsKey(path)) {
			TreeSet<Integer> set = new TreeSet<>();
			index.get(word).put(path, set);
		}

		index.get(word).get(path).add(position);
	}

	/**
	 * 
	 * @param index2
	 *            index to add all elements from
	 */
	public void addAll(InvertedIndex index2) {
		for (String key : index2.index.keySet()) {
			if (!index.containsKey(key)) {
				index.put(key, index2.index.get(key));
			} else {
				for (String path : index2.index.get(key).keySet()) {
					if (!index.get(key).containsKey(path)) {
						index.get(key).put(path, index2.index.get(key).get(path));
					} else {
						index.get(key).get(path).addAll(index2.index.get(key).get(path));
					}
				}
			}
		}
	}

	/**
	 * @return returns key value pairs
	 */
	public String toString() {
		return index.toString();
	}

	/**
	 * 
	 * @param word
	 *            to check if word is in index
	 * @return true or false
	 */
	public boolean containsWord(String word) {
		if (index.containsKey(word)) {
			return true;
		}
		return false;
	}

	/*
	 * @return size of index
	 */
	public int size() {
		return index.size();
	}

	/**
	 * 
	 * @param word
	 *            key value that contains file
	 * @param file
	 *            file name to check
	 * @return true or false if the word is in index
	 */
	public boolean containsFile(String word, String file) {
		return index.get(word).containsKey(file);
	}

	/**
	 * 
	 * @param output
	 *            location to write to
	 */
	public void toJSON(Path output) {
		IndexWriter.output(output, index);
	}

	/**
	 * 
	 * @param queryWords
	 *            words to perform partial search with
	 * @return results from searching with query words
	 */
	public ArrayList<QueryObject> partialSearch(String[] queryWords) {
		HashMap<String, QueryObject> visited = new HashMap<>();

		for (String qWord : queryWords) {

			for (String word : index.tailMap(qWord).keySet()) {
				if (word.startsWith(qWord)) {
					for (String path : index.get(word).keySet()) {
						if (visited.containsKey(path)) {
							visited.get(path).addFrequency(index.get(word).get(path).size());
							visited.get(path).setPosition(index.get(word).get(path).first());
						} else {
							QueryObject o = new QueryObject(path, index.get(word).get(path).size(),
									index.get(word).get(path).first());
							visited.put(path, o);
						}
					}
				} else {
					break;
				}
			}
		}
		ArrayList<QueryObject> searchResults = new ArrayList<>(visited.values());
		Collections.sort(searchResults);
		return searchResults;
	}
	
	/**
	 * 
	 * @param queryWords
	 *            words to perform partial search with
	 * @return results from searching with query words
	 */
	public ArrayList<QueryObject> exactSearch(String[] queryWords) {
		HashMap<String, QueryObject> visited = new HashMap<>();

		for (String qWord : queryWords) {

			if (index.containsKey(qWord))
				for (String path : index.get(qWord).keySet()) {
					if (visited.containsKey(path)) {
						visited.get(path).addFrequency(index.get(qWord).get(path).size());
						visited.get(path).setPosition(index.get(qWord).get(path).first());
					} else {
						QueryObject o = new QueryObject(path, index.get(qWord).get(path).size(),
								index.get(qWord).get(path).first());
						visited.put(path, o);
					}
				}
		}

		ArrayList<QueryObject> searchResults = new ArrayList<>(visited.values());
		Collections.sort(searchResults);
		return searchResults;
	}
}
