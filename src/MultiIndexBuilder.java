import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO Add javadoc for all classes and methods.
public class MultiIndexBuilder extends InvertedIndexBuilder {

	private final WorkQueue workers;

	private final ThreadSafeInvertedIndex index;
	public static final String TEXTEXTENSION = ".txt";

	public MultiIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		super(index);
		this.index = index;
		this.workers = queue;
	}

	
	public void buildIndex(Path directory) {
		traverse(directory, TEXTEXTENSION);
		workers.finish();
	}
	
	private void traverse(Path path, String suffix) {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			for (Path file : listing) {
				if (Files.isDirectory(file)) {
					traverse(file, suffix);
				}
				else {
					if (file.toString().toLowerCase().endsWith(suffix)) {
						workers.execute(new Minion(file.toString()));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class Minion implements Runnable {
		private final String path;
		private final InvertedIndex localIndex;
		
		public Minion(String path) {
			this.path = path;
			this.localIndex = new InvertedIndex();
		}
		
		@Override
		public void run() {
			InvertedIndexBuilder.buildIndexHelper(path, localIndex);
			index.addAll(localIndex);
		}
	}
}