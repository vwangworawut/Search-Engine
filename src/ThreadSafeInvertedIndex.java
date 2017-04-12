import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadSafeInvertedIndex extends InvertedIndex {
	
	private ReadWriteLock lock;
	//private static final Logger logger = LogManager.getRootLogger();

	public ThreadSafeInvertedIndex() {
		super();
		this.lock = new ReadWriteLock();
	}
	
	public void add(String word, String path, int position) {
		lock.lockReadWrite();
		try {
			super.add(word, path, position);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	public boolean containsWord(String word) {
		lock.lockReadOnly();
		try {
			return super.containsWord(word);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	public int size() {
		lock.lockReadOnly();
		try {
			return super.size();
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	public boolean containsFile(String word, String file) {
		lock.lockReadOnly();
		try {
			return super.containsFile(word, file);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	public void toJSON(Path output) {
		lock.lockReadOnly();
		try {
			super.toJSON(output);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	public void addAll(InvertedIndex index) {
		lock.lockReadWrite();
		try {
			super.addAll(index);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	public ArrayList<QueryObject> partialSearch(String[] queryWords) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(queryWords);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	public ArrayList<QueryObject> exactSearch(String[] queryWords) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(queryWords);
		} finally {
			lock.unlockReadOnly();
		}
	}
}
